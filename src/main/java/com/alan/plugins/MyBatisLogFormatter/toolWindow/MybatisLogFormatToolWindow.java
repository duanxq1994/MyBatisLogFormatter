package com.alan.plugins.MyBatisLogFormatter.toolWindow;

import com.alan.plugins.MyBatisLogFormatter.component.DataListComponent;
import com.alan.plugins.MyBatisLogFormatter.config.MyBatisLogFormatterSettings;
import com.alan.plugins.MyBatisLogFormatter.i18n.I18nBundle;
import com.alan.plugins.MyBatisLogFormatter.notice.NotificationHelper;
import com.alan.plugins.MyBatisLogFormatter.utils.DatabaseType;
import com.alan.plugins.MyBatisLogFormatter.utils.SqlUtils;
import com.alan.plugins.MyBatisLogFormatter.utils.Utils;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.ui.JBColor;
import com.intellij.ui.components.JBScrollPane;
import com.intellij.ui.components.JBTabbedPane;
import com.intellij.ui.components.JBTextArea;
import com.intellij.ui.components.JBTextField;
import org.apache.commons.lang3.StringUtils;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.LineBorder;
import java.awt.*;

public class MybatisLogFormatToolWindow {
    private final JPanel contentPanel = new JPanel();
    private final ToolWindow toolWindow;
    private final Project project;
//    private JBTabsImpl jbTabs;
    private final JBTabbedPane jbTabbedPane = new JBTabbedPane();
    private DataListComponent formatResultDataListComponent;
    private JComboBox<DatabaseType> databaseTypeComboBox;
//    Locale locale = DynamicBundle.getLocale();
//    String language = locale.getLanguage();


    public JPanel getContentPanel() {
        return contentPanel;
    }

    public MybatisLogFormatToolWindow(Project project, ToolWindow toolWindow) {
        this.project = project;
        this.toolWindow = toolWindow;
        init();
    }

    private void init() {
//        jbTabs = new JBTabsImpl(project);
        createFormatPanel();
        createCompressTabPanel();
        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
        contentPanel.add(jbTabbedPane, BorderLayout.CENTER);
    }

    /**
     * 格式化mybatisLog面板
     */
    private void createFormatPanel() {
        // 格式化sql面板
        JPanel formatTabPanel = new JPanel();
        formatTabPanel.setLayout(new BoxLayout(formatTabPanel, BoxLayout.Y_AXIS));
        Border border = BorderFactory.createEmptyBorder(8, 8, 8, 8);

        formatTabPanel.setBorder(border);

        // 输入框面板
        JPanel textPanel = new JPanel();
        textPanel.setLayout(new GridLayout(1, 1));
        textPanel.setPreferredSize(new Dimension(Short.MAX_VALUE, 400));
        textPanel.setMaximumSize(new Dimension(Short.MAX_VALUE, 400));
        JBTextArea formatText = new JBTextArea();
        formatText.setLineWrap(true);
        formatText.setBorder(new LineBorder(JBColor.DARK_GRAY));
        // 滚动条
        JScrollPane scrollPane = new JBScrollPane(formatText);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);

        textPanel.add(scrollPane);

        formatTabPanel.add(textPanel, 0);

        // 数据库类型选择区域
        JPanel dbTypePanel = new JPanel();
        dbTypePanel.setLayout(new FlowLayout(FlowLayout.LEFT));
        dbTypePanel.setPreferredSize(new Dimension(Short.MAX_VALUE, 30));
        dbTypePanel.setMaximumSize(new Dimension(Short.MAX_VALUE, 30));
        JLabel dbTypeLabel = new JLabel(I18nBundle.message("label.database.type"));
        // 从 DatabaseType 枚举获取所有可用的数据库类型，便于后续扩展
        DatabaseType[] databaseTypes = DatabaseType.values();
        databaseTypeComboBox = new JComboBox<>(databaseTypes);
        databaseTypeComboBox.setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, int index,
                                                          boolean isSelected, boolean cellHasFocus) {
                super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                if (value instanceof DatabaseType) {
                    setText(((DatabaseType) value).getDisplayName());
                }
                return this;
            }
        });
        // 使用配置中的默认数据库类型
        DatabaseType defaultType = MyBatisLogFormatterSettings.getInstance().getDefaultDatabaseType();
        databaseTypeComboBox.setSelectedItem(defaultType);
        dbTypePanel.add(dbTypeLabel);
        dbTypePanel.add(databaseTypeComboBox);
        formatTabPanel.add(dbTypePanel);

        // 按钮区域
        JPanel btnPanel = new JPanel();
        btnPanel.setPreferredSize(new Dimension(Short.MAX_VALUE, 40));
        btnPanel.setMaximumSize(new Dimension(Short.MAX_VALUE, 40));
        JButton formatBtn = new JButton(I18nBundle.message("formatCopyLabel"));
        JButton clearAllBtn = new JButton(I18nBundle.message("clearRecordsLabel"));
        formatBtn.addActionListener(e -> {
            String text = formatText.getText();
            if (StringUtils.isNotBlank(text)) {
                // 提前检测文本是否包含 MyBatis 日志关键字
                if (!SqlUtils.containsMybatisLog(text)) {
                    NotificationHelper.showWarningNotification(project, I18nBundle.message("label.notice.not.sql.statement"));
                    return;
                }
                // 获取选中的数据库类型
                DatabaseType selectedDbType = (DatabaseType) databaseTypeComboBox.getSelectedItem();
                String sql = SqlUtils.formatMybatisLog(text, selectedDbType);
                if (StringUtils.isNotBlank(sql)) {
                    Utils.copy(sql);
                    appendLog(sql);
                }
            }
        });
        clearAllBtn.addActionListener(e -> {
            clearAllHistory();
        });
        btnPanel.add(formatBtn);
        btnPanel.add(clearAllBtn);

        formatTabPanel.add(btnPanel);

        // 结果显示面板
        JPanel resultPanel = new JPanel();
        resultPanel.setLayout(new BoxLayout(resultPanel, BoxLayout.Y_AXIS));
        resultPanel.setPreferredSize(new Dimension(0, 400));
        resultPanel.setMaximumSize(new Dimension(Short.MAX_VALUE, 400));
        resultPanel.setBorder(BorderFactory.createLineBorder(JBColor.DARK_GRAY, 1));
        formatResultDataListComponent = new DataListComponent();
        formatResultDataListComponent.setReverseInd(true);
        resultPanel.add(formatResultDataListComponent.getScrollPane());

        formatTabPanel.add(resultPanel);

//        TabInfo formatTabInfo = new TabInfo(formatTabPanel);
//        formatTabInfo.setText("Formatter");
//        jbTabs.addTab(formatTabInfo);
        jbTabbedPane.addTab(I18nBundle.message("formatterLabel"), formatTabPanel);

    }

    /**
     * log格式化日志
     * @param sql sql
     */
    private void appendLog(String sql) {
        JPanel logItem = new JPanel();
        logItem.setLayout(new BorderLayout());
        logItem.setBorder(BorderFactory.createLineBorder(JBColor.DARK_GRAY, 1));

        // text
        JLabel logTextLabel = new JLabel();
        logTextLabel.setLayout(new GridLayout(1, 1));
        logTextLabel.setPreferredSize(new Dimension(50, 50));
        JBTextField textField = new JBTextField();
        textField.setEditable(false);
        textField.setFocusable(false);
        textField.setText(sql);
        textField.setBorder(null);
        textField.getCaret().setDot(0);
        logTextLabel.add(textField);

        logItem.add(logTextLabel, BorderLayout.CENTER);

        // copy
        JButton button = new JButton(I18nBundle.message("copyLabel"));
        button.addActionListener(e -> {
            String text = textField.getText();
            if (StringUtils.isNotBlank(text)) {
                Utils.copy(text);
                button.setText(I18nBundle.message("copiedLabel"));
                Utils.timerTask(2, () -> {
                    button.setText(I18nBundle.message("copyLabel"));
                });
            }
        });
        logItem.add(button, BorderLayout.EAST);
        formatResultDataListComponent.addRow(logItem);
    }


    private void clearAllHistory() {
        formatResultDataListComponent.removeAll();
    }

    /**
     * sql压缩和格式化面板
     */
    private void createCompressTabPanel() {
        // 压缩sql面板
        JPanel compressTabPanel = new JPanel();
        compressTabPanel.setLayout(new BoxLayout(compressTabPanel, BoxLayout.Y_AXIS));
        Border border = BorderFactory.createEmptyBorder(8, 8, 8, 8);

        compressTabPanel.setBorder(border);

        // 输入框面板
        JPanel textPanel = new JPanel();
        textPanel.setLayout(new GridLayout(1, 1));
        textPanel.setPreferredSize(new Dimension(Short.MAX_VALUE, 400));
        textPanel.setMaximumSize(new Dimension(Short.MAX_VALUE, 400));
        JBTextArea inputText = new JBTextArea();
        inputText.setLineWrap(true);
        inputText.setBorder(new LineBorder(JBColor.DARK_GRAY));
        // 滚动条
        JScrollPane inputScrollPane = new JBScrollPane(inputText);
        inputScrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        inputScrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);

        textPanel.add(inputScrollPane);

        compressTabPanel.add(textPanel, 0);

        // 按钮区域
        JPanel btnPanel = new JPanel();
        btnPanel.setPreferredSize(new Dimension(Short.MAX_VALUE, 40));
        btnPanel.setMaximumSize(new Dimension(Short.MAX_VALUE, 40));
        JButton compressBtn = new JButton(I18nBundle.message("compressCopyLabel"));
        JButton beautifyBtn = new JButton(I18nBundle.message("beautifyCopyLabel"));

        btnPanel.add(compressBtn);
        btnPanel.add(beautifyBtn);

        compressTabPanel.add(btnPanel);

        // 结果显示面板
        JPanel resultPanel = new JPanel();
        resultPanel.setLayout(new BoxLayout(resultPanel, BoxLayout.Y_AXIS));
        resultPanel.setPreferredSize(new Dimension(0, 400));
        resultPanel.setMaximumSize(new Dimension(Short.MAX_VALUE, 400));
        resultPanel.setBorder(BorderFactory.createLineBorder(JBColor.DARK_GRAY, 1));
        JBTextArea resultText = new JBTextArea();
        resultText.setLineWrap(true);
        // 滚动条
        JScrollPane resultScrollPane = new JBScrollPane(resultText);
        resultScrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        resultScrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);

        resultPanel.add(resultScrollPane);

        compressTabPanel.add(resultPanel);

        compressBtn.addActionListener(e -> {
            String text = inputText.getText();
            if (StringUtils.isNotBlank(text)) {
                String sql = SqlUtils.compressSql(text);
                if (StringUtils.isNotBlank(sql)) {
                    Utils.copy(sql);
                    resultText.setText(sql);
                }
            }
        });
        beautifyBtn.addActionListener(e -> {
            String text = inputText.getText();
            if (StringUtils.isNotBlank(text)) {
                String sql = SqlUtils.beautifySql(text);
                if (StringUtils.isNotBlank(sql)) {
                    Utils.copy(sql);
                    resultText.setText(sql);
                }
            }
        });

//        TabInfo compressTabInfo = new TabInfo(compressTabPanel);
//        compressTabInfo.setText("Compress");
//        jbTabs.addTab(compressTabInfo);
        jbTabbedPane.addTab(I18nBundle.message("compressLabel"), compressTabPanel);
    }

}
