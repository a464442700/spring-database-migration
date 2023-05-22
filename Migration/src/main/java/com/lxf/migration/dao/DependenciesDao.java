package com.lxf.migration.dao;



import com.lxf.migration.pojo.Node;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
@Repository
public interface DependenciesDao {
    ArrayList<Node> findAllNeighborNode(Node node) throws Exception;//直接相邻

}
