package com.alan.plugins.MyBatisLogFormatter.handler;

import com.alan.plugins.MyBatisLogFormatter.i18n.I18nBundle;
import com.alan.plugins.MyBatisLogFormatter.notice.NotificationHelper;
import com.intellij.database.dataSource.LocalDataSource;
import com.intellij.database.editor.DatabaseEditorHelper;
import com.intellij.database.psi.DataSourceManager;
import com.intellij.database.view.DatabaseView;
import com.intellij.openapi.application.Application;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.fileEditor.FileDocumentManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.Messages;
import com.intellij.openapi.vfs.VirtualFile;

import java.util.List;

/**
 * @author Alan
 */
public class OpenQueryConsoleActionHandler {

    public static void openQueryConsoleWithClipboardSql(Project project, String sqlContent) {
        if (project == null) {
            NotificationHelper.showErrorNotification(null, I18nBundle.message("label.project.not.found"));
            return;
        }
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
        // 如果只有一个数据源，直接使用；如果有多个，提示用户选择
        LocalDataSource selectedDataSource = null;
        if (dataSources.size() == 1) {
            selectedDataSource = dataSources.get(0);
        } else {
            String[] dataSourceNames = dataSources.stream().map(LocalDataSource::getName).toArray(String[]::new);
            int selectedIndex = Messages.showChooseDialog(
                    I18nBundle.message("label.select.dataSource"),
                    I18nBundle.message("label.open.queryConsole"),
                    dataSourceNames,
                    dataSourceNames[0],
                    Messages.getQuestionIcon()
            );
            if (selectedIndex != -1) {
                selectedDataSource = dataSources.get(selectedIndex);
            }
        }
        if (selectedDataSource == null) {
            // 未选择数据源，操作取消
            return;
        }
        // 打开 Query Console
        try {
            VirtualFile file = DatabaseEditorHelper.createNewConsoleVirtualFile(selectedDataSource);
            if (file != null) {
                DatabaseEditorHelper.openConsoleForFile(project, selectedDataSource, selectedDataSource.getModel().getCurrentRootNamespace(), file);
                Document document = FileDocumentManager.getInstance().getDocument(file);
                if (document != null) {
                    // 获取当前应用实例
                    Application application = ApplicationManager.getApplication();
                    // 使用 runWriteAction 执行写操作
                    application.runWriteAction(() -> {
                        // 设置新的内容
                        document.setText(sqlContent);
                    });
                    // 保存文档
                    FileDocumentManager.getInstance().saveDocument(document);
                }
            }
        } catch (Exception e) {
            NotificationHelper.showErrorNotification(project, I18nBundle.message("label.open.queryConsole.error")+ "\n" + e.getMessage());
        }
    }



}
