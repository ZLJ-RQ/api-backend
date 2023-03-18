package com.yupi.project.mapper;


import com.api.common.model.entity.UserInterfaceInfo;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;

import java.util.List;

/**
* @author 若倾
* @description 针对表【user_interface_info(接口信息表)】的数据库操作Mapper
* @createDate 2023-03-15 15:57:56
* @Entity com.yupi.project.model.entity.UserInterfaceInfo
*/
public interface UserInterfaceInfoMapper extends BaseMapper<UserInterfaceInfo> {

    List<UserInterfaceInfo> listTopInvokeInterfaceInfo(int limit);
}




