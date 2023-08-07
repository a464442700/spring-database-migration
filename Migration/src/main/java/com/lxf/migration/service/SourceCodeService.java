package com.lxf.migration.service;

import com.lxf.migration.dao.impl.SourceCodeDaoImpl;
import com.lxf.migration.exception.InvalidRequestException;
import com.lxf.migration.mapper.BFSMapper;
import com.lxf.migration.pojo.Node;
import com.lxf.migration.thread.impl.BFSThreadPool;
import com.lxf.migration.thread.impl.ThreadPoolImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Scope(BeanDefinition.SCOPE_PROTOTYPE)
public class SourceCodeService {
    @Autowired
    @Qualifier("localMapper")
    private BFSMapper localMapper;

    @Autowired
    @Qualifier("remoteMapper")
    private BFSMapper remoteMapper;


    public void getSourceCode(List<Node> nodes) {

        int size = nodes.size();

        if (size == 0) {
            throw new InvalidRequestException("下载列表不能为空");
        }

        BFSThreadPool threadPool = new ThreadPoolImpl();
        threadPool.init(size);

        for (Node node : nodes) {
            SourceCodeDaoImpl sourceCodeDaoImpl = new SourceCodeDaoImpl();
            if (node.getDataSource().equalsIgnoreCase("local")) {
                sourceCodeDaoImpl.setMapper(localMapper);

            } else if (node.getDataSource().equalsIgnoreCase("remote")) {
                sourceCodeDaoImpl.setMapper(remoteMapper);

            } else {

                throw new InvalidRequestException("数据源错误");
            }
            threadPool.setSourceCode(node, sourceCodeDaoImpl);
        }
        threadPool.shutdownPool();

    }
}
