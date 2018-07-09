package spring.demo.service.impl;

import spring.annotation.MyAutowired;
import spring.annotation.MyService;
import spring.demo.repository.RegisterDao;
import spring.demo.service.RegisterService;

/**
 * Created by Xiao Liang on 2018/6/27.
 */
@MyService
public class RegisterServiceImpl implements RegisterService{

    @MyAutowired
    private RegisterDao registerDao;

    @Override
    public void register() {
        registerDao.register();
    }
}
