package spring.demo.repository.impl;

import spring.annotation.MyRepository;
import spring.demo.repository.UserDao;

/**
 * Created by Xiao Liang on 2018/6/27.
 */
@MyRepository
public class UserDaoImpl implements UserDao {

    @Override
    public void test() {
        System.out.println("我是UserDao");
    }
}
