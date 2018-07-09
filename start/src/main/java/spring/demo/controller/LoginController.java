package spring.demo.controller;

import spring.annotation.MyAutowired;
import spring.annotation.MyController;
import spring.annotation.MyModelAttribute;
import spring.annotation.MyRequestMapping;
import spring.dataObject.User;
import spring.demo.service.UserService;
import spring.springmvc.MyModelAndView;
import spring.springmvc.MyModelMap;
import spring.xmlRules.RequestMethod;

/**
 * Created by Xiao Liang on 2018/6/27.
 */
@MyController
public class LoginController {

    @MyAutowired
    private UserService userService;


    //测试用的@MyRequstParam(value = "userName") String userName,  @MyRequstParam(value = "passWord") Integer passWord
    //返回值只支持MyModelAndView，数据模型和视图模型相结合
    @MyRequestMapping(value = "/hello", method = RequestMethod.POST)
    public MyModelAndView login(@MyModelAttribute("User") User user) {

        MyModelAndView myModelAndView = new MyModelAndView("success");

        MyModelMap myModel = new MyModelMap();
        User user1 = userService.queryUser("admin", "admin");
        myModel.addAttribute("test", user1.getUserName());
        myModelAndView.setModelMap(myModel);

        return myModelAndView;

    }

    @MyRequestMapping("/hello22")
    public String test() {
        return "success";
    }

}
