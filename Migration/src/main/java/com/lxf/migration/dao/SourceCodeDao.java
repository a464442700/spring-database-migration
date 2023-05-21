package com.lxf.migration.dao;


import com.lxf.migration.pojo.Node;

public interface SourceCodeDao {
    public void getSourcode(Node node) throws Exception;
    public void getSourcodeHash(Node node) throws Exception;
}
