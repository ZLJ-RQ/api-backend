package com.rq.apiinterface.controller;


import com.api.apiclientsdk.model.User;
import com.google.code.kaptcha.impl.DefaultKaptcha;
import com.rq.apiinterface.utils.EncryptUtil;
import org.springframework.web.bind.annotation.*;
import sun.misc.BASE64Encoder;


import javax.annotation.Resource;
import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletRequest;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;

/**
 * @author 若倾
 * @version 1.0
 * @description TODO
 * @date 2023/3/13 16:21
 */
@RestController
@RequestMapping("/name")
public class NameController {


    @Resource
    private DefaultKaptcha kaptcha;

    @GetMapping("/get")
    public String getNameByGet(String name){
        return "GET 你的名字是"+name;
    }

    @PostMapping("/post")
    public String getNameByPost(@RequestParam String name){
        return "POST 你的名字是"+name;
    }

    @PostMapping("/user")
    public String getUsernameByPost(@RequestBody User user, HttpServletRequest request)  {
        return "POST 你的名字是"+user.getUsername();
        
    }
    @PostMapping("/pic")
    private String createPic(@RequestParam String code) {
        // 生成图片验证码
        ByteArrayOutputStream outputStream = null;
        BufferedImage image = kaptcha.createImage(code);
        outputStream = new ByteArrayOutputStream();
        String imgBase64Encoder = null;
        try {
            // 对字节数组Base64编码
            BASE64Encoder base64Encoder = new BASE64Encoder();
            ImageIO.write(image, "png", outputStream);
            imgBase64Encoder = "data:image/png;base64," + EncryptUtil.encodeBase64(outputStream.toByteArray());
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                outputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return imgBase64Encoder;
    }
}
