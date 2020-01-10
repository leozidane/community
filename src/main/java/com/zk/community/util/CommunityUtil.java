package com.zk.community.util;

import org.apache.commons.lang3.StringUtils;
import org.springframework.util.DigestUtils;

import java.util.UUID;

/**
 * 常用方法工具类
 */
public class CommunityUtil {

    //生成随机字符串
    public static String generateUUID() {
        return UUID.randomUUID().toString().replaceAll("-","");
    }

    //生成md5:不可解码
    //生成MD5时需要给原始密码加上salt然后再进行MD5加密，因为过于简单的密码其md5加密后随机字符串也容易获得
    //比如：
    //hello ->abc123dfg345
    //hello + 3a3b6 -> abc123dfg345abc
    public static String md5(String key) {
        if (StringUtils.isBlank(key)) {
            return null;
        }
        return DigestUtils.md5DigestAsHex(key.getBytes());
    }
}
