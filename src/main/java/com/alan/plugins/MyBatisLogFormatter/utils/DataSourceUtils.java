package com.alan.plugins.MyBatisLogFormatter.utils;

import com.intellij.database.dataSource.LocalDataSource;
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

