package com.alan.plugins.MyBatisLogFormatter.actions;

import com.alan.plugins.MyBatisLogFormatter.config.MyBatisLogFormatterSettings;
import com.alan.plugins.MyBatisLogFormatter.handler.OpenQueryConsoleActionHandler;
import com.alan.plugins.MyBatisLogFormatter.i18n.I18nBundle;
import com.alan.plugins.MyBatisLogFormatter.notice.NotificationHelper;
import com.alan.plugins.MyBatisLogFormatter.utils.DataSourceUtils;
import com.alan.plugins.MyBatisLogFormatter.utils.DatabaseType;
import com.alan.plugins.MyBatisLogFormatter.utils.SqlUtils;
import com.intellij.database.dataSource.LocalDataSource;
import com.intellij.database.model.DasNamespace;
import com.intellij.database.psi.DataSourceManager;
import com.intellij.database.view.DatabaseView;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.popup.JBPopupFactory;
import com.intellij.openapi.ui.popup.JBPopup;
import com.intellij.ui.ListCellRendererWrapper;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;

import javax.swing.JList;
import java.util.List;
import java.util.function.Consumer;

/**
 * @author Alan
 */
public class MyBatisLogFormatterToQueryConsole extends AnAction {
    @Override
    public void actionPerformed(@NotNull AnActionEvent event) {
        Project project = event.getProject();
        if (project == null) {
            return;
        }
        // 获得选中的文本
        String selectedText = event.getRequiredData(CommonDataKeys.EDITOR).getSelectionModel().getSelectedText();
        if (StringUtils.isBlank(selectedText)) {
            NotificationHelper.showWarningNotification(project, I18nBundle.message("label.select.myBatisLog"));
            return;
        }

        chooseDataSource(event, project, selectedDataSource ->
                chooseNamespace(event, project, selectedDataSource, selectedNamespace -> {
                    // 使用配置中的默认数据库类型
                    final DatabaseType defaultDatabaseType = MyBatisLogFormatterSettings.getInstance().getDefaultDatabaseType();
                    DatabaseType databaseType = DataSourceUtils.getDatabaseType(selectedDataSource, defaultDatabaseType);
                    String sqlContent = SqlUtils.formatMybatisLog(selectedText, databaseType);

                    if (StringUtils.isBlank(sqlContent)) {
                        NotificationHelper.showWarningNotification(project, I18nBundle.message("label.notice.not.sql.statement"));
                        return;
                    }
                    OpenQueryConsoleActionHandler.openQueryConsoleWithClipboardSql(
                            project,
                            selectedDataSource,
                            selectedNamespace,
                            SqlUtils.beautifySql(sqlContent)
                    );
                })
        );
    }

    /**
     * 选择 schema/namespace
     * - 只有一个：直接回调
     * - 多个：弹出下拉列表，选择后立即回调（无确定/取消按钮）
     */
    private void chooseNamespace(@NotNull AnActionEvent event,
                                 @NotNull Project project,
                                 @NotNull LocalDataSource dataSource,
                                 @NotNull Consumer<DasNamespace> onChosen) {
        try {
            List<DasNamespace> namespaces = DataSourceUtils.getAvailableNamespaces(dataSource);

            // 如果只有一个 schema，直接使用
            if (namespaces.size() == 1) {
                onChosen.accept(namespaces.get(0));
                return;
            }

            if (namespaces.isEmpty()) {
                DasNamespace root = dataSource.getModel().getCurrentRootNamespace();
                if (root != null) {
                    onChosen.accept(root);
                    return;
                }
                NotificationHelper.showErrorNotification(project, "No schema/namespace found.");
                return;
            }

            JBPopup popup = JBPopupFactory.getInstance()
                    .createPopupChooserBuilder(namespaces)
                    .setTitle(I18nBundle.message("label.select.schema"))
                    // 兼容不同 IntelliJ Platform 版本：通过 Namer + SpeedSearch 实现输入快速定位
                    .setNamerForFiltering(MyBatisLogFormatterToQueryConsole::getNamespaceDisplayName)
                    .setRenderer(new ListCellRendererWrapper<>() {
                        @Override
                        public void customize(JList list, DasNamespace value, int index, boolean selected, boolean hasFocus) {
                            setText(getNamespaceDisplayName(value));
                        }
                    })
                    .setItemChosenCallback(chosen -> {
                        if (chosen != null) {
                            onChosen.accept(chosen);
                        }
                    })
                    .createPopup();
            showPopupAtBestPosition(event, popup);
        } catch (Exception e) {
            // 如果获取失败，尝试返回默认的 root namespace
            try {
                DasNamespace root = dataSource.getModel().getCurrentRootNamespace();
                if (root != null) {
                    onChosen.accept(root);
                }
            } catch (Exception ex) {
                NotificationHelper.showErrorNotification(project, "Failed to get namespace: " + e.getMessage());
            }
        }
    }

    /**
     * 选择数据源
     * - 只有一个：直接回调
     * - 多个：弹出下拉列表，选择后立即回调（无确定/取消按钮）
     */
    private void chooseDataSource(@NotNull AnActionEvent event,
                                  @NotNull Project project,
                                  @NotNull Consumer<LocalDataSource> onChosen) {
        try {
            // 获取 DatabaseView 实例
            DatabaseView databaseView = DatabaseView.getDatabaseView(project);
            if (databaseView == null) {
                NotificationHelper.showErrorNotification(project, I18nBundle.message("label.notFound.database.plugin"));
                return;
            }

            DataSourceManager<LocalDataSource> dataSourceManager = DataSourceManager.byDataSource(project, LocalDataSource.class);
            if (dataSourceManager == null) {
                NotificationHelper.showErrorNotification(project, I18nBundle.message("label.DataSourceManager.notFond"));
                return;
            }
            List<LocalDataSource> dataSources = dataSourceManager.getDataSources();
            if (dataSources.isEmpty()) {
                NotificationHelper.showErrorNotification(project, I18nBundle.message("label.dataSource.noDataSources"));
                return;
            }
            // 如果只有一个数据源，直接使用
            if (dataSources.size() == 1) {
                onChosen.accept(dataSources.get(0));
                return;
            }

            JBPopup popup = JBPopupFactory.getInstance()
                    .createPopupChooserBuilder(dataSources)
                    .setTitle(I18nBundle.message("label.select.dataSource"))
                    // 兼容不同 IntelliJ Platform 版本：通过 Namer + SpeedSearch 实现输入快速定位
                    .setNamerForFiltering(ds -> ds == null ? "" : ds.getName())
                    .setRenderer(new ListCellRendererWrapper<>() {
                        @Override
                        public void customize(JList list, LocalDataSource value, int index, boolean selected, boolean hasFocus) {
                            setText(value == null ? "" : value.getName());
                        }
                    })
                    .setItemChosenCallback(chosen -> {
                        if (chosen != null) {
                            onChosen.accept(chosen);
                        }
                    })
                    .createPopup();
            showPopupAtBestPosition(event, popup);
        } catch (Exception e) {
            // 如果获取失败，返回 null
            NotificationHelper.showErrorNotification(project, e.getMessage());
        }
    }

    private static void showPopupAtBestPosition(@NotNull AnActionEvent event, @NotNull JBPopup popup) {
        Editor editor = event.getData(CommonDataKeys.EDITOR);
        if (editor != null) {
            popup.show(JBPopupFactory.getInstance().guessBestPopupLocation(editor));
            return;
        }
        popup.showInBestPositionFor(event.getDataContext());
    }

    private static String getNamespaceDisplayName(DasNamespace value) {
        if (value == null) {
            return "";
        }
        try {
            return value.getName();
        } catch (Exception e) {
            return String.valueOf(value);
        }
    }
}
