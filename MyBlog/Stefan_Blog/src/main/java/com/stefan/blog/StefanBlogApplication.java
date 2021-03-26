package com.stefan.blog;

import com.mysql.cj.x.protobuf.MysqlxDatatypes;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.ServletComponentScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import tk.mybatis.spring.annotation.MapperScan;

@SpringBootApplication
@ServletComponentScan
@MapperScan("com.stefan.blog.mapper")
@EnableTransactionManagement
public class StefanBlogApplication {

	public static void main(String[] args) {
		SpringApplication.run(StefanBlogApplication.class, args);
	}

}
