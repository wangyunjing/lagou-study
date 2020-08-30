package com.lagou.demo.controller;

import com.lagou.demo.service.IDemoService;
import com.lagou.edu.mvcframework.annotations.LagouAutowired;
import com.lagou.edu.mvcframework.annotations.LagouController;
import com.lagou.edu.mvcframework.annotations.LagouRequestMapping;
import com.lagou.edu.mvcframework.annotations.Security;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@LagouController
@LagouRequestMapping("/demo")
@Security("zhangsan")
public class DemoController {

    @LagouAutowired
    private IDemoService demoService;

    @LagouRequestMapping("/query1")
    public String query1(HttpServletRequest request, HttpServletResponse response, String username) {
        return demoService.get(username);
    }

    @LagouRequestMapping("/query2")
    @Security("lisi")
    public String query2(HttpServletRequest request, HttpServletResponse response, String username) {
        return demoService.get(username);
    }

    @LagouRequestMapping("/query3")
    @Security(value = {"wangwu", "zhaoliu"})
    public String query3(HttpServletRequest request, HttpServletResponse response, String username) {
        return demoService.get(username);
    }
}
