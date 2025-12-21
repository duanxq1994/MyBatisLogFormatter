package com.alan.plugins.MyBatisLogFormatter.utils;

import com.intellij.database.dataSource.LocalDataSource;
import com.intellij.database.model.DasModel;
import com.intellij.database.model.DasNamespace;
import com.intellij.database.model.DasObject;
import com.intellij.database.model.basic.BasicElement;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * 数据源工具类
 */
public class DataSourceUtils {

    /**
     * 从 LocalDataSource 获取数据库类型
     */
    public static DatabaseType getDatabaseType(LocalDataSource dataSource, DatabaseType defaultDatabaseType) {
        if (dataSource == null) {
            return defaultDatabaseType;
        }
        
        try {
            // 方法1: 尝试通过 getDbms() 方法获取数据库类型
            String dbms = dataSource.getDbms().getName();
            return DatabaseType.fromString(dbms);
        } catch (Exception e) {
            // 忽略异常，继续尝试其他方法
        }
        
        try {
            // 方法2: 尝试通过连接 URL 判断
            String url = dataSource.getUrl();
            if (url != null) {
                String upperUrl = url.toUpperCase();
                if (upperUrl.contains("ORACLE") || upperUrl.contains("ORACLE:THIN") || upperUrl.contains("ORACLE:OCI")) {
                    return DatabaseType.ORACLE;
                } else if (upperUrl.contains("MYSQL") || upperUrl.contains("MARIADB")) {
                    return DatabaseType.MYSQL;
                }
            }
        } catch (Exception e) {
            // 忽略异常
        }
        
        try {
            // 方法3: 尝试通过驱动类名判断
            String driverClass = dataSource.getDriverClass();
            if (driverClass != null) {
                String upperDriver = driverClass.toUpperCase();
                if (upperDriver.contains("ORACLE")) {
                    return DatabaseType.ORACLE;
                } else if (upperDriver.contains("MYSQL")) {
                    return DatabaseType.MYSQL;
                }
            }
        } catch (Exception e) {
            // 忽略异常
        }
        return defaultDatabaseType;
    }

    /**
     * 获取数据源的所有可用 schema/namespace 列表
     * @param dataSource 数据源
     * @return schema 列表，如果无法获取则返回包含当前 root namespace 的列表
     */
    public static List<DasNamespace> getAvailableNamespaces(LocalDataSource dataSource) {
        Set<DasNamespace> namespaces = new HashSet<>();
        DasNamespace dasNamespace = dataSource.getModel().getCurrentRootNamespace();
        if (dasNamespace != null) {
            namespaces.add(dasNamespace);
        }
        for (DasObject modelRoot : dataSource.getModel().getModelRoots()) {
            if (modelRoot instanceof BasicElement && modelRoot instanceof DasNamespace) {
                boolean introspected = dataSource.getSchemaMapping().isIntrospected((BasicElement) modelRoot);
                if (introspected) {
                    namespaces.add((DasNamespace) modelRoot);
                }
            }
        }
        return new ArrayList<>(namespaces);
    }
}

