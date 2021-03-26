package com.stefan.blog.filter;

import com.alibaba.fastjson.JSON;
import com.fasterxml.jackson.databind.ser.Serializers;
import com.stefan.blog.controller.BaseController;
import com.stefan.blog.mapper.UserContentMapper;
import com.stefan.blog.model.User;
import com.stefan.blog.model.UserContent;
import com.stefan.blog.utils.PageHelper;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;
import org.springframework.web.context.support.WebApplicationContextUtils;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import java.io.IOException;
import java.util.List;

/**
 * 首页过滤器
 */
@WebFilter(urlPatterns = "/index")
public class IndexFilter extends BaseController implements Filter {


    @Override
    public void init(FilterConfig filterConfig) throws ServletException {

    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        User user = (User) getSession().getAttribute("user");
        servletRequest.setAttribute("user",user);
        System.out.println("===========自定义过滤器==========");
        ServletContext context = servletRequest.getServletContext();
        ApplicationContext ctx = WebApplicationContextUtils.getWebApplicationContext(context);
        UserContentMapper userContentMapper = ctx.getBean(UserContentMapper.class);
        PageHelper.startPage(null, null);//开始分页
        List<UserContent> list =  userContentMapper.findByJoin( null );
        PageHelper.Page endPage = PageHelper.endPage();//分页结束
        servletRequest.setAttribute("page", endPage );
        servletRequest.setAttribute("list", JSON.toJSONString( endPage.getResult() )  );
        filterChain.doFilter(servletRequest, servletResponse);
    }

    @Override
    public void destroy() {

    }
}
