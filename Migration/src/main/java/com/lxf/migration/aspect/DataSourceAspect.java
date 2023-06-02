package com.lxf.migration.aspect;

import com.lxf.migration.dao.impl.DependenciesDaoImpl;
import com.lxf.migration.mapper.BFSMapper;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class DataSourceAspect {

    @Autowired
    @Qualifier("localMapper")
    private BFSMapper localMapper;

    @Autowired
    @Qualifier("remoteMapper")
    private BFSMapper remoteMapper;

    @Around("execution(* com.lxf.migration.service.BFS.setStartNode(..)) && args(startNode)")
    public Object around(ProceedingJoinPoint joinPoint, String database) throws Throwable {
        DependenciesDaoImpl dao = (DependenciesDaoImpl)joinPoint.getTarget();
        if("local".equals(database)){
            dao.setMapper(localMapper);
        }else{
            dao.setMapper(remoteMapper);
        }
        return joinPoint.proceed();
    }

}