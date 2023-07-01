package com.lxf.migration.dao.impl;


import com.lxf.migration.common.Hash;
import com.lxf.migration.dao.SourceCodeDao;
import com.lxf.migration.mapper.BFSMapper;
import com.lxf.migration.model.ServiceName;
import com.lxf.migration.pojo.Node;
import org.apache.ibatis.io.Resources;
import org.apache.ibatis.session.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Scope;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.context.annotation.RequestScope;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;


import oracle.sql.CLOB;


import java.sql.Clob;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;

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

    public void getSourcode(Node node) {
        getSourcodeFromDatabase(node);

    }

    //判断是否选择缓存还是数据库
    public ServiceName getServiceName(Node node) {
        //如果redis服务不可用，从数据库读取
        if (!isRedisEnable(node)){
            return ServiceName.database;
        }
       //如果redis可用，但是


        return ServiceName.database;
    }


    public boolean isRedisEnable(Node node) {

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
