package com.lxf.migration.dao.impl;

import com.lxf.migration.dao.SourceCodeDao;
import com.lxf.migration.pojo.Node;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import java.util.List;


public class SourceCodeDaoThread implements Runnable {

    private Node node;

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

            sourceCodeDaoImpl.getSourcode(node);

            sourceCodeDaoImpl.getSourcodeHashSHA256(node);


        } catch (Exception e) {
            e.printStackTrace();
        } finally {

        }
    }
}
