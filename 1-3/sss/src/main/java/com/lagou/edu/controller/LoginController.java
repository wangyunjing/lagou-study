package com.lagou.edu.controller;

import com.lagou.edu.dao.ResumeDao;
import com.lagou.edu.pojo.Resume;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;
import java.util.List;
import java.util.Objects;

/**
 * @author yunjing.wang
 * @date 2020/8/30
 */
@Controller
@RequestMapping("/login")
public class LoginController {

    @Autowired
    ResumeDao resumeDao;

    @RequestMapping
    public ModelAndView login(HttpServletResponse response, @RequestParam("username") String username,
                              @RequestParam("password") String password) {
        ModelAndView modelAndView = new ModelAndView();
        if (Objects.equals("admin", username) && Objects.equals("admin", password)) {
            Cookie cookie = new Cookie("username", username);
            response.addCookie(cookie);
            modelAndView.setViewName("list");
            List<Resume> all = resumeDao.findAll();
            modelAndView.addObject("resumeList", all);
            return modelAndView;
        }
        modelAndView.setViewName("forward:/index.jsp");
        return modelAndView;
    }
}
