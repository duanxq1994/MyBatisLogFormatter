package com.alan.plugins.MyBatisLogFormatter.handler;

import com.alan.plugins.MyBatisLogFormatter.i18n.I18nBundle;
import com.alan.plugins.MyBatisLogFormatter.notice.NotificationHelper;
import com.intellij.database.dataSource.LocalDataSource;
import com.intellij.database.editor.DatabaseEditorHelper;
import com.intellij.database.model.DasNamespace;
import com.intellij.openapi.application.Application;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.fileEditor.FileDocumentManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;

/**
 * @author Alan
 */
public class OpenQueryConsoleActionHandler {

    /**
     * 打开 Query Console 并设置 SQL 内容
     * @param project 项目
     * @param selectedDataSource 选中的数据源
     * @param namespace 命名空间（schema），如果为 null 则使用默认的 root namespace
     * @param sqlContent SQL 内容
     */
    public static void openQueryConsoleWithClipboardSql(Project project, LocalDataSource selectedDataSource, DasNamespace namespace, String sqlContent) {
        // 打开 Query Console
        try {
            if (selectedDataSource == null) {
                NotificationHelper.showErrorNotification(project, I18nBundle.message("label.open.queryConsole.error") + "\n" + "DataSource is null");
                return;
            }
            
            // 如果 namespace 为 null，尝试获取默认的 root namespace
            if (namespace == null) {
                try {
                    namespace = selectedDataSource.getModel().getCurrentRootNamespace();
                } catch (Exception e) {
                    NotificationHelper.showErrorNotification(project, I18nBundle.message("label.open.queryConsole.error") + "\n" + "Failed to get namespace: " + e.getMessage());
                    return;
                }
            }
            
            VirtualFile file = DatabaseEditorHelper.createNewConsoleVirtualFile(selectedDataSource);
            if (file != null) {
                DatabaseEditorHelper.openConsoleForFile(project, selectedDataSource, namespace, file);
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
