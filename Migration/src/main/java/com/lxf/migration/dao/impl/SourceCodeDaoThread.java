package com.lxf.migration.dao.impl;

import com.lxf.migration.dao.SourceCodeDao;
import com.lxf.migration.pojo.Node;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

public class SourceCodeDaoThread implements Runnable {
    @Autowired
    private SourceCodeDao sourceCodeDao;
    private Node node;

    public void setNode(Node node) {
        this.node = node;
    }

    @Override
    public void run() {
        try {
            sourceCodeDao.getSourcode(node);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
