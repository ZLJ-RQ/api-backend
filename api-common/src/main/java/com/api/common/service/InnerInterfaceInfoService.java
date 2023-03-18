package com.api.common.service;


import com.api.common.model.entity.InterfaceInfo;


/**
* @author 若倾
* @description 针对表【interface_info(接口信息表)】的数据库操作Service
* @createDate 2023-03-12 21:31:10
*/
public interface InnerInterfaceInfoService {
     /**
      * @description 从数据库中查接口是否存在(请求路径,请求方法)
      * @param path
      * @param method
      * @return model.entity.InterfaceInfo
      * @author 若倾
      * @date 2023/3/17 19:41
      */
     InterfaceInfo getInterfaceInfo(String path, String method);

}
