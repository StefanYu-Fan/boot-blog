package com.stefan.blog.controller;

import com.google.code.kaptcha.Constants;
import com.stefan.blog.mail.SendEmail;
import com.stefan.blog.model.RoleUser;
import com.stefan.blog.model.User;
import com.stefan.blog.service.UserService;
import com.stefan.blog.utils.ConstansUtil;
import com.stefan.blog.utils.MD5Util;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
public class RegisterController extends BaseController{
    // 打印日志
    private final Logger log = LoggerFactory.getLogger(RegisterController.class);

    // redis保存激活码，定时失效
    @Autowired
    private  RedisTemplate redisTemplate;

    @Autowired
    private UserService userService;

    /**
     * 验证手机号是否正确
     * @param model
     * @param phone
     * @return
     */
    @RequestMapping("/checkPhone")
    @ResponseBody
    public Map<String, Object> checkPhone(Model model, @RequestParam(value = "phone", required = false) String phone) {
        log.info("注册-判断手机号" + phone + "是否可用");
        Map map = new HashMap<String, Object>();
        User user = userService.findByPhone(phone);
        if (user == null) {
            //未注册
            map.put("message", "success");
        } else {
            //已注册
            map.put("message", "fail");
        }

        return map;
    }

    /**
     * 判断邮箱是否被注册
     * @param model
     * @param email
     * @return
     */
    @RequestMapping("/checkEmail")
    @ResponseBody
    public Map<String, Object> checkEmail(Model model, @RequestParam(value = "email", required = false) String email) {
        log.info("注册-判断邮箱" + email + "是否可用");
        Map map = new HashMap<String, Object>();
        User user = userService.findByEmail(email);
        if (user == null) {
            //未注册
            map.put("message", "success");
        } else {
            //已注册
            map.put("message", "fail");
        }

        return map;
    }


    /**
     * 验证谷歌验证码是否正确
     * @param model
     * @param code
     * @return
     */
    @RequestMapping("/regist/checkCode")
    @ResponseBody
    public Map<String, Object> checkCode(Model model, @RequestParam(value = "code", required = false) String code) {
        log.info("注册-判断验证码" + code + "是否可用");
        Map map = new HashMap<String, Object>();
        String vcode = (String)getRequest().getSession().getAttribute(Constants.KAPTCHA_SESSION_KEY);

        if (code.equals(vcode)) {
            //未注册
            map.put("message", "success");
        } else {
            //已注册
            map.put("message", "fail");
        }

        return map;
    }

    /**
     * 用户注册
     *
     * @param model
     * @param email
     * @param password
     * @param phone
     * @param nickname
     * @param code
     * @return
     */
    @RequestMapping("/doregister")
    public String doRegister(Model model, @RequestParam(value = "email", required = false) String email,
                             @RequestParam(value = "password", required = false) String password,
                             @RequestParam(value = "phone", required = false) String phone,
                             @RequestParam(value = "nickName", required = false) String nickname,
                             @RequestParam(value = "code", required = false) String code) {

        log.info("注册...");
        if (StringUtils.isBlank(code)) {
            model.addAttribute("error", "非法注册，请重新注册！");
            return "/regist";
        }

        int b = checkValidateCode(code);
        if (b == -1) {
            model.addAttribute("error", "验证码超时，请重新注册！");
            return "/regist";
        } else if (b == 0) {
            model.addAttribute("error", "验证码不正确,请重新输入!");
            return "/regist";
        }


        User user = userService.findByEmail(email);
        if (user != null) {
            model.addAttribute("error", "该用户已经被注册！");
            return "/regist";
        } else {
            user = new User();
            user.setNickName(nickname);

            user.setPassword(MD5Util.encodeToHex("salt"+password));
            user.setPhone(phone);
            user.setEmail(email);
            user.setState("0");
            user.setEnable("0");
            user.setImgUrl("/images/icon_m.jpg");
            //邮件激活码
            String validateCode = MD5Util.encodeToHex("salt"+email + password);
            redisTemplate.opsForValue().set(email, validateCode, 24, TimeUnit.HOURS);// 24小时 有效激活 redis保存激活码

            userService.regist(user);

//            RoleUser roleUser = new RoleUser();
//            roleUser.setuId(user.getId());
//            roleUser.setrId(ConstansUtil.ROLE_USER);
//            roleUserService.add(roleUser);

            log.info("注册成功");
            SendEmail.sendEmailMessage(email, validateCode);
            String message = email + "," + validateCode;
            model.addAttribute("message", message);
            return "/active-regist/registSuccess";

        }
    }

    /**
     * 匹配激活码的准确性
     * @param code
     * @return
     */
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
     * 发送邮件
     * @param model
     * @return
     */
    @RequestMapping("/sendEmail")
    @ResponseBody
    public  Map<String,Object> sendEmail(Model model) {
        Map map = new HashMap<String,Object>(  );
        ServletRequestAttributes attrs = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        String validateCode = attrs.getRequest().getParameter( "validateCode" );
        String email = attrs.getRequest().getParameter( "email" );
        SendEmail.sendEmailMessage(email,validateCode);
        map.put( "success","success" );
        return map;
    }

    /**
     * 激活验证
     * @param model
     * @return
     */
    @RequestMapping("/activecode")
    public String active(Model model) {
        log.info( "==============激活验证==================" );
        //判断   激活有无过期 是否正确
        //validateCode=
        ServletRequestAttributes attrs = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        String validateCode = attrs.getRequest().getParameter( "validateCode" );
        String email = attrs.getRequest().getParameter( "email" );
        String code = (String) redisTemplate.opsForValue().get( email );
        log.info( "验证邮箱为："+email+",邮箱激活码为："+code+",用户链接的激活码为："+validateCode );
        //判断是否已激活

        User userTrue = userService.findByEmail( email );
        if(userTrue!=null && "1".equals( userTrue.getState() )){
            //已激活
            model.addAttribute( "success","您已激活,请直接登录！" );
            return "../login";
        }

        if(code==null){
            //激活码过期
            model.addAttribute( "fail","您的激活码已过期,请重新注册！" );
//            roleUserService.deleteByUid( userTrue.getId() );
            userService.deleteByEmail( email );
            return "/active-regist/activeFail";
        }

        if(StringUtils.isNotBlank( validateCode ) && validateCode.equals( code )){
            //激活码正确
            userTrue.setEnable( "1" );
            userTrue.setState( "1" );
            userService.update( userTrue );
            model.addAttribute( "email",email );
            return "/active-regist/activeSuccess";
        }else {
            //激活码错误
            model.addAttribute( "fail","您的激活码错误,请重新激活！" );
            return "/active-regist/activeFail";
        }

    }
}
