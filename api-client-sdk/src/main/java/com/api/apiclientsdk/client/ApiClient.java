package com.api.apiclientsdk.client;


import cn.hutool.core.util.RandomUtil;
import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import cn.hutool.http.HttpUtil;
import cn.hutool.json.JSONUtil;
import com.api.apiclientsdk.model.User;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

import static com.api.apiclientsdk.utils.SignUtil.genSign;


/**
 * @author 若倾
 * @version 1.0
 * @description TODO
 * @date 2023/3/13 16:41
 */
public class ApiClient {

    private String accessKey;
    private String secretKey;

    public ApiClient(String accessKey, String secretKey) {
        this.accessKey = accessKey;
        this.secretKey = secretKey;
    }

    private static final String GATEWAY_HOST="http://localhost:8090";

    public String getNameByGet(String name){
        //可以单独传入http参数，这样参数会自动做URL编码，拼接在URL中
        HashMap<String, Object> paramMap = new HashMap<>();
        paramMap.put("name", name);
        String result= HttpUtil.get(GATEWAY_HOST+"/api/name/", paramMap);
        System.out.println(result);
        return result;
    }


    public String getNameByPost(String name){
        //可以单独传入http参数，这样参数会自动做URL编码，拼接在URL中
        HashMap<String, Object> paramMap = new HashMap<>();
        paramMap.put("name", name);
        String result= HttpUtil.post(GATEWAY_HOST+"/api/name/", paramMap);
        System.out.println(result);
        return result;
    }

    public String createPic(String code){
        //可以单独传入http参数，这样参数会自动做URL编码，拼接在URL中
        HashMap<String, Object> paramMap = new HashMap<>();
        paramMap.put("code", code);
        String result= HttpUtil.post(GATEWAY_HOST+"/api/name/pic/", paramMap);
        System.out.println(result);
        return result;
    }

    
    
    private Map<String,String> getHeaders(String body) throws UnsupportedEncodingException {
        HashMap<String, String> headers = new HashMap<>();
        headers.put("accessKey",accessKey);
//        headers.put("secretKey",secretKey);
        headers.put("nonce", RandomUtil.randomNumbers(4));
        headers.put("body", URLEncoder.encode(body,"utf-8"));
        headers.put("timestamp",String.valueOf(System.currentTimeMillis()/1000));
        headers.put("sign",genSign(body,secretKey));
        return headers;
    }


    public String getUsernameByPost(User user) throws UnsupportedEncodingException {
        String json = JSONUtil.toJsonStr(user);
        HttpResponse response = HttpRequest.post(GATEWAY_HOST+"/api/name/user")
                .addHeaders(getHeaders(json))
                .contentType("application/json")
                .body(json)
                .execute();
        int status = response.getStatus();
        System.out.println(status);
        System.out.println(response.body());
        return response.body();
    }
}
