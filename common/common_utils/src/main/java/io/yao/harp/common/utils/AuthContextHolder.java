package io.yao.harp.common.utils;

import io.yao.harp.common.helper.JwtHelper;

import javax.servlet.http.HttpServletRequest;

// get current user info
public class AuthContextHolder {

    // get user Id
    public static Long getUserId(HttpServletRequest request) {
        String token = request.getHeader("token");
        return JwtHelper.getUserId(token);
    }

    // get username
    public static String getUsername(HttpServletRequest request) {
        String token = request.getHeader("token");
        return JwtHelper.getUserName(token);
    }
}
