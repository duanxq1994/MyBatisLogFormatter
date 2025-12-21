package com.alan.plugins.MyBatisLogFormatter.actions;

import com.alan.plugins.MyBatisLogFormatter.config.MyBatisLogFormatterSettings;
import com.alan.plugins.MyBatisLogFormatter.i18n.I18nBundle;
import com.alan.plugins.MyBatisLogFormatter.notice.NotificationHelper;
import com.alan.plugins.MyBatisLogFormatter.utils.DatabaseType;
import com.alan.plugins.MyBatisLogFormatter.utils.EditorUtils;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.openapi.editor.Editor;
import com.alan.plugins.MyBatisLogFormatter.utils.SqlUtils;
import com.alan.plugins.MyBatisLogFormatter.utils.Utils;
import com.intellij.openapi.project.Project;
import org.apache.commons.lang3.StringUtils;

public class MyBatisLogFormatter extends AnAction {

    @Override
    public void actionPerformed(AnActionEvent e) {
        Project project = e.getProject();
        Editor editor = e.getData(CommonDataKeys.EDITOR);
        if (editor == null || project == null) {
            return;
        }
        // 获得选中的文本，如果没有选中则获取光标周围 3 行
        String selectedText = EditorUtils.getSelectedTextOrAroundCursor(editor);
        if (StringUtils.isBlank(selectedText)) {
            NotificationHelper.showWarningNotification(project, I18nBundle.message("label.select.myBatisLog"));
            return;
        }

        // 提前检测文本是否包含 MyBatis 日志关键字
        if (!SqlUtils.containsMybatisLog(selectedText)) {
            NotificationHelper.showWarningNotification(project, I18nBundle.message("label.notice.not.sql.statement"));
            return;
        }

        // 使用配置中的默认数据库类型
        final DatabaseType defaultDatabaseType = MyBatisLogFormatterSettings.getInstance()
            .getDefaultDatabaseType();
        String sqlContent = SqlUtils.formatMybatisLog(selectedText, defaultDatabaseType);
        if (StringUtils.isNotBlank(sqlContent)) {
            Utils.copy(sqlContent);
            NotificationHelper.showInfoNotification(project, I18nBundle.message("label.notice.copy.success"));
        }
    }
}
