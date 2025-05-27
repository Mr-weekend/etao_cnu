package cn.etaocnu.cloud.springSecurity.utils;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.util.StringUtils;

public class TokenUtils {

    public static String getTokenFromRequest(HttpServletRequest request) {
        // 从请求头获取
        String token = request.getHeader("AdminToken");

        // 如果请求头中没有，则从请求参数中获取
        if (!StringUtils.hasText(token)) {
            token = request.getParameter("token");
        }

        return token;
    }
}
