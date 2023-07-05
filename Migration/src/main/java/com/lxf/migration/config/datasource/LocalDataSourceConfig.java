package com.lxf.migration.config.datasource;


import com.lxf.migration.interceptor.SessionInterceptor;
import com.lxf.migration.mapper.BFSMapper;
import org.apache.ibatis.datasource.pooled.PooledDataSource;
import org.apache.ibatis.plugin.Interceptor;
import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.mybatis.spring.SqlSessionTemplate;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import javax.sql.DataSource;
//
//@Configuration
//@MapperScan(basePackages = LocalDataSourceConfig.PACKAGE, sqlSessionFactoryRef = "localSqlSessionFactory")
//public class LocalDataSourceConfig {
//
//    static final String PACKAGE = "com.lxf.migration.mapper";
//    static final String MAPPER_LOCATION = "classpath:/com/lxf/migration/mapper/*.xml";
//
//
//    @Value("${spring.datasource.local.url}")
//    private String url;
//
//    @Value("${spring.datasource.local.username}")
//    private String user;
//
//    @Value("${spring.datasource.local.password}")
//    private String password;
//
//    @Value("${spring.datasource.local.driver-class-name}")
//    private String driverClass;
//
//    @Bean(name = "localDataSource")
//    @Primary
//    public DataSource localDataSource() {
//        //DruidDataSource dataSource = new DruidDataSource();
//        PooledDataSource dataSource = new PooledDataSource();
//        dataSource.setDriver(driverClass);
//        dataSource.setUrl(url);
//        dataSource.setUsername(user);
//        dataSource.setPassword(password);
//        return dataSource;
//    }
//
//    @Bean(name = "localSqlSessionFactory")
//    @Primary
//    public SqlSessionFactory localSqlSessionFactory(@Qualifier("localDataSource") DataSource dataSource) throws Exception {
//        SqlSessionFactoryBean bean = new SqlSessionFactoryBean();
//        bean.setDataSource(dataSource);
//        return bean.getObject();
//    }
//
//    // 创建SqlSessionTemplate
//    @Bean(name = "localSqlSessionTemplate")
//    @Primary
//    public SqlSessionTemplate localSqlSessionTemplate(@Qualifier("localSqlSessionFactory") SqlSessionFactory sqlSessionFactory) throws Exception {
//        System.out.println("sqlSessionFactory"+sqlSessionFactory);
//        sqlSessionFactory.getConfiguration().addMapper(BFSMapper.class);
//        return new SqlSessionTemplate(sqlSessionFactory);
//    }
//
//    @Bean(name = "mapper")
//    @Primary
//    public BFSMapper mapper(@Qualifier("localSqlSessionTemplate") SqlSessionTemplate localSqlSessionTemplate) throws Exception {
//        System.out.println(localSqlSessionTemplate);
//        return  localSqlSessionTemplate.getMapper(BFSMapper.class);
//    }
//}


@Configuration

@MapperScan(basePackages = "com.lxf.migration.mapper", sqlSessionTemplateRef = "localSqlSessionTemplate")
public class LocalDataSourceConfig {
    @Autowired
    private org.springframework.core.env.Environment env;

//    @Autowired
//    private DataSourceProperty localDataSourceProperty;

    @Bean
    public SessionInterceptor sessionInterceptor() {
        return new SessionInterceptor();
    }

//    @Autowired
//    public DataSourceContextHolder dataSourceContextHolder;

    // 创建数据源
//    @RefreshScope
//    @Lazy
    @Bean(name = "localDataSource")
    public DataSource localDataSource(


    ) {
        //动态注册数据源，但是@RefreshScope没有生效，暂时只能使用配置文件获取
//        DataSourceWrapper dataSourceWrapper= dataSourceContextHolder.getCurrentDataSourceWrapper();
//        DataSourceProperty localDataSourceProperty=dataSourceWrapper.getLocalDataSource();
//        String driver = localDataSourceProperty.getDriver();
//        String username = localDataSourceProperty.getUsername();
//        String password = localDataSourceProperty.getPassword();
//        String url = localDataSourceProperty.getUrl();
//        PooledDataSource dataSource = new PooledDataSource(
//                driver, url, username, password
//
//        );
        PooledDataSource dataSource = new PooledDataSource();

        String driver = env.getProperty("spring.datasource.local.driver-class-name");
        String userName = env.getProperty("spring.datasource.local.username");
        String passWord = env.getProperty("spring.datasource.local.password");
        String url = env.getProperty("spring.datasource.local.url");
        dataSource.setDriver(driver);
        dataSource.setUrl(url);
        dataSource.setUsername(userName);
        dataSource.setPassword(passWord);
        dataSource.setPoolMaximumActiveConnections(30);
        dataSource.setPoolMaximumIdleConnections(20);
        dataSource.setPoolMaximumCheckoutTime(30000);
        dataSource.setPoolTimeToWait(30000);//重试获取连接之前等待的时间
        dataSource.setPoolPingEnabled(true);//确定是否应使用 ping 查询。
        dataSource.setPoolPingQuery("SELECT 1 FROM DUAL");
        dataSource.setPoolPingConnectionsNotUsedFor(30000);//如果在30秒内未使用连接，请 ping 数据库以确保连接仍在 好。
        dataSource.setDefaultAutoCommit(false);
        
        //  System.out.println("注册datasource成功");
        return dataSource;
    }


    // 创建SqlSessionFactory
    @Bean(name = "localSqlSessionFactory")
    @Primary
    public SqlSessionFactory localSqlSessionFactory(@Qualifier("localDataSource") DataSource dataSource) throws Exception {
        SqlSessionFactoryBean bean = new SqlSessionFactoryBean();
        bean.setPlugins(new Interceptor[]{sessionInterceptor()});//只在local注入拦截器，remote一般是正式环境
        bean.setDataSource(dataSource);
        return bean.getObject();
    }

    // 创建SqlSessionTemplate
    @Bean(name = "localSqlSessionTemplate")
    @Primary
    public SqlSessionTemplate localSqlSessionTemplate(@Qualifier("localSqlSessionFactory") SqlSessionFactory sqlSessionFactory) throws Exception {

        sqlSessionFactory.getConfiguration().addMapper(BFSMapper.class);
        return new SqlSessionTemplate(sqlSessionFactory);
    }

    @Bean(name = "localMapper")
    @Primary
    public BFSMapper localMapper(@Qualifier("localSqlSessionTemplate") SqlSessionTemplate localSqlSessionTemplate) throws Exception {
//System.out.println("注册mapper成功");
        return localSqlSessionTemplate.getMapper(BFSMapper.class);
    }


//    public SessionInterceptor sessionInterceptor(
//            @Qualifier(value ="localMapper" )   BFSMapper localMapper
//    ) {
//        SessionInterceptor interceptor = new SessionInterceptor();
//        interceptor.setMapper(localMapper);
//        return interceptor;
//    }
}
