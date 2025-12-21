package com.alan.plugins.MyBatisLogFormatter.utils;

import com.intellij.openapi.editor.CaretModel;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.editor.SelectionModel;
import com.intellij.openapi.util.TextRange;
import org.apache.commons.lang3.StringUtils;

public class EditorUtils {
    public static String getSelectedTextOrAroundCursor(Editor editor) {
        SelectionModel selectionModel = editor.getSelectionModel();
        String selectedText = selectionModel.getSelectedText();
        if (StringUtils.isNotBlank(selectedText)) {
            return selectedText;
        }

        Document document = editor.getDocument();
        CaretModel caretModel = editor.getCaretModel();
        int caretOffset = caretModel.getOffset();
        int lineNum = document.getLineNumber(caretOffset);
        
        // 获取光标周围 1 行
        int around = 1;
        int startLine = Math.max(0, lineNum - around);
        int endLine = Math.min(document.getLineCount() - 1, lineNum + around);

        int startOffset = document.getLineStartOffset(startLine);
        int endOffset = document.getLineEndOffset(endLine);

        return document.getText(new TextRange(startOffset, endOffset));
    }
}

