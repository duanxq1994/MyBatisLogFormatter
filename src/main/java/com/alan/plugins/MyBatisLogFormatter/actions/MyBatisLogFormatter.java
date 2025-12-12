package com.alan.plugins.MyBatisLogFormatter.actions;

import com.alan.plugins.MyBatisLogFormatter.config.MyBatisLogFormatterSettings;
import com.alan.plugins.MyBatisLogFormatter.utils.DatabaseType;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.alan.plugins.MyBatisLogFormatter.utils.SqlUtils;
import com.alan.plugins.MyBatisLogFormatter.utils.Utils;
import org.apache.commons.lang3.StringUtils;

public class MyBatisLogFormatter extends AnAction {

    @Override
    public void actionPerformed(AnActionEvent e) {
        // 获得选中的文本
        String selectedText = e.getRequiredData(CommonDataKeys.EDITOR).getSelectionModel().getSelectedText();
        if (StringUtils.isNotBlank(selectedText)) {
            // 使用配置中的默认数据库类型
            final DatabaseType defaultDatabaseType = MyBatisLogFormatterSettings.getInstance()
                .getDefaultDatabaseType();
            Utils.copy(SqlUtils.formatMybatisLog(selectedText, defaultDatabaseType));
        }
    }
}
