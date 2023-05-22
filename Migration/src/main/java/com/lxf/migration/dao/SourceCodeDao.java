package com.lxf.migration.dao;


import com.lxf.migration.pojo.Node;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

@Repository
public interface SourceCodeDao {
    public void getSourcode(Node node) throws Exception;
    public void getSourcodeHash(Node node) throws Exception;
}
