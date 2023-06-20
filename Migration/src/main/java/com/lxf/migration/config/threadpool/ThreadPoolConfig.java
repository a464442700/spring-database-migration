package com.lxf.migration.config.threadpool;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

//@Configuration
public class ThreadPoolConfig {
//
//    @Bean
//    public ExecutorService threadPool() {
//        return Executors.newFixedThreadPool(20);
//    }
}

//threadlocal示例用法
//@Component
//public class SourceCodeDaoThread implements Runnable {
//
//    private final  ThreadLocal<Node> threadLocal = new ThreadLocal<Node>();
//
//    public void setThreadLocal(Node node) {
//        threadLocal.set(node);//这里打印node,是有值的
//    }
//
//    @Override
//    public void run() {
//        try {
//            var node=threadLocal.get();//这里get
//        } catch (Exception e) {
//            e.printStackTrace();
//        } finally {
//            threadLocal.remove();//执行完毕必须remove
//        }
//    }
//}