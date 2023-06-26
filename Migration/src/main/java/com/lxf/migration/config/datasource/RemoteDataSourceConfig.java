package com.lxf.migration.config.datasource;

import com.lxf.migration.mapper.BFSMapper;
import org.apache.ibatis.datasource.pooled.PooledDataSource;
import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.mybatis.spring.SqlSessionTemplate;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.context.annotation.Primary;

import javax.sql.DataSource;



@Configuration

@MapperScan(basePackages = "com.lxf.migration.mapper", sqlSessionTemplateRef = "remoteSqlSessionTemplate")
public class RemoteDataSourceConfig {
    @Autowired
    private org.springframework.core.env.Environment env;
    // 创建数据源
    @Bean(name = "remoteDataSource")
    @RefreshScope
    public DataSource remoteDataSource() {
        //DruidDataSource dataSource = new DruidDataSource();
        PooledDataSource dataSource = new PooledDataSource();
        String driver = env.getProperty("spring.datasource.remote.driver-class-name");
        String userName = env.getProperty("spring.datasource.remote.username");
        String passWord = env.getProperty("spring.datasource.remote.password");
        String url = env.getProperty("spring.datasource.remote.url");
        dataSource.setDriver(driver);
        dataSource.setUrl( url);
        dataSource.setUsername(userName);
        dataSource.setPassword( passWord);
        dataSource.setPoolMaximumActiveConnections(30);
        dataSource.setPoolMaximumIdleConnections(20);
        dataSource.setPoolMaximumCheckoutTime(30000);
        dataSource.setPoolTimeToWait(30000);
        dataSource.setPoolPingEnabled(true);
        dataSource.setPoolPingQuery("SELECT 1 FROM DUAL");
        dataSource.setPoolPingConnectionsNotUsedFor(30000);
        dataSource.setDefaultAutoCommit(false);
        return dataSource;
    }


    // 创建SqlSessionFactory
    @Bean(name = "remoteSqlSessionFactory")
    @Primary
    public SqlSessionFactory remoteSqlSessionFactory(@Qualifier("remoteDataSource") DataSource dataSource) throws Exception {
        SqlSessionFactoryBean bean = new SqlSessionFactoryBean();
        bean.setDataSource(dataSource);
        return bean.getObject();
    }

    // 创建SqlSessionTemplate
    @Bean(name = "remoteSqlSessionTemplate")
    @Primary
    public SqlSessionTemplate remoteSqlSessionTemplate(@Qualifier("remoteSqlSessionFactory") SqlSessionFactory sqlSessionFactory) throws Exception {

        sqlSessionFactory.getConfiguration().addMapper(BFSMapper.class);
        return new SqlSessionTemplate(sqlSessionFactory);
    }

    @Bean(name = "remoteMapper")
    @Primary
    public BFSMapper remoteMapper(@Qualifier("remoteSqlSessionTemplate") SqlSessionTemplate remoteSqlSessionTemplate) throws Exception {

        return  remoteSqlSessionTemplate.getMapper(BFSMapper.class);
    }

}
