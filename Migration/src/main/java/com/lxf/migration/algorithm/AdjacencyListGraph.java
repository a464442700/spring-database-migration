package com.lxf.migration.algorithm;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

//不完全是邻接表，因为顶点表没有直接指向边表，而是用hashMap索引
public class AdjacencyListGraph<N> {
    private List<N> vertex;//顶点表
    private Map<N, List<N>> side;//边表

    public List<N> getVertex() {
        return vertex;
    }

    public Map<N, List<N>> getSide() {
        return side;
    }

    public AdjacencyListGraph() {
        this.vertex = new ArrayList<N>();
        this.side = new HashMap<N,List<N>>();
    }

    //添加顶点
    public void addVertex(N n) {
        this.vertex.add(n);
       // List<N> value= new ArrayList<N>();
      //  this.side.put(n, value);//将初始化放入addSide,否则最后一个边是空的

    }
    //添加边
    //v是顶点，n是边节点
    //有向图只用添加一条边
    public void addSide(N v,N u){
        if (!this.side.containsKey(v)){
             List<N> value= new ArrayList<N>();
             this.side.put(v, value);//放到这里虽然不会产生空边表，但是每次添加边表都要判断，性能会下降一点
        }
    this.side.get(v).add(u);
    }

    @Override
    public String toString() {
        return "AdjacencyListGraph{" +
                "vertex=" + vertex +
                ", side=" + side +
                '}';
    }
}
