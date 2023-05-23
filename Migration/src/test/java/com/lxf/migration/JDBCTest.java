package com.lxf.migration;

import com.lxf.migration.config.MyBatisConfig;
import com.lxf.migration.mapper.BFSMapper;
import org.apache.ibatis.io.Resources;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.ComponentScan;

import java.io.IOException;
import java.io.InputStream;

public class JDBCTest {


    public static void main(String[] args) {

        var context = new AnnotationConfigApplicationContext(MyBatisConfig.class);
        BFSMapper mapper = context.getBean(BFSMapper.class);
        System.out.println("打印mapper:");
        System.out.println(mapper);
        String database = mapper.selectDataBase();
        System.out.println("database" + database);
    }
}
