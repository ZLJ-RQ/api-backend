package com.yupi.project.service.impl.inner;

import com.api.common.model.entity.InterfaceInfo;
import com.api.common.service.InnerInterfaceInfoService;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.yupi.project.mapper.InterfaceInfoMapper;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * @author 若倾
 * @version 1.0
 * @description TODO
 * @date 2023/3/18 11:02
 */
@DubboService
public class InnerInterfaceInfoServiceImpl implements InnerInterfaceInfoService {

    @Autowired
    private InterfaceInfoMapper interfaceInfoMapper;
    /**
     * @description 从数据库中查接口是否存在(请求路径,请求方法)
     * @param path
     * @param method
     * @return model.entity.InterfaceInfo
     * @author 若倾
     * @date 2023/3/17 19:41
     */
    @Override
    public InterfaceInfo getInterfaceInfo(String path, String method) {
        QueryWrapper<InterfaceInfo> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("url",path);
        queryWrapper.eq("method",method);
        return interfaceInfoMapper.selectOne(queryWrapper);
    }
}
