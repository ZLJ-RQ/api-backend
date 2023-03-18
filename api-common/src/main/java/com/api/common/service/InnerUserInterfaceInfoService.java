package com.api.common.service;




/**
* @author 若倾
* @description 针对表【user_interface_info(接口信息表)】的数据库操作Service
* @createDate 2023-03-15 15:57:56
*/
public interface InnerUserInterfaceInfoService  {


    /**
     * @description 调用接口统计
     * @param interfaceInfoId
     * @param userId
     * @return boolean
     * @author 若倾
     * @date 2023/3/17 19:41
    */
    boolean invokeCount(long interfaceInfoId,long userId);
    /**
     * @description 查询剩余调用次数
     * @param interfaceInfoId
     * @param userId
     * @return java.lang.Integer
     * @author 若倾
     * @date 2023/3/18 15:41
    */
    Integer queryLeftNum(long interfaceInfoId, long userId) ;
}
