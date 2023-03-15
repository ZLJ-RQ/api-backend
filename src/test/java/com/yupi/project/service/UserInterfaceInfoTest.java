package com.yupi.project.service;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;

/**
 * @author 若倾
 * @version 1.0
 * @description TODO
 * @date 2023/3/15 16:45
 */
@SpringBootTest
public class UserInterfaceInfoTest {

    @Resource
    UserInterfaceInfoService userInterfaceInfoService;

    @Test
    public void name() {
        userInterfaceInfoService.invokeCount(1L,1L);
    }
}
