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

    private boolean localConnect;
    private boolean remoteConnect;

    private String getDataBase(BFSMapper mapper) {
        try {
            return mapper.selectDataBase();
        } catch (Exception e) {
            return null;

        }

    }

    private ServiceStatus getDbaTabPrivs(String source, String packageName) {
        String userName = env.getProperty("spring.datasource." + source + ".username");
        Map map = new HashMap();

        map.put("grantee", userName.toUpperCase());
        map.put("packageName", packageName);
        Integer count = null;
        BFSMapper mapper = null;
        if (source.equalsIgnoreCase("local")) {
            mapper = localMapper;
        } else if (source.equalsIgnoreCase("remote")) {
            mapper = remoteMapper;
        }

        String dataBase = getDataBase(mapper);
        String dataBaseResult = (dataBase != null) ? dataBase : source;

        try {
            count = mapper.selectDbaTabPrivs(map);
        } catch (Exception e) {
            count =0;

        }

        if ( count == 0) {
            return new ServiceStatus("please execute [grant execute on " + packageName + " to " + userName + "] in " + dataBaseResult + " database", "#990000");

        } else {
            return null;
        }

    }


    private ServiceStatus getDbaSysPrivs(String source, String privilege) {
        if (!((localConnect && source.equalsIgnoreCase("local"))
                || (remoteConnect && source.equalsIgnoreCase("remote")
        ))) {
            return null;
        }
        String userName = env.getProperty("spring.datasource." + source + ".username");
        Map map = new HashMap();

        map.put("grantee", userName.toUpperCase());
        map.put("privilege", privilege);
        Integer count = null;
        BFSMapper mapper = null;
        if (source.equalsIgnoreCase("local")) {
            mapper = localMapper;
        } else if (source.equalsIgnoreCase("remote")) {
            mapper = remoteMapper;
        }

        String dataBase = getDataBase(mapper);
        String dataBaseResult = (dataBase != null) ? dataBase : source;
        try {
            count = mapper.selectDbaSysPrivs(map);
        } catch (Exception e) {
            count = 0;

        }

        if ( count == 0) {
            return new ServiceStatus("please execute [grant " + privilege + " to " + userName + "] in " + dataBaseResult + " database", "#990000");

        } else {
            return null;
        }

    }


    public List<ServiceStatus> getServiceStatus() {

        List<ServiceStatus> serviceStatusList = new ArrayList<ServiceStatus>();
        ServiceStatus serviceStatus;

        //redis服务
        try {
            // 尝试连接 Redis，通过执行简单的操作来确定 Redis 服务是否生效
            redisTemplate.opsForValue().get("health_check_key");
            serviceStatusList.add(new ServiceStatus("redis is ok", "#52c41a"));


        } catch (Exception e) {
            // 如果连接失败或发生异常，返回 HTTP 503 Service Unavailable
            serviceStatusList.add(new ServiceStatus("redis not work", "#666666"));
        }


        if (localMapper.checkConnectStatus() == null) {

            serviceStatusList.add(new ServiceStatus("local database cannot connect,please check database is enable , or username can connect database", "#990000"));
        } else {

            this.localConnect = true;
        }
        if (remoteMapper.checkConnectStatus() == null) {

            serviceStatusList.add(new ServiceStatus("remote database cannot connect,please check database is enable , or username can connect database", "#990000"));
        } else {
            this.remoteConnect = true;
        }

        //local
        String localDatabase = getDataBase(localMapper);


        if (!(localDatabase == null)) {

            serviceStatusList.add(new ServiceStatus("local database [" + localDatabase + "] is ok", "#52c41a"));
        }
        //remote
        String remoteDatabase = getDataBase(remoteMapper);


        if (!(remoteDatabase == null)) {
            serviceStatusList.add(new ServiceStatus("remote database [" + remoteDatabase + "] is ok", "#52c41a"));
        }

//=====================================================///
        //local
        serviceStatus = getDbaTabPrivs("local", "DBMS_METADATA");
        if (serviceStatus != null) {
            serviceStatusList.add(serviceStatus);

        }

        //remote

        serviceStatus = getDbaTabPrivs("remote", "DBMS_METADATA");
        if (serviceStatus != null) {
            serviceStatusList.add(serviceStatus);

        }
//=====================================================///

        //local
        serviceStatus = getDbaSysPrivs("local", "SELECT ANY DICTIONARY");
        if (serviceStatus != null) {
            serviceStatusList.add(serviceStatus);

        }

        //remote

        serviceStatus = getDbaSysPrivs("remote", "SELECT ANY DICTIONARY");
        if (serviceStatus != null) {
            serviceStatusList.add(serviceStatus);

        }
//=====================================================///

//        //local
//        serviceStatus=  getDbaSysPrivs("local", "CREATE SESSION");
//        if (serviceStatus !=null){
//            serviceStatusList.add(serviceStatus);
//
//        }
//
//        //remote
//
//        serviceStatus=  getDbaSysPrivs("remote", "CREATE SESSION");
//        if (serviceStatus !=null){
//            serviceStatusList.add(serviceStatus);
//
//        }
//=====================================================///


        return serviceStatusList;
    }


}
