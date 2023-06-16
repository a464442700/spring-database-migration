package com.lxf.migration.config;

import org.springframework.stereotype.Component;


public class DataSourceWrapper {
    private DataSourceProperty localDataSource;
    private DataSourceProperty remoteDataSource;

    public void setLocalDataSource(DataSourceProperty localDataSource) {
        this.localDataSource = localDataSource;
    }

    public void setRemoteDataSource(DataSourceProperty remoteDataSource) {
        this.remoteDataSource = remoteDataSource;
    }


    public DataSourceProperty getLocalDataSource() {
        return localDataSource;
    }

    public DataSourceProperty getRemoteDataSource() {
        return remoteDataSource;
    }


}
