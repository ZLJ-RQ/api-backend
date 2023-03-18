package com.api.common.model.vo;

import com.api.common.model.entity.InterfaceInfo;
import lombok.Data;

/**
 * @author 若倾
 * @version 1.0
 * @description TODO
 * @date 2023/3/18 13:04
 */
@Data
public class InterfaceInfoVO extends InterfaceInfo {

    private Integer totalNum;

    private static final long serialVersionUID = 1L;

}
