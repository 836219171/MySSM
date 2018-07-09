package spring.factory;


import lombok.extern.slf4j.Slf4j;
import spring.Utils.AnnotationUtils;
import spring.annotation.MyAutowired;
import spring.constants.Constants;
import spring.mybatis.MySqlSession;

import java.lang.reflect.Field;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by Xiao Liang on 2018/6/27.
 */
@Slf4j
public class InitBean extends BeanDefinition {

    //初始化后的bean容器 key为class名，value为实例化对象
    public Map<String, Object> beanContainerMap = new ConcurrentHashMap<>();

    /**
     * 初始化bean容器方法
     * 注意，扫描的bean会覆盖xml中配置的bean，spring也是这样，扫描的注入和装配都是在xml之后
     * MyAutowired暂时是根据名称装配和扫描
     */

    public void initBeans() {
        //初始化xml配置
        initXmlBeans(Constants.contextConfigLocation);
        initXmlBeans(Constants.springmvcConfigLocation);
        //初始化扫描注解的配置
        initAutowiredBeans(Constants.contextConfigLocation);
    }

    /**
     * 初始化xml中bean内容的方法
     */
    public void initXmlBeans(String contextConfigLocation) {
        ApplicationContext applicationContext = new ApplicationContext(contextConfigLocation);
        Class<?> aClass = null;
        //从容器中取出bean，用application的getbean方法依次加载bean
        Map<String, GenericBeanDefinition> beanDefinitionMap = super.getbeanDefinitionXmlMap(contextConfigLocation);
        Set<Map.Entry<String, GenericBeanDefinition>> entries = beanDefinitionMap.entrySet();
        for (Map.Entry<String, GenericBeanDefinition> entry :
                entries) {
            String beanId = entry.getKey();
            String className = entry.getValue().getClassName();
            try {
                aClass = Class.forName(className);

            } catch (ClassNotFoundException e) {
                log.error("xml中{}无法实例化", className);
                e.printStackTrace();
            }
            beanContainerMap.put(className, aClass.cast(applicationContext.getBean(beanId)));
        }
    }


    /**
     * 将所有的componentList(也就是加注解的类)里面的bean实例化
     *
     * @return
     */
    public void initAutowiredBeans(String contextConfigLocation) {
        List<String> componentList = super.getComponentList(contextConfigLocation);
        System.out.println("实例化的顺序" + componentList);
        //扫描到有注解的类，初始化类的名单
        for (String className :
                componentList) {
            //将每一个类初始化
            try {
                initClass(className);
            } catch (ClassNotFoundException e) {
                log.error("{}没有找到", className);
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            } catch (InstantiationException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 初始化每一个类的方法,初始化的时候由于spring要实现使用接口注入，所以比较麻烦
     * 需要根据类名来判断是否有接口，然后在将接口名和实现类对应上装配到容器中
     *
     * @param className
     */
    public void initClass(String className) throws ClassNotFoundException, IllegalAccessException, InstantiationException {
        Class<?> aClass = Class.forName(className);
        //先判断这个类有没有接口，如果有接口，将接口装配
        Class<?>[] interfaces = aClass.getInterfaces();

       //如果类是接口，注入的对象是动态代理的对象
        if (aClass.isInterface()){
            MySqlSession mySqlSession = new MySqlSession();
            beanContainerMap.put(aClass.getName(),mySqlSession.getMapper(aClass, Constants.mybatisConfigLocation));
        }
       //如果不是接口的实现类，也就是controller层
        else if (interfaces == null || interfaces.length == 0) {
            noInterfaceInit(className, className);
        }
        else {
            for (Class<?> interfaceClass :
                    interfaces) {
                boolean flag = isExistInContainer(className);
                //容器中如果有，则直接使用这个对象进行装配
                if (flag) {
                    beanContainerMap.put(interfaceClass.getName(), aClass.newInstance());
                } else {
                    //如果容器中没有，则先实例化实现类，然后再装配到容器中
                    noInterfaceInit(className, interfaceClass.getName());
                }
            }
        }
    }

    /**
     * @param className
     * @param interfaceName
     */
    public void noInterfaceInit(String className, String interfaceName) throws ClassNotFoundException, IllegalAccessException, InstantiationException {
        Class<?> aClass = Class.forName(className);
        //bean实例化
        System.out.println("实例化的名字"+aClass.getName());
        Object object = aClass.newInstance();
        Field[] declaredFields = aClass.getDeclaredFields();
        for (Field field :
                declaredFields) {
            //如果属性上有MyAutowired注解，则先将属性注入进去
            if (!AnnotationUtils.isEmpty(field.getAnnotation(MyAutowired.class))) {
                //System.out.println("发现注解");
                //设置私有属性可见
                field.setAccessible(true);
                //如果有注解，在实例化链表里面搜寻类名
                Set<Map.Entry<String, Object>> entries = beanContainerMap.entrySet();
                for (Map.Entry<String, Object> entry :
                        entries) {
                    String type = field.getType().getName();
                    if (entry.getKey().equals(type)){
                        field.set(object, entry.getValue());
                    }
                }
            }

        }
        beanContainerMap.put(interfaceName, object);

    }

    /**
     * 属于工具类，不是很重要
     * 在实例化该类之前先判断该类在容器中是否存在
     *
     * @param className
     * @return
     */
    public boolean isExistInContainer(String className) {
        Set<Map.Entry<String, Object>> entries = beanContainerMap.entrySet();
        if (entries != null) {
            for (Map.Entry<String, Object> map :
                    entries) {
                if (map.getKey().equals(className)) {
                    return true;
                } else {
                    return false;
                }
            }
        }
        return false;
    }


    public static void main(String[] args) {
        InitBean initBean = new InitBean();
        initBean.initBeans();
        System.out.println(initBean.beanContainerMap);
        //测试初始化是否成功
 /*       InitBean initBean = new InitBean();
        initBean.initBeans();
        JDBCUtils jDBCUtils = null;
        System.out.println(initBean.beanContainerMap);
        Set<Map.Entry<String, Object>> entries = initBean.beanContainerMap.entrySet();
        for (Map.Entry<String, Object> entry :
                entries) {
          *//*  if (entry.getKey().equals("spring.demo.controller.RegisterController")) {
                RegisterController loginController = (RegisterController) entry.getValue();
                loginController.regeister();
            }*//*
            if (entry.getKey().equals("spring.mybatis.JDBCUtils")) {
                jDBCUtils = (JDBCUtils) entry.getValue();
            }
        }
        Connection connection = jDBCUtils.getConnection();
        String sql = "select * from login_user where id = ?";
        ResultSet rs = null;
        PreparedStatement preparedStatement = null;

        try {
            preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setString(1,"1");
        } catch (SQLException e) {
            log.error("sql语句异常，请检查sql{}",sql);
            e.printStackTrace();
        }
        try {
            rs = preparedStatement.executeQuery();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        try {
            while (rs.next()){
                System.out.println(rs.getString(1));
                System.out.println(rs.getString(2));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }*/

    }

}


