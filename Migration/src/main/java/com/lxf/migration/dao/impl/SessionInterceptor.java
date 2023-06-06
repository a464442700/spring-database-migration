package com.lxf.migration.dao.impl;

import com.lxf.migration.mapper.BFSMapper;
import org.apache.ibatis.executor.statement.StatementHandler;
import org.apache.ibatis.plugin.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

import java.sql.Connection;
import java.util.HashMap;
import java.util.Map;

@Intercepts({
        @Signature(type = StatementHandler.class, method = "prepare", args = {Connection.class, Integer.class})
})
public class SessionInterceptor implements Interceptor {
    public void setMapper(BFSMapper mapper) {
        this.mapper = mapper;
    }
    @Qualifier(value ="localMapper" )
    @Autowired
    private BFSMapper mapper;
    @Override
    public Object intercept(Invocation invocation) throws Throwable {
        // 调用存储过程
        callProcedure(invocation);
        // 执行原来的操作
        return invocation.proceed();
    }

    private void callProcedure(Invocation invocation) {
     //   System.out.println("生效拦截器");
        Map dbaobjMap = new HashMap();
        dbaobjMap.put("clientID", "mybatis");

        mapper.callSetIdentifier(dbaobjMap);
        //暂时不知道如何获取sqlsession
    }
//    @Override
//    public Object plugin(Object target) {
//        return Plugin.wrap(target, this);
//    }
}
