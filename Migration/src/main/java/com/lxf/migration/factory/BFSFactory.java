package com.lxf.migration.factory;

import com.lxf.migration.pojo.Node;
import com.lxf.migration.service.BFS;

public class BFSFactory {
    public static  BFS createBFS(Node node ){
        BFS bfs =new BFS();
        bfs.start(node);

        return bfs;

    }
}
