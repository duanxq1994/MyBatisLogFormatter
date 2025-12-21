package com.alan.plugins.MyBatisLogFormatter.handler;

import com.alan.plugins.MyBatisLogFormatter.i18n.I18nBundle;
import com.alan.plugins.MyBatisLogFormatter.notice.NotificationHelper;
import com.intellij.database.dataSource.LocalDataSource;
import com.intellij.database.editor.DatabaseEditorHelper;
import com.intellij.database.model.DasNamespace;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.application.ModalityState;
import com.intellij.openapi.command.WriteCommandAction;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.editor.ScrollType;
import com.intellij.openapi.fileEditor.FileDocumentManager;
import com.intellij.openapi.fileEditor.FileEditorManager;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiDocumentManager;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiManager;
import com.intellij.psi.codeStyle.CodeStyleManager;
import org.jetbrains.annotations.Nullable;

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
    public static void openQueryConsoleWithClipboardSql(Project project, LocalDataSource selectedDataSource, @Nullable DasNamespace namespace, String sqlContent) {
        // 打开 Query Console
        try {
            if (selectedDataSource == null) {
                NotificationHelper.showErrorNotification(project, I18nBundle.message("label.open.queryConsole.error") + "\n" + I18nBundle.message("label.error.dataSource.is.null"));
                return;
            }

            VirtualFile file = DatabaseEditorHelper.createNewConsoleVirtualFile(selectedDataSource);
            if (file != null) {
                DatabaseEditorHelper.openConsoleForFile(project, selectedDataSource, namespace, file);

                // 等待 editor/psi 准备好后再写入并格式化（使用 IDEA 自带 SQL 格式化规则）
                ApplicationManager.getApplication().invokeLater(() -> {
                    if (project.isDisposed()) {
                        return;
                    }
                    Document document = FileDocumentManager.getInstance().getDocument(file);
                    if (document == null) {
                        return;
                    }

                    WriteCommandAction.runWriteCommandAction(project, () -> {
                        document.setText(sqlContent);
                        PsiDocumentManager.getInstance(project).commitDocument(document);

                        PsiFile psiFile = PsiManager.getInstance(project).findFile(file);
                        if (psiFile != null) {
                            CodeStyleManager.getInstance(project).reformat(psiFile);
                        }
                    });

                    // 尽量让光标落在该 Console 的编辑器中
                    Editor editor = FileEditorManager.getInstance(project).getSelectedTextEditor();
                    if (editor != null) {
                        VirtualFile currentFile = FileDocumentManager.getInstance().getFile(editor.getDocument());
                        if (file.equals(currentFile)) {
                            editor.getCaretModel().moveToOffset(0);
                            editor.getScrollingModel().scrollToCaret(ScrollType.CENTER_UP);
                        }
                    }

                    FileDocumentManager.getInstance().saveDocument(document);
                }, ModalityState.defaultModalityState());
            }
        } catch (Exception e) {
            NotificationHelper.showErrorNotification(project, I18nBundle.message("label.open.queryConsole.error")+ "\n" + e.getMessage());
        }
    }

}
