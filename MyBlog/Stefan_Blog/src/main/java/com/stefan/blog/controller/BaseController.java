package com.stefan.blog.controller;

import com.stefan.blog.model.User;
import com.stefan.blog.model.UserContent;
import com.stefan.blog.service.UserContentService;
import com.stefan.blog.service.UserService;
import com.stefan.blog.utils.PageHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import com.stefan.blog.utils.PageHelper.Page;
@Component
public class BaseController {

    @Autowired
    private UserContentService userContentService;
    @Autowired
    private UserService userService;


    private static final String[] HEADERS_TO_TRY = {
            "X-Forwarded-For",
            "Proxy-Client-IP",
            "WL-Proxy-Client-IP",
            "HTTP_X_FORWARDED_FOR",
            "HTTP_X_FORWARDED",
            "HTTP_X_CLUSTER_CLIENT_IP",
            "HTTP_CLIENT_IP",
            "HTTP_FORWARDED_FOR",
            "HTTP_FORWARDED",
            "HTTP_VIA",
            "REMOTE_ADDR",
            "X-Real-IP"};

    public PageHelper.Page<UserContent> findAll(UserContent content, Integer pageNum, Integer pageSize){
        Page<UserContent> page = userContentService.findAll( content,pageNum ,pageSize);
        return page;
    }

    public Page<UserContent> findAll(Integer pageNum, Integer pageSize){
        Page<UserContent> page = userContentService.findAll(pageNum ,pageSize);
        return page;
    }

    public Page<UserContent> findAllByUpvote(UserContent content, Integer pageNum, Integer pageSize){
        Page<UserContent> page = userContentService.findAllByUpvote( content,pageNum ,pageSize);
        return page;
    }
    /**
     * 获取request
     * @return
     */
    public static HttpServletRequest getRequest() {
        ServletRequestAttributes attrs =(ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        return attrs.getRequest();
    }

    /**
     * 获取response
     * @return
     */
    public static HttpServletResponse getResponse() {
        HttpServletResponse response = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getResponse();
        return response;
    }

    /**
     * 获取session
     * @return
     */
    public static HttpSession getSession() {
        HttpSession session = null;
        try {
            session = getRequest().getSession();
        } catch (Exception e) {}
        return session;
    }

//    /**
//     * 获取当前用户
//     * @return
//     */
//    public User getCurrentUser(){
//        User user = null;
//        Authentication authentication = null;
//        SecurityContext context = SecurityContextHolder.getContext();
//        if(context!=null){
//            authentication = context.getAuthentication();
//        }
//        if(authentication!=null){
//            Object principal = authentication.getPrincipal();
//            if(principal instanceof OpenUser){
//                user = ((OpenUser) principal).getUser();
//            }else if(authentication.getPrincipal().toString().equals( "anonymousUser" )){
//                //如果是匿名用户
//                return null;
//            }else {
//                user = (User)principal;
//            }
//
//        }
//        return user;
//    }


    /***
     * 获取客户端ip地址(可以穿透代理)
     * @return
     */
    public static String getClientIpAddress() {
        HttpServletRequest request = getRequest();
        for (String header : HEADERS_TO_TRY) {
            String ip = request.getHeader(header);
            if (ip != null && ip.length() != 0 && !"unknown".equalsIgnoreCase(ip)) {
                return ip;
            }
        }
        return request.getRemoteAddr();
    }
}
