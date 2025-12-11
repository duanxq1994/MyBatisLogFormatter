package com.alan.plugins.MyBatisLogFormatter.config;

import com.alan.plugins.MyBatisLogFormatter.utils.DatabaseType;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.components.PersistentStateComponent;
import com.intellij.openapi.components.State;
import com.intellij.openapi.components.Storage;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * MyBatis Log Formatter 配置服务
 */
@State(name = "MyBatisLogFormatterSettings", storages = @Storage("MyBatisLogFormatter.xml"))
public class MyBatisLogFormatterSettings implements PersistentStateComponent<MyBatisLogFormatterSettings.State> {

    private State myState = new State();

    public static MyBatisLogFormatterSettings getInstance() {
        return ApplicationManager.getApplication().getService(MyBatisLogFormatterSettings.class);
    }

    /**
     * 获取默认数据库类型
     */
    public DatabaseType getDefaultDatabaseType() {
        if (myState.defaultDatabaseTypeName == null || myState.defaultDatabaseTypeName.isEmpty()) {
            return DatabaseType.MYSQL; // 默认 MySQL
        }
        try {
            return DatabaseType.valueOf(myState.defaultDatabaseTypeName);
        } catch (IllegalArgumentException e) {
            return DatabaseType.MYSQL; // 默认 MySQL
        }
    }

    /**
     * 设置默认数据库类型
     */
    public void setDefaultDatabaseType(DatabaseType databaseType) {
        if (databaseType != null) {
            myState.defaultDatabaseTypeName = databaseType.name();
        } else {
            myState.defaultDatabaseTypeName = DatabaseType.MYSQL.name();
        }
    }

    @Override
    public @Nullable State getState() {
        return myState;
    }

    @Override
    public void loadState(@NotNull State state) {
        myState = state;
    }

    /**
     * 状态类
     */
    public static class State {
        public String defaultDatabaseTypeName = DatabaseType.MYSQL.name();
    }
}

