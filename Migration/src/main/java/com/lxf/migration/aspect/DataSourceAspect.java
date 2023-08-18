package com.lxf.migration.aspect;

import com.lxf.migration.dao.impl.DependenciesDaoImpl;
import com.lxf.migration.dao.impl.SourceCodeDaoImpl;
import com.lxf.migration.mapper.BFSMapper;
import com.lxf.migration.service.BFS;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.RedisTemplate;
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

//    @Autowired
//    private RedisTemplate redisTemplate;
//切面只注入了mapper
    @Around("execution(* com.lxf.migration.service.BFS.setDataSource(..)) && args(dataSource)")
    public Object around(ProceedingJoinPoint joinPoint, String dataSource) throws Throwable {
        System.out.println("切面执行");
        BFS bfs = (BFS) joinPoint.getTarget();
        DependenciesDaoImpl dependenciesDao = bfs.getDependenciesDaoImpl();
        SourceCodeDaoImpl sourceCodeDao = bfs.getSourceCodeDaoImpl();
        if ("local".equalsIgnoreCase(dataSource)) {
            dependenciesDao.setMapper(localMapper);
            sourceCodeDao.setMapper(localMapper);
           // sourceCodeDao.setRedisTemplate(redisTemplate);

        } else if ("remote".equalsIgnoreCase(dataSource)) {
            dependenciesDao.setMapper(remoteMapper);
            sourceCodeDao.setMapper(remoteMapper);
            //sourceCodeDao.setRedisTemplate(redisTemplate);
        }
        return joinPoint.proceed();
    }

}