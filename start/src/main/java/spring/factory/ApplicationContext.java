package spring.factory;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import lombok.extern.slf4j.Slf4j;

import spring.Person;
import spring.Utils.GetMethodName;
import spring.constants.Constants;
import spring.exception.XmlException;

import spring.xml.FileSystemXmlApplicationContext;

import spring.xml.XmlApplicationContext;
import spring.xmlRules.IocRules;

/**
 * Created by Xiao Liang on 2018/6/27.
 * 上下文获取的方法
 */
@Slf4j
public class ApplicationContext extends FileSystemXmlApplicationContext implements BeanFactory {

    public Map<String, GenericBeanDefinition> subMap = null;


    public ApplicationContext(String contextConfigLocation) {
        this.subMap = super.getGenericBeanDefinition(contextConfigLocation);
    }

    /**
     * 获取bean的方法
     * @param beanId
     * @return
     */
    @Override
    public Object getBean(String beanId) {

        assert beanId == null : "beanId不存在";
        Object object = null;
        Class<?> aClass = null;
        Set<Map.Entry<String, GenericBeanDefinition>> entries = subMap.entrySet();

        // 判断容器中是否存在beanId
        if (subMap.containsKey(beanId)) {

            // 如果存在。开始遍历每一个bean
            for (Map.Entry<String, GenericBeanDefinition> entry : entries) {

                // 如果beanId在容器中找到了
                if (beanId.equals(entry.getKey())) {

                    // 声明一个容器中的子对象，用来保存子元素
                    GenericBeanDefinition genericBeanDefinition = entry.getValue();
                    String beanName = genericBeanDefinition.getClassName();

                    // 此对象的意思是对象的属性集合
                    List<ChildBeanDefinition> childBeanDefinitionList =
                            genericBeanDefinition.getChildBeanDefinitionList();

                    try {
                        aClass = Class.forName(beanName);
                    } catch (ClassNotFoundException e) {
                        log.error("{}没有找到", beanName);
                        e.printStackTrace();
                    }

                    try {
                        object = aClass.newInstance();
                    } catch (InstantiationException e) {
                        log.error("实例化对象异常");
                        e.printStackTrace();
                    } catch (IllegalAccessException e) {
                        e.printStackTrace();
                    }

                    // 遍历属性集合
                    for (ChildBeanDefinition childBeanDefinition : childBeanDefinitionList) {

                        // 如果xml中的属性和IocRules中定义的setRule属性一致，则使用set注入
                        if (IocRules.SET_INJECT.getType().equals(childBeanDefinition.getChildrenType())) {
                            setValue(aClass, childBeanDefinition, object);
                        }

                        // 同理，如果符合构造器注入的规则，则使用构造器注入
                        else if (IocRules.CONS_INJECT.getType().equals(childBeanDefinition.getChildrenType())) {
                            List<ChildBeanDefinition> constructorChildBeanDefinition = new ArrayList<>();

                            // 构造器注入需要同时注入所有属性
                            for (ChildBeanDefinition conChildBeanDefinition : childBeanDefinitionList) {
                                if (IocRules.CONS_INJECT.getType().equals(conChildBeanDefinition.getChildrenType())) {
                                    constructorChildBeanDefinition.add(conChildBeanDefinition);
                                }
                            }
                            object = consValue(aClass, constructorChildBeanDefinition, object);

                            break;
                        }
                    }
                }
            }
        } else {
            throw new XmlException("容器中不存在该bean");
        }

        return object;
    }

    /**
     * @param aClass
     * @param childBeanDefinitionList
     * @param object
     * @return 构造器注入的方法 暂时只支持注入String，Integer，int属性
     */
    private Object consValue(Class<?> aClass, List<ChildBeanDefinition> childBeanDefinitionList, Object object) {
        Constructor<?> constructor = null;
        Field[] fields = aClass.getDeclaredFields();
        Class<?>[] classArray = new Class[fields.length];

        for (int i = 0; i < fields.length; i++) {
            if ("String".equals(fields[i].getType().getSimpleName())) {
                classArray[i] = String.class;
            } else if ("Integer".equals(fields[i].getType().getSimpleName())) {
                classArray[i] = Integer.class;
            } else if ("int".equals(fields[i].getType().getSimpleName())) {
                classArray[i] = int.class;
            }
        }
        try {
            constructor = aClass.getConstructor(classArray);

            try {
                //这里没有写成动态的,固定写死了，需要改一下
                object = constructor.newInstance(childBeanDefinitionList.get(0).getAttributeOne(),
                        childBeanDefinitionList.get(1).getAttributeOne());
            } catch (InstantiationException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            } catch (InvocationTargetException e) {
                e.printStackTrace();
            }
        } catch (NoSuchMethodException e) {
            log.error("没有找到,{}", constructor);
        }

        return object;
    }



    /**
     * set注入
     *
     * @param aClass
     * @param childBeanDefinition
     * @param object
     */
    private void setValue(Class<?> aClass, ChildBeanDefinition childBeanDefinition, Object object) {
        Field field = null;
        Method[] methods = null;

        // 只支持 int Integer String的属性注入
        String type = null;
        String propertyName = childBeanDefinition.getAttributeOne();
        String propertyValue = childBeanDefinition.getAttributeTwo();

        // 拼接set方法名
        String methodName = GetMethodName.getSetMethodNameByField(propertyName);

        // 获取属性的类
        try {
            field = aClass.getDeclaredField(propertyName);
            type = field.getType().getSimpleName();
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        }

        try {

            // method = aClass.getMethod(methodName, String.class);
            methods = aClass.getMethods();

            for (Method method : methods) {
                if (methodName.equals(method.getName())) {
                    try {

                        // 判断属性是什么类
                        if ("String".equals(type)) {
                            method.invoke(object, propertyValue);
                        } else if ("Integer".equals(type)) {

                            Integer propertyValue2 = Integer.valueOf(propertyValue);
                            method.invoke(object, propertyValue2);
                        } else if ("int".equals(type)) {
                            Integer propertyValue2 = Integer.valueOf(propertyValue);

                            method.invoke(object, propertyValue2);
                        } else {
                            log.error("暂时不支持该属性,{}", type);
                        }
                    } catch (IllegalAccessException e) {
                        log.error("没有找到该方法名+{}", method.getName());
                        e.printStackTrace();
                    } catch (InvocationTargetException e) {
                        e.printStackTrace();
                    }
                }
            }
        } catch (Exception e) {
            log.error("方法注入异常");
            e.printStackTrace();
        }
    }


    public static void main(String[] args) {
        ApplicationContext applicationContext = new ApplicationContext(Constants.contextConfigLocation);
        Person person = (Person) applicationContext.getBean("hostess");
        System.out.println(person.getPassWord());
    }
}


