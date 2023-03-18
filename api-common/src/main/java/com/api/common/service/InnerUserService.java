package com.api.common.service;


import com.api.common.model.entity.User;


/**
 * 用户服务
 *
 * @author yupi
 */
public interface InnerUserService {

    /**
     * @description 数据库中查是否已经分配给用户密钥(accessKey)
     * @param accessKey
     * @return model.entity.User
     * @author 若倾
     * @date 2023/3/17 19:40
     */
    User getInvokeUser(String accessKey);
}
