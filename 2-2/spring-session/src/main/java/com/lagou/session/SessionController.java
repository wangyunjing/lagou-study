package com.lagou.session;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpSession;

/**
 * @author yunjing.wang
 * @date 2020/10/19
 */
@Controller
public class SessionController {

    @Value("${server.port}")
    int port;

    @GetMapping("/login")
    public Object login(String username, String password,
                        HttpSession httpSession) {
        if ("admin".equals(username) && "admin".equals(password)) {
            httpSession.setAttribute("username", "admin");
            return "redirect:/session";
        } else {
            return "login failure";
        }
    }

    @ResponseBody
    @GetMapping("/session")
    public Object session(HttpSession httpSession) {
        return "当前端口："+port + "<br>SessionId：" + httpSession.getId();
    }
}
