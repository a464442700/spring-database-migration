package com.lxf.migration.dao.impl;


import com.lxf.migration.dao.SourceCodeDao;
import com.lxf.migration.mapper.BFSMapper;
import com.lxf.migration.pojo.Node;
import org.apache.ibatis.io.Resources;
import org.apache.ibatis.session.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.sql.Clob;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

@Service
public class SourceCodeDaoImpl implements SourceCodeDao {


//    @Override
//    public void getSourcode(Node node) {
//        //这段代码不起作用
//        String resource = "mybatis-config.xml";
//
//        Map dbaobjMap = new HashMap();
//        dbaobjMap.put("owner", node.owner);
//        dbaobjMap.put("objectType", node.objectType);
//        dbaobjMap.put("objectName", node.objectName);
//
//
//        try {
//            InputStream inputStream = Resources.getResourceAsStream(resource);
//            SqlSessionFactory sqlSessionFactory = (new SqlSessionFactoryBuilder()).build(inputStream);
//            SqlSession sqlSession = sqlSessionFactory.openSession();
//
//
//            String functionName = "callGetDDL";
//            System.out.println("dbaobjMap: " + dbaobjMap);
//
//            sqlSession.select(functionName, dbaobjMap, new ResultHandler() {
//                @Override
//                public void handleResult(ResultContext resultContext) {
//
//                    Map<String, Object> resultMap = (Map<String, Object>) resultContext.getResultObject();
//                    try {
//                        Clob clob = (Clob) resultMap.get("sourceCode");
//                        System.out.println("clob: " + clob);
//                        try (Reader reader = clob.getCharacterStream()) {
//                            char[] buffer = new char[(int) clob.length()];
//                            reader.read(buffer);
//                            String resultString = new String(buffer);
//                            System.out.println("sourceCode: " + resultString);
//                        }
//                    } catch (SQLException | IOException e) {
//                        e.printStackTrace();
//                    }
//                }
//            });
//        } catch (IOException v) {
//            v.printStackTrace();
//        }
//
//
//    }

    @Autowired
    @Qualifier("mapper")
    private BFSMapper mapper;

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
        return sb.toString();
    }

    private String dealPackageCode(String s) {
        String regex = "\r\n(?=CREATE OR REPLACE EDITIONABLE PACKAGE BODY)";
        String replacement = "/\r\n";
        return s.replaceAll(regex, replacement);
    }

    public void getSourcode(Node node) {

        String resource = "mybatis-config.xml";

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

}
