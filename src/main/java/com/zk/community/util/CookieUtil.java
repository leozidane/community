package com.zk.community.util;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;

/**
 * Cookie工具类：通过给定的参数从cookie数组中得到对应的值
 */
public class CookieUtil {

    public static String getValue (HttpServletRequest request, String name) {
        if (request == null || name == null) {
            throw new IllegalArgumentException("给定参数为空");
        }
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if (cookie.getName().equals(name)) {
                    return cookie.getValue();
                }
            }
        }
        return null;
    }
}
