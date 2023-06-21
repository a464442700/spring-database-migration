package com.lxf.migration.dao.impl;

import com.lxf.migration.dao.SourceCodeDao;
import com.lxf.migration.pojo.Node;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import java.util.List;


public class SourceCodeDaoThread implements Runnable {
    //    @Autowired
//    private SourceCodeDaoImpl sourceCodeDaoImpl;
//    @Autowired
//    private ApplicationContext applicationContext;
//    private final  ThreadLocal<Node> threadLocal = new ThreadLocal<Node>();

//    public void setThreadLocal(Node node) {
//        threadLocal.set(node);
//        System.out.println("set node:"+node.objectName);
//    }
    private  Node node;

    private SourceCodeDaoImpl sourceCodeDaoImpl;

    public void setSourceCodeDaoImpl(SourceCodeDaoImpl sourceCodeDaoImpl) {
        this.sourceCodeDaoImpl = sourceCodeDaoImpl;
    }

    public SourceCodeDaoThread(Node node, SourceCodeDaoImpl sourceCodeDaoImpl) {
        this.node = node;
        this.sourceCodeDaoImpl = sourceCodeDaoImpl;
    }

    @Override
    public void run() {
        try {
//            var node=threadLocal.get();
//            System.out.println("get node:"+node.objectName);
            sourceCodeDaoImpl.getSourcode(node);
            sourceCodeDaoImpl.getSourcodeHash(node);
         

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
          //  threadLocal.remove();
        }
    }
}
