package com.api.apiclientsdk.utils;

import cn.hutool.crypto.digest.DigestAlgorithm;
import cn.hutool.crypto.digest.Digester;

/**
 * @author 若倾
 * @version 1.0
 * @description TODO
 * @date 2023/3/13 20:11
 */
public class SignUtil {

    public static String genSign(String body, String secretKey){
        Digester md5 = new Digester(DigestAlgorithm.SHA256);
        String content = body+"."+secretKey;
        return md5.digestHex(content);
    }
}
