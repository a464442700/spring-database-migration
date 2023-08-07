package com.lxf.migration.thread.impl;

import com.lxf.migration.dao.SourceCodeDao;
import com.lxf.migration.dao.impl.SourceCodeDaoImpl;
import com.lxf.migration.dao.impl.SourceCodeDaoThread;
import com.lxf.migration.pojo.Node;

import java.util.concurrent.ExecutorService;

public interface BFSThreadPool {
    public void init(Integer threadPoolSize);

    public void setSourceCode(Node node, SourceCodeDaoImpl s);

    public void shutdownPool();
}
