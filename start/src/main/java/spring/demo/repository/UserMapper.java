package spring.demo.repository;

import spring.dataObject.User;

/**
 * @ClassName UserMapper
 * @Description
 * @Data 2018/7/7
 * @Author xiao liang
 */
public interface UserMapper {

    User queryUser(String userName,String passWord);

}
