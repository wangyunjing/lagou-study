package com.lagou.edu.interceptor;

import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Objects;

/**
 * @author yunjing.wang
 * @date 2020/8/30
 */
public class LoginInterceptor implements HandlerInterceptor {
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        Cookie[] cookies = request.getCookies();
        if (cookies == null) {
            request.getRequestDispatcher("/index.jsp").forward(request, response);
            return false;
        }
        for (Cookie cookie : cookies) {
            if (Objects.equals("username", cookie.getName()) &&
                    Objects.equals("admin", cookie.getValue())) {
                return true;
            }
        }
        request.getRequestDispatcher("/index.jsp").forward(request, response);
        return false;
    }
}
