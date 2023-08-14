package com.lxf.migration.dao.impl;


import com.lxf.migration.dao.DependenciesDao;
import com.lxf.migration.mapper.BFSMapper;
import com.lxf.migration.pojo.DbaObjects;
import com.lxf.migration.pojo.Node;
import org.apache.ibatis.io.Resources;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;
import org.springframework.web.context.annotation.RequestScope;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
//@RequestScope
@Scope(BeanDefinition.SCOPE_PROTOTYPE)
public class DependenciesDaoImpl implements DependenciesDao {
//    @Autowired
//    @Qualifier("localMapper")
//    private BFSMapper mapper;
    //将静态注入改成动态注入

    private BFSMapper mapper;

    public void setMapper(BFSMapper mapper) {
        this.mapper = mapper;
    }

    private ArrayList<Node> getNodes(List<DbaObjects> allNeighbors, Node parentNode, String dependence_type) {
        ArrayList<Node> nodes = new ArrayList<Node>();

        for (DbaObjects obj : allNeighbors) {
            Node node = new Node(obj.getOwner(), obj.getObjectName(), obj.getobjectType());
            node.setLevel(parentNode);
            node.setDependence_type(dependence_type);
            nodes.add(node);
        }
        return nodes;
    }

    private ArrayList<Node> getIndexes(Map dbaobjMap, Node parantNode) {

        List<DbaObjects> Indexes = mapper.selectIndexes(dbaobjMap);
        return getNodes(Indexes, parantNode, "Indirect");

    }

    private ArrayList<Node> getAllNeighborNode(Map dbaobjMap, Node parantNode) {

        List<DbaObjects> allDirectNeighbors = mapper.selecteDirectDependencies(dbaobjMap);
        // System.out.println("getAllNeighborNode执行成功");
        return getNodes(allDirectNeighbors, parantNode, "Direct");

    }

    private ArrayList<Node> getDblinks(Map dbaobjMap, Node parantNode) {

        List<DbaObjects> dblinks = mapper.selectDBlink(dbaobjMap);
        return getNodes(dblinks, parantNode, "Direct");

    }

    private ArrayList<Node> getTriggers(Map dbaobjMap, Node parantNode) {

        List<DbaObjects> triggers = mapper.selectTrigger(dbaobjMap);
        return getNodes(triggers, parantNode, "Indirect");

    }

    private ArrayList<Node> getSynonym(Map dbaobjMap, Node parantNode) {

        List<DbaObjects> synonym = mapper.selectSynonym(dbaobjMap);
        return getNodes(synonym, parantNode, "Indirect");

    }

    public String getDatabase() {
        String database = mapper.selectDataBase();
        return database;
    }

    public void setDBAObject(Map dbaobjMap, Node node) {
        DbaObjects dbaObjects = mapper.selectDbaObjects(dbaobjMap);
        node.setObjectID(dbaObjects.getObjectID());
        node.setLastDDLTime(dbaObjects.getLastDDLTime());
    }

    private void setIdentifier(SqlSession sqlSession) {
        Map<String, Object> paramMap = new HashMap<String, Object>();
        paramMap.put("clientID", "mybatis");
        sqlSession.selectOne("callSetIdentifier", paramMap);

    }

    private void setAllNodeDBAObject(Map dbaobjMap,List<Node> nodes){
        nodes.forEach((node) -> {
            setDBAObject(dbaobjMap,node);

        });
    }

    private void setAllNodeDatabase(List<Node> nodes, String database) {


        nodes.forEach((node) -> {
            node.setDatabase(database);

        });


    }

    @Override
    public ArrayList<Node> findAllNeighborNode(Node node) {
        String database = getDatabase();

        Map dbaobjMap = new HashMap();
        dbaobjMap.put("owner", node.owner);
        dbaobjMap.put("objectType", node.objectType);
        dbaobjMap.put("objectName", node.objectName);

        //获取所有的边节点，存入allNeighborNode
        ArrayList<Node> allNeighborNode = new ArrayList<Node>();


        allNeighborNode.addAll(getAllNeighborNode(dbaobjMap, node));

        if (node.objectType.equals("TABLE")) {
            allNeighborNode.addAll(getIndexes(dbaobjMap, node));
            allNeighborNode.addAll(getTriggers(dbaobjMap, node));
        }
        allNeighborNode.addAll(getSynonym(dbaobjMap, node));
      //  allNeighborNode.addAll(getDblinks(dbaobjMap, node));//DBlink的DDL有点问题


        // 设置当前节点和边节点的数据库名称
        if (node.getDatabase()==null) {

            node.setDatabase(database);
        }
        if (node.getDataSource()==null){

        }



        //设置当前节点的object_id ,last_ddl_time
        setDBAObject(dbaobjMap, node);

        //放开此注释是为了TreeList服务
        setAllNodeDBAObject(dbaobjMap,allNeighborNode);
        setAllNodeDatabase(allNeighborNode, database);//每个节点都会尝试访问子节点，所以不需要额外对子节点赋值

        return allNeighborNode;


    }


}