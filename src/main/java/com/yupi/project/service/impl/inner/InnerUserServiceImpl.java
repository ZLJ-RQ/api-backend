package com.yupi.project.service.impl.inner;

import com.api.common.model.entity.User;
import com.api.common.service.InnerUserService;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.yupi.project.mapper.UserMapper;
import com.yupi.project.service.UserService;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * @author 若倾
 * @version 1.0
 * @description TODO
 * @date 2023/3/18 11:04
 */
@DubboService
public class InnerUserServiceImpl implements InnerUserService {

    @Autowired
    private UserMapper userMapper;

    @Override
    public User getInvokeUser(String accessKey) {
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("accessKey",accessKey);
        return userMapper.selectOne(queryWrapper);
    }
}
