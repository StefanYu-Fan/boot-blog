package com.stefan.blog.config;

import com.stefan.blog.rabbitmq.SmssUtils;
import org.springframework.amqp.core.Queue;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

//@Configuration
public class RabbitMqConfig {
    @Bean
    public Queue smm(){
        return new Queue("sms");
    }
}
