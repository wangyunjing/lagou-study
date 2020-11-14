package com.lagou.service;

public class UserServiceImpl implements IUserService {

    @Override
    public String sayHello(String msg) {
        System.out.println(msg);
        return "success";
    }
}
