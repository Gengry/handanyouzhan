package com.geng.hd.youzhan.config;

import com.alibaba.druid.pool.DruidDataSource;
import com.github.pagehelper.PageInterceptor;
import org.apache.ibatis.plugin.Interceptor;
import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.bind.RelaxedPropertyResolver;
import org.springframework.context.EnvironmentAware;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.sql.DataSource;
import java.sql.SQLException;
import java.util.Properties;

@Configuration
@EnableTransactionManagement
@MapperScan(value = "com.zhonglianxs.erp.cpw.mapper")
public class DataBaseConfiguration implements EnvironmentAware{

    private Environment environment;
    private RelaxedPropertyResolver propertyResolver;
    @Override
    public void setEnvironment(Environment environment) {
        this.environment = environment;
        this.propertyResolver = new RelaxedPropertyResolver(environment,"spring.datasource.");
    }

    @Bean(name = "druidDataSource" ,initMethod = "init",destroyMethod = "close")
    public DruidDataSource dataSource(){
        DruidDataSource druidDataSource = new DruidDataSource();
        druidDataSource.setDriverClassName(propertyResolver.getProperty("driverClassName"));
        druidDataSource.setUrl(propertyResolver.getProperty("url"));
        druidDataSource.setUsername(propertyResolver.getProperty("username"));
        druidDataSource.setPassword(propertyResolver.getProperty("password"));
        druidDataSource.setMaxActive(20);
        druidDataSource.setInitialSize(1);
        druidDataSource.setMaxWait(60000);
        druidDataSource.setTimeBetweenEvictionRunsMillis(60000);
        druidDataSource.setMinEvictableIdleTimeMillis(300000);
        druidDataSource.setTestWhileIdle(true);
        druidDataSource.setTestOnBorrow(false);
        druidDataSource.setTestOnReturn(false);
        druidDataSource.setPoolPreparedStatements(true);
        druidDataSource.setMaxOpenPreparedStatements(20);
        druidDataSource.setValidationQuery("SELECT 'x'");
        return druidDataSource;
    }

    @Bean public SqlSessionFactory sqlSessionFactory(@Qualifier("druidDataSource") DataSource dataSource) throws Exception {
        SqlSessionFactoryBean sqlSessionFactoryBean = new SqlSessionFactoryBean();
        sqlSessionFactoryBean.setDataSource(dataSource);
        //mybatis分页
//        PageHelper pageHelper = new PageHelper();
        Properties props = new Properties();
        props.setProperty("offsetAsPageNum", "false");
        props.setProperty("rowBoundsWithCount", "true");
        props.setProperty("pageSizeZero", "false");
        props.setProperty("reasonable", "false");
        props.setProperty("params", "pageNum=pageNum;pageSize=pageSize;count=countSql;reasonable=reasonable;pageSizeZero=pageSizeZero");
        props.setProperty("supportMethodsArguments","false");
        props.setProperty("autoRuntimeDialect","true");
//        pageHelper.setProperties(props); //添加插件
        PageInterceptor pageInterceptor = new PageInterceptor();
        pageInterceptor.setProperties(props);
        sqlSessionFactoryBean.setPlugins(new Interceptor[]{pageInterceptor});
        PathMatchingResourcePatternResolver resolver = new PathMatchingResourcePatternResolver();
        sqlSessionFactoryBean.setMapperLocations(resolver.getResources("classpath*:com/zhonglianxs/erp/cpw/mapping/*.xml"));
        return sqlSessionFactoryBean.getObject();
    }
    @Bean public PlatformTransactionManager transactionManager() throws SQLException {
        return new DataSourceTransactionManager(dataSource());
    }

}
