package com.lxf.migration.dao;



import com.lxf.migration.pojo.Node;

import java.util.ArrayList;

public interface DependenciesDao {
    ArrayList<Node> findAllNeighborNode(Node node) throws Exception;//直接相邻

}
