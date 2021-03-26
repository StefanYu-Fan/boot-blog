package com.stefan.blog.service.impl;

import com.stefan.blog.mapper.UserInfoMapper;
import com.stefan.blog.model.UserInfo;
import com.stefan.blog.service.UserInfoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;



@Service
public class UserInfoServiceImpl implements UserInfoService {
    @Autowired
    private UserInfoMapper userInfoMapper;


    public UserInfo findByUid(Long id) {
        UserInfo userInfo = new UserInfo();
        userInfo.setuId(id);
        return userInfoMapper.selectOne(userInfo);
    }

    public void update(UserInfo userInfo) {
        userInfoMapper.updateByPrimaryKey(userInfo);
    }

    public void add(UserInfo userInfo) {
        userInfoMapper.insert(userInfo);
    }
}
