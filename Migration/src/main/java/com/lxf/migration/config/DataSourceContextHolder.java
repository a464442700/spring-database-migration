package com.lxf.migration.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;

import java.util.Objects;


public class DataSourceContextHolder {
    private static final ThreadLocal<DataSourceWrapper> contextHolder = new ThreadLocal<>();

    public static void setCurrentDataSourceWrapper(DataSourceWrapper dataSourceWrapper) {

        contextHolder.set(dataSourceWrapper);
    }

    public static DataSourceWrapper getCurrentDataSourceWrapper() {
        return contextHolder.get();
    }

    public static void clear() {
        contextHolder.remove();
    }
}

