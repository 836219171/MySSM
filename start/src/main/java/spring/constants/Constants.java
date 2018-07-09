package spring.constants;

import spring.xml.FileSystemXmlApplicationContext;

/**
 * Created by Xiao Liang on 2018/6/27.
 * @Description :保存的是各个配置文件的路径
 */
public interface Constants {
    String PATH = FileSystemXmlApplicationContext.class.getResource("/").getPath();
    String contextConfigLocation = "application.xml";
    String springmvcConfigLocation = "spring-mvc.xml";
    String mybatisConfigLocation = "MyUserMapper.xml";
}


