package com.stefan.blog.service.impl;

import com.stefan.blog.mapper.UserMapper;
import com.stefan.blog.model.User;
import com.stefan.blog.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class IUserService implements UserService {

    @Autowired
    private UserMapper userMapper;

    /**
     * 注册用户
     * @param user
     * @return
     */
    @Override
    public int regist(User user) {
        return userMapper.insert(user);
    }

    /**
     * 登录用户验证
     * @param name
     * @param password
     * @return
     */
    @Override
    public User login(String name, String password) {
        User user = new User();
        user.setEmail(name);
        user.setPassword(password);
        return userMapper.selectOne(user);
        //return userMapper.findUserByNameAndPwd( name,password );
    }

    /**
     * 通过email查找用户
     * @param email
     * @return
     */
    @Override
    public User findByEmail(String email) {
        User user = new User();
        user.setEmail(email);
        return userMapper.selectOne(user);
        // return userMapper.findByEmail(email);
    }

    /**
     * 通过手机号查找用户
     * @param phone
     * @return
     */
    @Override
    public User findByPhone(String phone) {
        User user = new User();
        user.setPhone(phone);
        return userMapper.selectOne(user);
    }

    /**
     * 通过id查找用户
     * @param id
     * @return
     */
    @Override
    public User findById(Long id) {
        User user = new User();
        user.setId(id);
        return userMapper.selectOne(user);
    }

    /**
     * 根据邮箱是否被激活查找
     * @param email
     * @return
     */
    public User findByEmailActive(String email) {
        User user = new User();
        user.setEmail(email);
        return userMapper.selectOne(user);
        // return userMapper.findByEmail(email);
    }

    public User findById(String id) {
        User user = new User();
        Long uid = Long.parseLong(id);
        user.setId(uid);
        return userMapper.selectOne(user);
    }

    public User findById(long id) {
        User user = new User();
        user.setId(id);
        return userMapper.selectOne(user);
    }

    public void deleteByEmail(String email) {
        User user = new User();
        user.setEmail(email);
        userMapper.delete(user);
    }

    public void deleteByEmailAndFalse(String email) {
        User user = new User();
        user.setEmail(email);
        userMapper.delete(user);
    }

    public void update(User user) {
        userMapper.updateByPrimaryKeySelective(user);
    }
}
