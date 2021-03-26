package com.stefan.blog.config;

import com.stefan.blog.utils.PageHelper;
import org.apache.ibatis.session.SqlSessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.context.annotation.Configuration;

import javax.annotation.PostConstruct;
import java.util.List;

@Configuration
public class PageHelperConfig {
    @Autowired
    private List<SqlSessionFactory> sqlSessionFactoryList;

    @PostConstruct
    public void addMySqlInterceptor() {
        PageHelper interceptor = new PageHelper();
        for (SqlSessionFactory sqlSessionFactory : sqlSessionFactoryList) {

            // 添加自定义属性
//            Properties properties = new Properties();
//            properties.setProperty("prop1", "value1");
//            interceptor.setProperties(properties);
            sqlSessionFactory.getConfiguration().addInterceptor(interceptor);
        }
    }
}
