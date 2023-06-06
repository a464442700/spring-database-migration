package com.lxf.migration.config;

import com.alibaba.druid.pool.DruidDataSource;
import com.lxf.migration.dao.impl.SessionInterceptor;
import com.lxf.migration.mapper.BFSMapper;
import org.apache.ibatis.datasource.pooled.PooledDataSource;
import org.apache.ibatis.plugin.Interceptor;
import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.mybatis.spring.SqlSessionTemplate;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;

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

    @Bean
    public SessionInterceptor sessionInterceptor() {
        return new SessionInterceptor();
    }

    // 创建数据源
    @Bean(name = "localDataSource")
    public DataSource localDataSource() {
        //DruidDataSource dataSource = new DruidDataSource();
        PooledDataSource dataSource = new PooledDataSource();
        String driver = env.getProperty("spring.datasource.local.driver-class-name");
        String userName = env.getProperty("spring.datasource.local.username");
        String passWord = env.getProperty("spring.datasource.local.password");
        String url = env.getProperty("spring.datasource.local.url");
        dataSource.setDriver(driver);
        dataSource.setUrl( url);
        dataSource.setUsername(userName);
        dataSource.setPassword( passWord);
        return dataSource;
    }


    // 创建SqlSessionFactory
    @Bean(name = "localSqlSessionFactory")
    @Primary
    public SqlSessionFactory localSqlSessionFactory(@Qualifier("localDataSource") DataSource dataSource) throws Exception {
        SqlSessionFactoryBean bean = new SqlSessionFactoryBean();
        bean.setPlugins(new Interceptor[]{sessionInterceptor()});//拦截器
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

        return  localSqlSessionTemplate.getMapper(BFSMapper.class);
    }


//    public SessionInterceptor sessionInterceptor(
//            @Qualifier(value ="localMapper" )   BFSMapper localMapper
//    ) {
//        SessionInterceptor interceptor = new SessionInterceptor();
//        interceptor.setMapper(localMapper);
//        return interceptor;
//    }
}
