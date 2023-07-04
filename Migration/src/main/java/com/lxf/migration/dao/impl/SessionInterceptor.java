package com.lxf.migration.dao.impl;

import com.lxf.migration.mapper.BFSMapper;
import org.apache.ibatis.executor.statement.StatementHandler;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.plugin.*;
import org.apache.ibatis.reflection.MetaObject;
import org.apache.ibatis.reflection.SystemMetaObject;
import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

import java.sql.Connection;
import java.util.HashMap;
import java.util.Map;

@Intercepts({
        @Signature(type = StatementHandler.class, method = "prepare", args = {Connection.class, Integer.class})
})
public class SessionInterceptor implements Interceptor {
    //    public void setMapper(BFSMapper mapper) {
//        this.mapper = mapper;
//    }
    @Lazy
    @Qualifier(value = "localMapper")
    @Autowired
    private BFSMapper localMapper;

    @Override
    public Object intercept(Invocation invocation) throws Throwable {


        StatementHandler statementHandler = (StatementHandler) invocation.getTarget();
        MetaObject metaObject = SystemMetaObject.forObject(statementHandler);
        MappedStatement mappedStatement = (MappedStatement) metaObject.getValue("delegate.mappedStatement");
        String id = mappedStatement.getId();

        if (!id.contains("callSetIdentifier")) {//这个判断防止循环拦截，因为拦截器本来是拦截sql执行，结果拦截器本身又执行了sql
         //   callProcedure(invocation);   // 调用存储过程
            //注释掉拦截器代码
        }




        // 执行原来的操作
        return invocation.proceed();
    }

    private void callProcedure(Invocation invocation) {

        Map dbaobjMap = new HashMap();
        dbaobjMap.put("clientID", "mybatis");//用于性能分析

        localMapper.callSetIdentifier(dbaobjMap);

    }

}
