package com.lxf.migration.service;

import com.lxf.migration.mapper.BFSMapper;
import com.lxf.migration.pojo.ServiceStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class CheckStatusService {
    @Autowired
    private org.springframework.core.env.Environment env;

    @Autowired
    @Qualifier("localMapper")
    private BFSMapper localMapper;

    @Autowired
    @Qualifier("remoteMapper")
    private BFSMapper remoteMapper;
    @Autowired
    private RedisTemplate redisTemplate;

   private String getDataBase(BFSMapper mapper){
       try {
           return  mapper.selectDataBase();
       } catch (Exception e) {
           return null;

       }

   }
    private ServiceStatus getDBMSSessionPrivs(String source) {
        String userName = env.getProperty("spring.datasource.local.username");
        Map map = new HashMap();

        map.put("grantee", userName.toUpperCase());
        map.put("packageName", "DBMS_SESSION");
        Integer count;

        String dataBase =getDataBase(mapper);
        try {
            count = mapper.selectDbaTabPrivs(map);
        } catch (Exception e) {
            count = null;

        }

        if (count==0 ){
          return  new ServiceStatus("please execute [grant create session to "+userName+"] in "+dataBase+ " database", "#990000");

        }else{
          return  null;
        }

    }


    private ServiceStatus getDBMSMetadataPrivs(BFSMapper mapper) {
        String userName = env.getProperty("spring.datasource.local.username");
        Map map = new HashMap();

        map.put("grantee", userName.toUpperCase());
        map.put("packageName", "DBMS_SESSION");
        Integer count;

        String dataBase =getDataBase(mapper);
        try {
            count = mapper.selectDbaTabPrivs(map);
        } catch (Exception e) {
            count = null;

        }

        if (count==0 ){
            return  new ServiceStatus("please execute [grant create session to "+userName+"] in "+dataBase+ " database", "#990000");

        }else{
            return  null;
        }

    }


    public List<ServiceStatus> getServiceStatus() {

        List<ServiceStatus> serviceStatusList = new ArrayList<ServiceStatus>();

        //redis服务
        try {
            // 尝试连接 Redis，通过执行简单的操作来确定 Redis 服务是否生效
            redisTemplate.opsForValue().get("health_check_key");
            serviceStatusList.add(new ServiceStatus("redis is ok", "#52c41a"));


        } catch (Exception e) {
            // 如果连接失败或发生异常，返回 HTTP 503 Service Unavailable
            serviceStatusList.add(new ServiceStatus("redis not work", "#666666"));
        }

        //local
        String localDatabase=getDataBase(localMapper);


        if (localDatabase == null) {
            serviceStatusList.add(new ServiceStatus("local database " + "not work", "#990000"));
        } else {
            serviceStatusList.add(new ServiceStatus("local database [" + localDatabase + "] is ok", "#52c41a"));
        }
        //remote
        String remoteDatabase =getDataBase(remoteMapper);


        if (remoteDatabase == null) {
            serviceStatusList.add(new ServiceStatus("remote database " + "not work", "#990000"));
        } else {
            serviceStatusList.add(new ServiceStatus("remote database [" + remoteDatabase + "] is ok", "#52c41a"));
        }


        return serviceStatusList;
    }


}
