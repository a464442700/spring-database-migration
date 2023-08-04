package com.lxf.migration.dao.impl;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.lxf.migration.common.Hash;
import com.lxf.migration.dao.SourceCodeDao;
import com.lxf.migration.mapper.BFSMapper;
import com.lxf.migration.model.ServiceName;
import com.lxf.migration.pojo.DbaObjects;
import com.lxf.migration.pojo.Node;
import org.apache.ibatis.io.Resources;
import org.apache.ibatis.session.*;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Scope;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.context.annotation.RequestScope;

import java.io.*;


import oracle.sql.CLOB;


import java.sql.Clob;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Service
//@RequestScope
@Scope(BeanDefinition.SCOPE_PROTOTYPE)
public class SourceCodeDaoImpl implements SourceCodeDao {


    //    @Autowired
//    private ExecutorService threadPool;
//    @Override
//    public void run() {
//        getSourcode( node)
//
//    }
    private BFSMapper mapper;




    @Autowired
    private RedisTemplate redisTemplate;

    public void setMapper(BFSMapper mapper) {
        this.mapper = mapper;
    }

    public static String convertClobToString(Clob clob) throws SQLException, IOException {
        if (clob == null) {
            return "";
        }
        StringBuilder sb = new StringBuilder();
        Reader reader = clob.getCharacterStream();
        BufferedReader br = new BufferedReader(reader);
        String line;
        while ((line = br.readLine()) != null) {
            sb.append(line).append("\r\n");
        }
        reader.close();
        return sb.toString();
    }


    private String dealPackageCode(String s) {
        String regex = "\r\n(?=CREATE OR REPLACE EDITIONABLE PACKAGE BODY)";
        String replacement = "/\r\n";
        return s.replaceAll(regex, replacement);
    }

    public void setDbaObjects(Node node) {
        if (node.objectID == null) {
            Map dbaobjMap = new HashMap();
            dbaobjMap.put("owner", node.owner);
            dbaobjMap.put("objectType", node.objectType);
            dbaobjMap.put("objectName", node.objectName);
            DbaObjects dbaObjects = mapper.selectDbaObjects(dbaobjMap);
            node.setObjectID(dbaObjects.getObjectID());
            node.setLastDDLTime(dbaObjects.getLastDDLTime());
            if (dbaObjects.getObjectID() == null) {
                System.out.println("无法获取object_id:" + node.objectName);
            }
        }
        if (node.database == null) {
            String database = mapper.selectDataBase();
            node.setDatabase(database);
        }


    }

    public void getSourcode(Node node) {
        setDbaObjects(node);
        Node redisNode = null;
        ServiceName seviceName = ServiceName.database;
        String hashKey = null;
        String key = "SOURCECODE" + ":" + node.database;
        if (!(node.objectID == null)) {
            hashKey = node.objectID.toString();
        }
        //判断redis服务是否可用,如果不可用，从数据库读取


        //  getSourcodeFromDatabase(node);


        //判断redis是否存在此键 SOURCECODE:DATABASE

        if (!isRedisEnable() || (hashKey == null) || hashKey.isEmpty() || !redisTemplate.hasKey(key) ||
                !redisTemplate.opsForHash().hasKey(key, hashKey)

        ) {
            seviceName = ServiceName.database;
        } else {
            try {
                //     var obj =redisTemplate.opsForHash().get(key, hashKey);
                //System.out.println("反序列化对象："+redisTemplate.opsForHash().get(key, hashKey).getClass());
                LinkedHashMap<String, Object> redisNodeHashMap = (LinkedHashMap<String, Object>) redisTemplate.opsForHash().get(key, hashKey);
                // System.out.println("反序列化对象："+redisNodeHashMap.getClass());

                ObjectMapper objectMapper = new ObjectMapper();
                String redisNodeJson = objectMapper.writeValueAsString(redisNodeHashMap);

                Jackson2JsonRedisSerializer<Node> serializer = new Jackson2JsonRedisSerializer<>(Node.class);
                redisNode = serializer.deserialize(redisNodeJson.getBytes());
                //   System.out.println("反序列化对象："+redisNode.getClass());
                //redisNode = (Node) redisTemplate.opsForHash().get(key, hashKey);
                if (!redisNode.lastDDLTime.equals(node.lastDDLTime)) {
                    seviceName = ServiceName.database;
                } else {
                    seviceName = ServiceName.redis;
                }
            } catch (Exception e) {
                e.printStackTrace();
                seviceName = ServiceName.database;
            }

        }
        if (seviceName.equals(ServiceName.database)) {
            getSourcodeFromDatabase(node);
            //将node刷入redis
            if (isRedisEnable() && !(hashKey == null) && !(node == null)) {
                try {
                    // SerializationCheck(node);
                    redisTemplate.opsForHash().put(key, hashKey, node);
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }

        } else if (!(redisNode.getSourceCode() == null) && !(redisNode == null)) {
            //  System.out.println("从redis获取对象：" + redisNode.objectName+redisNode.getSourceCode().substring(1,10));
            //     node = redisNode;
            BeanUtils.copyProperties(redisNode, node);
            //  System.out.println(node.getSourceCode().substring(1, 10));
        }


    }

//    public void SerializationCheck(Node node) {
//        try {
//            // 尝试将对象序列化到字节数组
//            ByteArrayOutputStream baos = new ByteArrayOutputStream();
//            ObjectOutputStream oos = new ObjectOutputStream(baos);
//            oos.writeObject(node);
//
//            System.out.println("Object is serializable");
//        } catch (IOException e) {
//            System.out.println("Object is not serializable: " + e.getMessage());
//        }
//
//    }


    public boolean isRedisEnable() {

        try {
            // 尝试连接 Redis，通过执行简单的操作来确定 Redis 服务是否生效
            redisTemplate.opsForValue().get("hello");
            return true;
        } catch (Exception e) {
            return false;
        }


    }

    //从数据库中读取DDL文件
    public void getSourcodeFromDatabase(Node node) {


        Map dbaobjMap = new HashMap();
        dbaobjMap.put("owner", node.owner);

        if (node.objectType.equals("DATABASE LINK")) {

            dbaobjMap.put("objectType", "DB_LINK");
        } else {
            dbaobjMap.put("objectType", node.objectType);
        }

        dbaobjMap.put("objectName", node.objectName);

//            InputStream inputStream = Resources.getResourceAsStream(resource);
//            SqlSessionFactory sqlSessionFactory = (new SqlSessionFactoryBuilder()).build(inputStream);
//            SqlSession sqlSession = sqlSessionFactory.openSession();
//            BFSMapper mapper = (BFSMapper) sqlSession.getMapper(BFSMapper.class);

        mapper.callGetDDL(dbaobjMap);
        //System.out.println(dbaobjMap.get("sourceCode").getClass());
        Clob sourceClob = (Clob) dbaobjMap.get("sourceCode");
        try {
            String sourCode = convertClobToString(sourceClob);


            if (node.objectType.equals("PACKAGE")) {
                sourCode = dealPackageCode(sourCode);

            }
            node.setSourceCode(sourCode);

        } catch (Exception e) {
            System.out.println("获取sourceCode异常:" + node.objectName);
            e.printStackTrace();
        }

    }


    public void getSourcodeHash(Node node) {

        String resource = "mybatis-config.xml";

        Map dbaobjMap = new HashMap();
        dbaobjMap.put("owner", node.owner);
        if (node.objectType.equals("DATABASE LINK")) {

            dbaobjMap.put("objectType", "DB_LINK");
        } else {
            dbaobjMap.put("objectType", node.objectType);
        }


        dbaobjMap.put("objectName", node.objectName);

        // InputStream inputStream = Resources.getResourceAsStream(resource);
        // SqlSessionFactory sqlSessionFactory = (new SqlSessionFactoryBuilder()).build(inputStream);
        // SqlSession sqlSession = sqlSessionFactory.openSession();
        // BFSMapper mapper = (BFSMapper) sqlSession.getMapper(BFSMapper.class);
        mapper.callGetHashCode(dbaobjMap);
        String sourceCodeHash = (String) dbaobjMap.get("sourceCodeHash");
        node.setSourceCodeHash(sourceCodeHash);

        //System.out.println("sourceCodeHash"+sourceCodeHash);


    }

    @Override
    public void getSourcodeHashSHA256(Node node) throws Exception {
        String sourceCodeHash = Hash.getSha256(node.getSourceCode());
        node.setSourceCodeHash(sourceCodeHash);
    }


}
