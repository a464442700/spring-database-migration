package com.lxf.migration.output;

import com.lxf.migration.algorithm.AdjacencyListGraph;
import com.lxf.migration.pojo.Node;

public interface GraphPicture {
   public byte[] GetBinaryPicture(AdjacencyListGraph<Node> adjacencyListGraph);
}
