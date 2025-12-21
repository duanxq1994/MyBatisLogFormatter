package com.alan.plugins.MyBatisLogFormatter.config;

import com.alan.plugins.MyBatisLogFormatter.i18n.I18nBundle;
import com.alan.plugins.MyBatisLogFormatter.utils.DatabaseType;
import com.intellij.openapi.options.Configurable;
import com.intellij.openapi.options.ConfigurationException;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

/**
 * MyBatis Log Formatter 配置界面
 */
public class MyBatisLogFormatterConfigurable implements Configurable {

    private JComboBox<DatabaseType> databaseTypeComboBox;
    private MyBatisLogFormatterSettings settings;

    @Nls(capitalization = Nls.Capitalization.Title)
    @Override
    public String getDisplayName() {
        return "MyBatis Log Formatter";
    }

    @Nullable
    @Override
    public JComponent createComponent() {
        settings = MyBatisLogFormatterSettings.getInstance();
        
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(new EmptyBorder(10, 10, 10, 10));

        // 数据库类型选择
        JPanel dbTypePanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JLabel dbTypeLabel = new JLabel(I18nBundle.message("label.database.type.default"));
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
        // 安全地获取默认数据库类型
        DatabaseType defaultType = DatabaseType.DEFAULT_DATABASE_TYPE;
        if (settings != null) {
            defaultType = settings.getDefaultDatabaseType();
        }
        databaseTypeComboBox.setSelectedItem(defaultType);
        
        dbTypePanel.add(dbTypeLabel);
        dbTypePanel.add(databaseTypeComboBox);
        
        panel.add(dbTypePanel, BorderLayout.NORTH);
        
        return panel;
    }

    @Override
    public boolean isModified() {
        if (settings == null || databaseTypeComboBox == null) {
            return false;
        }
        DatabaseType currentSelected = (DatabaseType) databaseTypeComboBox.getSelectedItem();
        if (currentSelected == null) {
            return false;
        }
        DatabaseType savedType = settings.getDefaultDatabaseType();
        return !currentSelected.equals(savedType);
    }

    @Override
    public void apply() throws ConfigurationException {
        if (settings != null && databaseTypeComboBox != null) {
            DatabaseType selectedType = (DatabaseType) databaseTypeComboBox.getSelectedItem();
            if (selectedType != null) {
                settings.setDefaultDatabaseType(selectedType);
            }
        }
    }

    @Override
    public void reset() {
        if (settings == null || databaseTypeComboBox == null) {
            return;
        }
        DatabaseType defaultType = settings.getDefaultDatabaseType();
        databaseTypeComboBox.setSelectedItem(defaultType);
    }
}

