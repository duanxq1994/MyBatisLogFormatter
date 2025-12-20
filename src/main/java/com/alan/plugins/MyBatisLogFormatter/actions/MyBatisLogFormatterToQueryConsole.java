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
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.Messages;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;

import java.util.List;

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

        // 根据选定的数据源确定数据库类型
        LocalDataSource selectedDataSource = getSelectedDataSource(project);
        if (selectedDataSource == null) {
            return;
        }
        
        // 获取选定的 schema
        DasNamespace selectedNamespace = getSelectedNamespace(project, selectedDataSource);
        if (selectedNamespace == null) {
            return;
        }
        
        // 使用配置中的默认数据库类型
        final DatabaseType defaultDatabaseType = MyBatisLogFormatterSettings.getInstance().getDefaultDatabaseType();
        DatabaseType databaseType = DataSourceUtils.getDatabaseType(selectedDataSource, defaultDatabaseType);
        String sqlContent = SqlUtils.formatMybatisLog(selectedText, databaseType);
        
        if (StringUtils.isBlank(sqlContent)) {
            NotificationHelper.showWarningNotification(project, I18nBundle.message("label.notice.not.sql.statement"));
            return;
        }
        OpenQueryConsoleActionHandler.openQueryConsoleWithClipboardSql(project, selectedDataSource, selectedNamespace, SqlUtils.beautifySql(sqlContent));
    }

    /**
     * 获取选定的 schema/namespace
     * 如果只有一个 schema，直接使用；如果有多个，提示用户选择
     */
    private DasNamespace getSelectedNamespace(Project project, @NotNull LocalDataSource dataSource) {
        try {
            List<DasNamespace> namespaces = DataSourceUtils.getAvailableNamespaces(dataSource);

            // 如果只有一个 schema，直接使用
            if (namespaces.size() == 1) {
                return namespaces.get(0);
            }
            
            // 如果有多个，提示用户选择
            String[] namespaceNames = namespaces.stream()
                    .map(ns -> {
                        try {
                            return ns.getName();
                        } catch (Exception e) {
                            return ns.toString();
                        }
                    })
                    .toArray(String[]::new);
            
            int selectedIndex = Messages.showChooseDialog(
                    I18nBundle.message("label.select.schema"),
                    I18nBundle.message("label.select.schema"),
                    namespaceNames,
                    namespaceNames[0],
                    Messages.getQuestionIcon()
            );
            
            if (selectedIndex != -1 && selectedIndex < namespaces.size()) {
                return namespaces.get(selectedIndex);
            }
        } catch (Exception e) {
            // 如果获取失败，尝试返回默认的 root namespace
            try {
                return dataSource.getModel().getCurrentRootNamespace();
            } catch (Exception ex) {
                NotificationHelper.showErrorNotification(project, "Failed to get namespace: " + e.getMessage());
            }
        }
        return null;
    }

    /**
     * 获取选定的数据源
     * 如果只有一个数据源，直接使用；如果有多个，提示用户选择
     */
    private LocalDataSource getSelectedDataSource(Project project) {
        try {
            if (project == null) {
                NotificationHelper.showErrorNotification(null, I18nBundle.message("label.project.not.found"));
                return null;
            }
            // 获取 DatabaseView 实例
            DatabaseView databaseView = DatabaseView.getDatabaseView(project);
            if (databaseView == null) {
                NotificationHelper.showErrorNotification(project, I18nBundle.message("label.notFound.database.plugin"));
                return null;
            }

            DataSourceManager<LocalDataSource> dataSourceManager = DataSourceManager.byDataSource(project, LocalDataSource.class);
            if (dataSourceManager == null) {
                NotificationHelper.showErrorNotification(project, I18nBundle.message("label.DataSourceManager.notFond"));
                return null;
            }
            List<LocalDataSource> dataSources = dataSourceManager.getDataSources();
            if (dataSources.isEmpty()) {
                NotificationHelper.showErrorNotification(project, I18nBundle.message("label.dataSource.noDataSources"));
                return null;
            }
            // 如果只有一个数据源，直接使用
            if (dataSources.size() == 1) {
                return dataSources.get(0);
            }
            // 如果有多个，提示用户选择
            String[] dataSourceNames = dataSources.stream().map(LocalDataSource::getName).toArray(String[]::new);
            int selectedIndex = Messages.showChooseDialog(
                    I18nBundle.message("label.select.dataSource"),
                    I18nBundle.message("label.open.queryConsole"),
                    dataSourceNames,
                    dataSourceNames[0],
                    Messages.getQuestionIcon()
            );
            if (selectedIndex != -1) {
                return dataSources.get(selectedIndex);
            }
        } catch (Exception e) {
            // 如果获取失败，返回 null
            NotificationHelper.showErrorNotification(project, e.getMessage());
        }
        return null;
    }
}
