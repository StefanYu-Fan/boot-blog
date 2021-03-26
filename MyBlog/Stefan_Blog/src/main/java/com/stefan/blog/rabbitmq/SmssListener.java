package com.stefan.blog.rabbitmq;

import com.aliyuncs.exceptions.ClientException;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Map;

//@Component
//@RabbitListener(queues = "sms")
public class SmssListener {

//    @Autowired
    private SmssUtils smssUtill;
//
//    @Value("${aliyun.sms.template_code}")
//    private String template_code;
//
//    @Value("${aliyun.sms.sign_name}")
//    private String sign_name;

//    @RabbitHandler
    public void executeSmss(Map<String,String> map){
        String mobile = map.get("mobile");
        String checkcode = map.get("checkcode");
        System.out.println(mobile);
        System.out.println(checkcode);

        try {
            smssUtill.sendSms(mobile, "{\"checkcode\":\""+checkcode+"\"}");
        } catch (ClientException e) {
            e.printStackTrace();
        }
    }

}