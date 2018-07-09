package spring.demo.service;

import spring.dataObject.User;

/**
 * Created by Xiao Liang on 2018/6/27.
 */
public interface UserService {

     User queryUser(String userName, String passWord);
}
