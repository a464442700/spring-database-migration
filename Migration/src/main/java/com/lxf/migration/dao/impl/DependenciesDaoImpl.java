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
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
@Service
public class DependenciesDaoImpl implements DependenciesDao {
    @Autowired
    @Qualifier("localMapper")
    private BFSMapper mapper;
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

    private ArrayList<Node> getIndexes( Map dbaobjMap, Node parantNode) {

        List<DbaObjects> Indexes = mapper.selectIndexes(dbaobjMap);
        return getNodes(Indexes, parantNode, "Indirect");

    }

    private ArrayList<Node> getAllNeighborNode(Map dbaobjMap, Node parantNode) {

        List<DbaObjects> allDirectNeighbors = mapper.selecteDirectDependencies(dbaobjMap);
       // System.out.println("getAllNeighborNode执行成功");
        return getNodes(allDirectNeighbors, parantNode, "Direct");

    }

    private ArrayList<Node> getDblinks( Map dbaobjMap, Node parantNode) {

        List<DbaObjects> dblinks = mapper.selectDBlink(dbaobjMap);
        return getNodes(dblinks, parantNode, "Direct");

    }

    private ArrayList<Node> getTriggers( Map dbaobjMap, Node parantNode) {

        List<DbaObjects> triggers = mapper.selectTrigger(dbaobjMap);
        return getNodes(triggers, parantNode, "Indirect");

    }

    private ArrayList<Node> getSynonym(Map dbaobjMap, Node parantNode) {

        List<DbaObjects> synonym = mapper.selectSynonym(dbaobjMap);
        return getNodes(synonym, parantNode, "Indirect");

    }

    private String getDatabase() {
        String database = mapper.selectDataBase();
        return database;
    }

    private void setIdentifier(SqlSession sqlSession) {
        Map<String, Object> paramMap = new HashMap<String, Object>();
        paramMap.put("clientID", "mybatis");
        sqlSession.selectOne("callSetIdentifier", paramMap);

    }




    private void setAllNodeDatabase(List<Node> nodes, String database) {


        nodes.forEach((node) -> {
            node.setDatabase(database);

        });


    }

    @Override
    public ArrayList<Node> findAllNeighborNode(Node node) {
       // String resource = "mybatis-config.xml";

        Map dbaobjMap = new HashMap();
        dbaobjMap.put("owner", node.owner);
        dbaobjMap.put("objectType", node.objectType);
        dbaobjMap.put("objectName", node.objectName);



//            InputStream inputStream = Resources.getResourceAsStream(resource);
//            SqlSessionFactory sqlSessionFactory = (new SqlSessionFactoryBuilder()).build(inputStream);
//            SqlSession sqlSession = sqlSessionFactory.openSession();
//            // setIdentifier(sqlSession);
//            BFSMapper mapper = (BFSMapper) sqlSession.getMapper(BFSMapper.class);
           String database = getDatabase();
            node.setDatabase(database);
            ArrayList<Node> allNeighborNode = new ArrayList<Node>();


            allNeighborNode.addAll(getAllNeighborNode( dbaobjMap, node));

            if (node.objectType.equals("TABLE")) {
                allNeighborNode.addAll(getIndexes( dbaobjMap, node));
                allNeighborNode.addAll(getTriggers(dbaobjMap, node));
            }
            allNeighborNode.addAll(getSynonym(dbaobjMap, node));
            allNeighborNode.addAll(getDblinks( dbaobjMap, node));


            // sqlSession.close();

            setAllNodeDatabase(allNeighborNode, database);
            return allNeighborNode;




    }


}