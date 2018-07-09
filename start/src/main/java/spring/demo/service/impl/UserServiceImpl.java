package spring.demo.service.impl;

import spring.annotation.MyAutowired;
import spring.annotation.MyService;
import spring.dataObject.User;
import spring.demo.repository.RegisterDao;
import spring.demo.repository.UserDao;
import spring.demo.repository.UserMapper;
import spring.demo.service.UserService;

/**
 * Created by Xiao Liang on 2018/6/27.
 */
@MyService
public class UserServiceImpl implements UserService {

    @MyAutowired
    private UserDao userDao;

    @MyAutowired
    private RegisterDao registerDao;

    /*********************************/
    @MyAutowired
    private UserMapper userMapper;
    /*********************************/

    @Override
    public User queryUser(String userName, String passWord) {
        return userMapper.queryUser(userName,passWord);
    }
}
