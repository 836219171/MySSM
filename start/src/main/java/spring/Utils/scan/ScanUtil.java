package spring.Utils.scan;

import lombok.extern.slf4j.Slf4j;
import spring.Utils.AnnotationUtils;
import spring.Utils.ListAddUtils;
import spring.annotation.MyAutowired;
import spring.annotation.MyController;
import spring.annotation.MyRepository;
import spring.annotation.MyService;

import java.io.File;
import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.net.URL;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by Xiao Liang on 2018/6/27.
 * 扫描工具类(核心方法是getClassName和getComponentList)
 * 1 扫描包下的注解
 * 2 扫描包下的类名
 */
@Slf4j
public class ScanUtil {

    private static List<String> listClassName = new ArrayList<>();
    private static List<String> componentList = new ArrayList<>();
    private static Map<String, String> interfaceAndImplMap = new ConcurrentHashMap<>();

    /**
     * 扫描指定包下面的所有类名
     *
     * @param packageName,包名
     * @return 类名的集合，
     */
    public static List<String> getClassName(String packageName) {
        Enumeration<URL> urls = null;
        ClassLoader contextClassLoader = Thread.currentThread().getContextClassLoader();
        String newPackageName = packageName.replace(".", "/");

        try {
            urls = contextClassLoader.getResources(newPackageName);
            while (urls.hasMoreElements()) {
                URL url = urls.nextElement();
                File packageFile = new File(url.getPath());
                File[] files = packageFile.listFiles();
                if (files == null) {
                    break;
                }
                for (File file :
                        files) {
                    //如果是class，则添加到list中返回
                    if (file.getName().endsWith(".class")) {
                        String templeName = (packageName.replace("/", ".") + "." + file.getName());
                        String newTempleName = templeName.substring(0, templeName.lastIndexOf("."));
                        listClassName.add(newTempleName);
                    }
                    //如果是package，则继续遍历
                    else {
                        String nextPackageName = newPackageName + "." + file.getName();
                        getClassName(nextPackageName);
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return listClassName;
    }


    /**
     * 返回 有注解的实例化顺序的链表
     */
    public static List<String> getComponentList(String packageName) {
        //获取所有类
        List<String> classNameList = getClassName(packageName);
        //将扫描的接口和其实现类，使用map对应上,模仿spring接口注入，复杂的原因是java不支持从接口获取实现类
        makeInterfaceAndImplMap(classNameList);

        for (String className :
                classNameList) {
            try {
                //实例化每个类
                resolveComponent(className);
            } catch (ClassNotFoundException e) {
                log.error("扫描注解的时候,{}没有找到", className);
                e.printStackTrace();
            }
        }
        return componentList;

    }


    /**
     * getComponentList();递归调用的子方法
     *
     * @param className
     */
    public static void resolveComponent(String className) throws ClassNotFoundException {
        Class<?> aClass = Class.forName(className);
        //在此处添加要识别的注解，也是每次扫描的顺序，最好遵循习惯
        addNewAnnotation(MyController.class, aClass);
        addNewAnnotation(MyService.class, aClass);
        addNewAnnotation(MyRepository.class, aClass);
    }

    public static <A extends Annotation> void addNewAnnotation(Class<A> annotationClass, Class<?> aClass) throws ClassNotFoundException {
        //如果类上有注解
        if (!AnnotationUtils.isEmpty(aClass.getAnnotation(annotationClass))) {
            Field[] fields = aClass.getDeclaredFields();
            if (fields == null || fields.length == 0) {
                ListAddUtils.add(componentList, aClass.getName());
            } else {
                //跳出递归的语句，也就是最底层的类，如果所有属性没有@MyAutowired注解，则注入到链表中
                if (isEmptyAutowired(fields)) {
                    ListAddUtils.add(componentList, aClass.getName());
                } else {
                    //如果属性上有@MyAutowired，则继续递归
                    for (Field field :
                            fields) {
                        //递归具体的查找到底哪个属性上有@MyAutowired。
                        if (field.getAnnotation(MyAutowired.class) != null) {
                            //如果有则根据类名查找类，然后去对应的类中递归此过程
                            String newFieldName = field.getType().getName();
                            //如果是接口，则用其实现类注入
                            if (Class.forName(newFieldName).isInterface()) {
                                String nextName = convertInterfaceToImpl(newFieldName);
                                if (!componentList.contains(nextName)) {
                                    resolveComponent(nextName);
                                }
                            } else {
                                resolveComponent(newFieldName);
                            }
                        }
                    }
                    ListAddUtils.add(componentList, aClass.getName());
                }

            }

        }

        //如果是需要动态的代理注入的接口，加入到实例化的链表中
        else if (aClass.isInterface() && interfaceAndImplMap.get(aClass.getName()).equals("proxy")) {
            ListAddUtils.add(componentList, aClass.getName());
        }


    }

    /**
     * 判断一组属性里面有没有注解
     *
     * @param fields
     * @return
     */
    private static boolean isEmptyAutowired(Field[] fields) {
        for (Field field :
                fields) {
            if (!AnnotationUtils.isEmpty(field.getAnnotation(MyAutowired.class))) {
                return false;
            }
        }
        return true;

    }

    /**
     * 工具类，组装接口和实现类
     *
     * @param classNameList
     * @return
     */
    private static Map<String, String> makeInterfaceAndImplMap(List<String> classNameList) {

        Class<?> aClass = null;

        //interfaceNameList是所有接口类名的链表
        List<String> interfaceNameList = new ArrayList<>();
        //这个链表保存的是有实现类的接口的链表名，默认没有实现类的接口即为需要动态注的链表
        List<String> interfaceExist = new ArrayList<>();
        //循环类名，将类名注入到链表中
        for (String className :
                classNameList) {
            try {
                aClass = Class.forName(className);
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
            if (aClass.isInterface()) {
                interfaceNameList.add(aClass.getName());
            }
        }

        for (String className :
                classNameList) {
            Class<?> bClass = null;
            try {
                bClass = Class.forName(className);
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
            Class<?>[] interfaces = bClass.getInterfaces();
            //如果是接口的实现类
            if (interfaces != null && interfaces.length != 0) {
                for (String interfaceName :
                        interfaceNameList) {
                    for (Class<?> interfaceClass :
                            interfaces) {
                        //如果既有接口，也有实现类，则组成map
                        if (interfaceName.equals(interfaceClass.getName())) {
                            interfaceAndImplMap.put(interfaceName, className);
                            interfaceExist.add(interfaceName);
                        }
                    }

                }
            }

        }

        //需要动态代理注入的接口，在map中用value = proxy来识别
        interfaceNameList.removeAll(interfaceExist);
        if (interfaceNameList != null && interfaceNameList.size() > 0) {
            for (String spareInterfaceName :
                    interfaceNameList) {
                interfaceAndImplMap.put(spareInterfaceName, "proxy");
            }
            System.out.println("已经存在的" + interfaceNameList);

        }
        return null;
    }

    /**
     * 工具类:接口转换为实现类
     *
     * @param newFileName
     * @return
     */
    private static String convertInterfaceToImpl(String newFileName) {
        Set<Map.Entry<String, String>> entries = interfaceAndImplMap.entrySet();
        for (Map.Entry<String, String> entry :
                entries) {
            if (newFileName.equals(entry.getKey()) && !entry.getValue().equals("proxy")) {
                return entry.getValue();
            } else if (newFileName.equals(entry.getKey()) && entry.getValue().equals("proxy")) {
                return entry.getKey();
            }

        }
        return null;
    }


    public static void main(String[] args) {

        System.out.println(getComponentList("spring.demo"));
    }


}
