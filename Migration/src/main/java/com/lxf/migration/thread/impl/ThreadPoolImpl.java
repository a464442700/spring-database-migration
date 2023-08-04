package com.lxf.migration.thread.impl;
import com.lxf.migration.dao.impl.SourceCodeDaoImpl;
import com.lxf.migration.thread.impl.BFSThreadPool;
import com.lxf.migration.dao.impl.SourceCodeDaoThread;
import com.lxf.migration.pojo.Node;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
@Service
@Scope(BeanDefinition.SCOPE_PROTOTYPE)
public class ThreadPoolImpl implements BFSThreadPool {
    private ExecutorService threadPool;
    //线程池开启
    public void init(Integer threadPoolSize) {
        int threadCount = (threadPoolSize != null) ? threadPoolSize : 20;
        this.threadPool= Executors.newFixedThreadPool(threadCount);
    }

    //线程池执行任务
    public void setSourceCode(Node node, SourceCodeDaoImpl s) {
        SourceCodeDaoThread sourceCodeDaoThread = new SourceCodeDaoThread(node, s);

        threadPool.execute(sourceCodeDaoThread);

    }

    //销毁线程池
    public void shutdownPool() {

        threadPool.shutdown();

        while (!threadPool.isTerminated()) {//循环是要判断所有的线程是否关闭
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {

                e.printStackTrace();
            }
        }

    }
}
