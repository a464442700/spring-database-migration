package com.lxf.migration.service;

import com.lxf.migration.dao.impl.SourceCodeDaoImpl;
import com.lxf.migration.exception.InvalidRequestException;
import com.lxf.migration.mapper.BFSMapper;
import com.lxf.migration.pojo.Node;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Scope;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Scope(BeanDefinition.SCOPE_PROTOTYPE)
public class SourceCodeParalleService {
    @Autowired
    @Qualifier("localMapper")
    private BFSMapper localMapper;

    @Autowired
    @Qualifier("remoteMapper")
    private BFSMapper remoteMapper;
    @Autowired
    private RedisTemplate redisTemplate;

    private SourceCodeDaoImpl sourceCodeDaoImpl;

    private void setSourceCode(Node node) throws Exception {


        SourceCodeDaoImpl sourceCodeDaoImpl = new SourceCodeDaoImpl();
        sourceCodeDaoImpl.setRedisTemplate(redisTemplate);
        if (node.getDataSource().equalsIgnoreCase("local")) {
            sourceCodeDaoImpl.setMapper(localMapper);

        } else if (node.getDataSource().equalsIgnoreCase("remote")) {
            sourceCodeDaoImpl.setMapper(remoteMapper);

        } else {

            throw new InvalidRequestException("数据源错误");
        }



        sourceCodeDaoImpl.getSourcode(node);

        sourceCodeDaoImpl.getSourcodeHashSHA256(node);
    }


    public void getSourceCode(List<Node> nodes) {

        nodes.parallelStream().forEach(node -> {
            try {
                setSourceCode(node);
            } catch (Exception e) {
                e.printStackTrace();
            }

        });

    }
}
