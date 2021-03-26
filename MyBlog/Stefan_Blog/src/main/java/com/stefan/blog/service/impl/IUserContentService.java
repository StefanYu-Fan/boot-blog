package com.stefan.blog.service.impl;

import com.stefan.blog.mapper.CommentMapper;
import com.stefan.blog.mapper.UserContentMapper;
import com.stefan.blog.model.Comment;
import com.stefan.blog.model.User;
import com.stefan.blog.model.UserContent;
import com.stefan.blog.service.UserContentService;
import com.stefan.blog.utils.PageHelper;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tk.mybatis.mapper.entity.Example;
import com.stefan.blog.utils.PageHelper.Page;

import java.util.List;
@Service
public class IUserContentService implements UserContentService {
    @Autowired
    private UserContentMapper userContentMapper;
    @Autowired
    private CommentMapper commentMapper;
    public int addContent(UserContent content) {
        return userContentMapper.inserContent(content);
    }

    public List<UserContent> findByUserId(Long uid) {
        UserContent userContent = new UserContent();
        userContent.setuId(uid);
        List<UserContent> list = userContentMapper.select( userContent );
        return list;
    }

    public List<UserContent> findAll() {
        return userContentMapper.select( null );
    }

    public Page<UserContent> findAll(UserContent content, Integer pageNum, Integer pageSize) {
        //分页查询
        System.out.println("第"+pageNum+"页");
        System.out.println("每页显示："+pageSize+"条");
        PageHelper.startPage(pageNum, pageSize);//开始分页
        List<UserContent> list = userContentMapper.findByJoin(content);
        Page endPage = PageHelper.endPage();//分页结束
        List<UserContent> result = endPage.getResult();
        return endPage;
    }

    public Page<UserContent> findAll(UserContent content, Comment comment, Integer pageNum, Integer pageSize) {
        //分页查询
        System.out.println("第"+pageNum+"页");
        System.out.println("每页显示："+pageSize+"条");
        PageHelper.startPage(pageNum, pageSize);//开始分页
        List<UserContent> list =  userContentMapper.select( content );

        List<Comment> comments = commentMapper.select( comment );

        Page endPage = PageHelper.endPage();//分页结束
        List<UserContent> result = endPage.getResult();
        return endPage;
    }

    public Page<UserContent> findAllByUpvote(UserContent content, Integer pageNum, Integer pageSize) {
        Example e = new Example(UserContent.class);
        e.setOrderByClause("upvote DESC");
        PageHelper.startPage(pageNum, pageSize);//开始分页
        List<UserContent> list = userContentMapper.selectByExample(e);
        Page endPage = PageHelper.endPage();//分页结束
        return endPage;
    }

    public UserContent findById(long id) {
        UserContent userContent = new UserContent();
        userContent.setId( id );
        List<UserContent> list = userContentMapper.findByJoin(userContent);
        if(list!=null && list.size()>0){
            return list.get(0);
        }else {
            return null;
        }
    }


    public void updateById(UserContent content) {
        userContentMapper.updateByPrimaryKeySelective( content );
    }

    @Override
    public List<UserContent> findCategoryByUid(Long uid) {
        return userContentMapper.findCategoryByUid(uid);
    }

    @Override
    public Page<UserContent> findByCategory(String category,Long uid,Integer pageNum, Integer pageSize) {
        UserContent userContent = new UserContent();
        if(StringUtils.isNotBlank(category) && !"null".equals(category)){
            userContent.setCategory(category);
        }
        userContent.setuId(uid);
        userContent.setPersonal("0");
        PageHelper.startPage(pageNum, pageSize);//开始分页
        userContentMapper.select(userContent);
        Page endPage = PageHelper.endPage();//分页结束
        return endPage;
    }

    @Override
    public Page<UserContent> findPersonal(Long uid, Integer pageNum, Integer pageSize) {
        UserContent userContent = new UserContent();
        userContent.setuId(uid);
        userContent.setPersonal("1");
        PageHelper.startPage(pageNum, pageSize);//开始分页
        userContentMapper.select(userContent);
        Page endPage = PageHelper.endPage();//分页结束
        return endPage;
    }

    @Override
    public void deleteById(Long cid) {
        userContentMapper.deleteByPrimaryKey(cid);
    }

    @Override
    public Page<UserContent> findAll(Integer pageNum, Integer pageSize) {
        //分页查询
        PageHelper.startPage(pageNum, pageSize);//开始分页
        List<UserContent> list = userContentMapper.findByJoin(null);
        Page endPage = PageHelper.endPage();//分页结束
        return endPage;
    }

    @Override
    public void updateByUId(User user) {
        userContentMapper.updateByUId(user);
    }


}
