package com.lxf.migration.dao.impl;

import org.apache.ibatis.executor.statement.StatementHandler;
import org.apache.ibatis.plugin.*;

import java.sql.Connection;

@Intercepts({
        @Signature(type = StatementHandler.class, method = "prepare", args = {Connection.class, Integer.class})
})
public class SessionInterceptor implements Interceptor {
    @Override
    public Object intercept(Invocation invocation) throws Throwable {
        // 调用存储过程
        callProcedure(invocation);
        // 执行原来的操作
        return invocation.proceed();
    }

    private void callProcedure(Invocation invocation) {
        System.out.println("生效拦截器");
        //暂时不知道如何获取sqlsession
    }
}
