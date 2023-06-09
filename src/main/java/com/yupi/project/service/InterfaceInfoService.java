package com.yupi.project.service;


import com.api.common.model.entity.InterfaceInfo;
import com.baomidou.mybatisplus.extension.service.IService;

/**
* @author 若倾
* @description 针对表【interface_info(接口信息表)】的数据库操作Service
* @createDate 2023-03-12 21:31:10
*/
public interface InterfaceInfoService extends IService<InterfaceInfo> {
     void validInterfaceInfo(InterfaceInfo interfaceInfo, boolean add);
}
