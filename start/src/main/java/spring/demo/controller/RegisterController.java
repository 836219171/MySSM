package spring.demo.controller;

import spring.annotation.MyAutowired;
import spring.annotation.MyController;
import spring.annotation.MyRequestMapping;
import spring.demo.service.RegisterService;
import spring.demo.service.UserService;

/**
 * Created by Xiao Liang on 2018/6/27.
 */
@MyController
public class RegisterController {

    @MyAutowired
    private UserService userService;

    @MyAutowired
    private RegisterService registerService;

    @MyRequestMapping("/register")
    public void regeister(){
        userService.queryUser("","");
        registerService.register();
    }

}
