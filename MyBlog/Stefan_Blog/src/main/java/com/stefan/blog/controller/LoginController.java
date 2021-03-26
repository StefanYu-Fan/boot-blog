package com.stefan.blog.controller;

import com.fasterxml.jackson.databind.ser.Serializers;
import com.google.code.kaptcha.Constants;
import com.stefan.blog.model.User;
import com.stefan.blog.service.UserService;
import com.stefan.blog.utils.ConstansUtil;
import com.stefan.blog.utils.MD5Util;
import com.stefan.blog.utils.RandStringUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@Controller
public class LoginController extends BaseController {

    //记录日志信息
    private final Logger log = LoggerFactory.getLogger(LoginController.class);

    @Autowired
    private RedisTemplate redisTemplate;

    @Autowired
    private RabbitTemplate rabbitTemplate;

    @Autowired
    private UserService userService;

    /**
     * 验证码是否正确
     * @param model
     * @param code
     * @return
     */
    @RequestMapping("/login/checkCode")
    @ResponseBody
    public Map<String, Object> checkCode(Model model, @RequestParam(value = "code", required = false) String code) {
        log.info("注册-判断验证码" + code + "是否可用");
        Map map = new HashMap<String, Object>();
        // 获取当前线程
        ServletRequestAttributes attrs = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        String vcode = (String) attrs.getRequest().getSession().getAttribute(Constants.KAPTCHA_SESSION_KEY);

        if (code.equals(vcode)) {
            //未注册
            map.put("message", "success");
        } else {
            //已注册
            map.put("message", "fail");
        }

        return map;
    }

    @RequestMapping("/doLogin")
    public String doLogin(Model model, @RequestParam(value = "username",required = false) String email,
                          @RequestParam(value = "password",required = false) String password,
                          @RequestParam(value = "code",required = false) String code,
                          @RequestParam(value = "state",required = false) String state,
                          @RequestParam(value = "pageNum",required = false) Integer pageNum ,
                          @RequestParam(value = "pageSize",required = false) Integer pageSize) {
        if (StringUtils.isBlank(code)) {
            model.addAttribute("error", "fail");
            return "login";
        }
        int b = checkValidateCode(code);
        if (b == -1) {
            model.addAttribute("error", "fail");
            return "login";
        } else if (b == 0) {
            model.addAttribute("error", "fail");
            return "login";
        }
        log.info("--------" + password + "--------");
        password = MD5Util.encodeToHex(ConstansUtil.SALT+password);
        User user =  userService.login(email,password);
        if (user!=null){
            if("0".equals(user.getState())){
                //未激活
                model.addAttribute("email",email);
                model.addAttribute("error","active");
                return "login";
            }
            log.info("用户登录登录成功");
            getSession().setAttribute( "user",user );
            model.addAttribute("user",user);
            return "redirect:/list";
        }else{
            log.info("用户登录登录失败");
            model.addAttribute("email",email);
            model.addAttribute( "error","fail" );
            return "login";
        }
    }

    // 匹对验证码的正确性
    private int checkValidateCode(String code) {
        Object vercode = getRequest().getSession().getAttribute(Constants.KAPTCHA_SESSION_KEY);
        if (null == vercode) {
            return -1;
        }
        if (!code.equalsIgnoreCase(vercode.toString())) {
            return 0;
        }
        return 1;
    }

    /**
     * 手机验证码发送
     * @param telephone
     * @return
     */
    @RequestMapping("/sendSms")
    @ResponseBody
    public Map<String,Object> index(@RequestParam(value = "telephone",required = false) final String telephone ) {
        Map map = new HashMap<String,Object>(  );
        try { //  发送验证码操作
            final String code = RandStringUtils.getCode();
            redisTemplate.opsForValue().set(telephone, code, 120, TimeUnit.SECONDS);// 120秒 有效 redis保存验证码
            log.info("--------短信验证码为："+code);
            Map<String ,String > mapMessage = new HashMap<>();
            mapMessage.put("mobile",telephone);
            mapMessage.put("checkcode",code);
            // 调用RabbitMQ rabbitTemplate，发送一条消息给MQ
            rabbitTemplate.convertAndSend("sms",mapMessage);
        } catch (Exception e) {
            map.put( "msg",false );
        }
        map.put( "msg",true );
        return map;

    }

    /**
     * 退出登录
     * @param model
     * @return
     */
    @RequestMapping("/logout")
    public String exit(Model model) {
        log.info( "退出登录" );
        getSession().removeAttribute( "user" );
        getSession().invalidate();
        return "/login";
    }

}
