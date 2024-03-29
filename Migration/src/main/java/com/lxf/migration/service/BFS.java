package com.lxf.migration.service;


import com.lxf.migration.algorithm.AdjacencyListGraph;
import com.lxf.migration.dao.impl.DependenciesDaoImpl;
import com.lxf.migration.dao.impl.SourceCodeDaoImpl;
import com.lxf.migration.dao.impl.SourceCodeDaoThread;
import com.lxf.migration.pojo.Node;
import com.lxf.migration.pojo.TreeListNode;
import com.lxf.migration.thread.impl.BFSThreadPool;
import com.lxf.migration.thread.impl.ThreadPoolImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

@Service
//@RequestScope
@Scope(BeanDefinition.SCOPE_PROTOTYPE)
//@Scope(value = WebApplicationContext.SCOPE_REQUEST, proxyMode = ScopedProxyMode.TARGET_CLASS)
public class BFS implements Runnable {


    //   private ExecutorService threadPool;

    public SourceCodeDaoImpl getSourceCodeDaoImpl() {
        return s;
    }

    public DependenciesDaoImpl getDependenciesDaoImpl() {
        return d;
    }

    @Autowired//切面进行了手动注入，这个注解加了没有用，为什么没有自动注入，因为BFS是new 出来的，没有被容器管理
    //切面注入的是mapper,不是service，所以当我把new BFS去掉后
    public SourceCodeDaoImpl s;
    @Autowired//切面进行了手动注入，
    private DependenciesDaoImpl d;

    //    @Autowired
//    private BFSThreadPool t;
    private String dataSource;
    private List<TreeListNode> treeListNodes;

    public List<TreeListNode> getTreeListNodes() {

        return this.set.stream().map((node) -> {
            return new TreeListNode(node.objectID, node.parentNode != null ? node.parentNode.objectID : null,
                    node.database + "." + node.owner + "." + node.objectName + "." + node.objectType,
                    "[" + node.objectType + "] " + node.objectName
            );
        }).collect(Collectors.toList());


    }

    public void setDataSource(String dataSource) {
        this.dataSource = dataSource;
    }

    private Boolean displaySourceCode;

    public void setDisplaySourceCode(Boolean displaySourceCode) {
        this.displaySourceCode = displaySourceCode;
    }

    public AdjacencyListGraph<Node> getGraph() {
        return graph;
    }

    public BFS(Node startNode) {
        this.startNode = startNode;
        this.queue = new LinkedList<Node>();
        this.set = new HashSet<Node>();
        this.graph = new AdjacencyListGraph<Node>();

        // this.threadPool = Executors.newFixedThreadPool(10);
    }

    public BFS() {

        //BFS属于构造函数，构造函数执行后，才进行注入，所以执行this.init，里面的t对象将会是空的
        // this.init();

    }

    public void setStartNode(Node startNode) {
        this.startNode = startNode;
        //   this.displaySourceCode = startNode.getShowSourceCode();
    }

    private AdjacencyListGraph<Node> graph;
    private Node startNode;//广度优先搜索起点
    private Queue<Node> queue;//辅助队列
    private Set<Node> set;//访问标记集合，防止带环图无限循环

    public Stack<Node> getStack() {
        return this.stack;
    }

    public Set<Node> getSet() {
        return this.set;
    }

    private Stack<Node> stack;//访问一个节点入栈，这样从栈弹出顺序就是编译顺序


    //该节点是否被访问
    public boolean isVisited(Node node) {
//        System.out.print(node);
//        System.out.print(node.hashCode());
//        System.out.print(node.getClass());
//        System.out.print(this.set);
//        System.out.println(this.set.contains(node));
        return this.set.contains(node);

    }
//调用这个方法就能获取到源码
//    private void setSourceCode(Node node) {
//        SourceCodeDaoThread sourceCodeDaoThread=new SourceCodeDaoThread(node,s);
//
//        this.threadPool.execute(sourceCodeDaoThread);

//    }

    //设置访问标签
    public void visited(Node node) {
        this.set.add(node);//写入集合
        this.stack.add(node);//入栈，作用是从栈弹出的一定是level最高的
        this.graph.addVertex(node);
        //不打印速度会快一点
        if (this.displaySourceCode) {
            s.getSourcode(node);

            try {
                s.getSourcodeHashSHA256(node);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        if (node.dataSource == null) {
            node.setDataSource(this.dataSource);
        }


    }

//    private void shutdownPool() {
//        if (this.displaySourceCode) {
//            t.shutdownPool();
////            threadPool.shutdown();
////
////            while (!threadPool.isTerminated()) {//循环是要判断所有的线程是否关闭
////                try {
////                    Thread.sleep(100);
////                } catch (InterruptedException e) {
////
////                    e.printStackTrace();
////                }
////            }
//        }
//    }

    //初始化
    public void init() {

        this.queue = new LinkedList<Node>();
        this.set = new HashSet<Node>();
        this.graph = new AdjacencyListGraph<Node>();
        this.stack = new Stack<Node>();
        this.treeListNodes = new ArrayList<TreeListNode>();
        // this.threadPool =s.getThreadPool(20);// Executors.newFixedThreadPool(20);//初始化20个线程，后续并行获取sourceCode
        //this.t.init(20);
        setDisplaySourceCode(false);
    }

    public void start(Node node) {


        this.init();
        this.setStartNode(node);
        // this.setDataSource(node.dataSource);
        //在AOP中，切面是通过代理机制实现的。当你通过获取实例并直接调用实例方法时，
        // 实际上不会经过AOP的代理对象，而是直接调用实际对象的方法。因此，切面对于同一个实例内部调用的方法不会起作用。
        this.setDisplaySourceCode(node.showSourceCode);
    }

    private ArrayList<Node> getNeighbors(Node node) {
        //   DependenciesDaoImpl d = new DependenciesDaoImpl();
        ArrayList<Node> nodes = d.findAllNeighborNode(node);
        return nodes;
    }


    public void Traverse() {
        //判断起点node是否存在

        if (!d.isObjectExists(this.startNode, d.getMapper())) {
            return;
        }
        Node v = this.startNode;//起始节点
        this.visited(v);//设置访问标记
        this.queue.add(v);//访问后入队
        while (!this.queue.isEmpty()) {
            v = this.queue.poll();//出队
            //开始访问所有v的子节点


            for (Node u : this.getNeighbors(v)) {
                this.graph.addSide(v, u);
                //   TreeListNode treeListNode =new TreeListNode( u.objectID ,v.objectID,v.objectID.toString() ,u.database+"."+u.owner+"."+u.objectName+"."+ u.objectType);
                //    this.treeListNodes.add(treeListNode);
                u.setParentNode(v);


                if (!this.isVisited(u)) {
                    this.visited(u);//访问
                    this.queue.add(u);//入队
                }
            }


        }
        //等到所有多线程获取源码执行完毕，再往下进行
        // shutdownPool();

    }

    public static void main(String[] args) {

    }
//mvc代码

//    public   List<DataSource> getDataSources(){
//        List<DataSource> dataSources =new ArrayList<DataSource>();
//        DataSource dataSource=new DataSource();
//        dataSource.sourceName=d.getDatabase();
//
//        dataSources.add(dataSource);
//
//        return  dataSources;
//    }

    public String getDataBase() {
        return d.getDatabase();
    }

    //这里的run是两个不同的数据源对应的BFS同时跑
    @Override
    public void run() {
        Traverse();
    }
}

//react-force-graph
//图可视化框架，本项目未采用,前后端分离项目个人开发花时间
//DependencyController是符合前后端分离的
//https://potoyang.gitbook.io/spring-in-action-v5/
//在线电子书