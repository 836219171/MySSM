package spring.xml;

import lombok.extern.slf4j.Slf4j;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import spring.Utils.StringUtils;
import spring.Utils.scan.ScanUtil;
import spring.constants.Constants;
import spring.exception.XmlException;
import spring.factory.ChildBeanDefinition;
import spring.factory.GenericBeanDefinition;
import spring.xmlRules.IocRules;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by Xiao Liang on 2018/6/28.
 * 封装解析xml的方法，模仿Ioc注入 BeanDefinition。实际注入的是GenericBeanDefinition
 */
@Slf4j
public class XmlApplicationContext {


    /**
     * @Description 将xml中的bean元素注入到容器中的方法
     *
     * @return 返回值是指定xml中的bean的容器
     */
    public  Map<String, GenericBeanDefinition> getBeanDefinitionMap(String contextConfigLocation) {
        Map<String, GenericBeanDefinition> beanDefinitionXmlMap = new ConcurrentHashMap<>(256);
        List<Element> elementsList = getElements(contextConfigLocation);
        //遍历每一个bean，注入beanDefinitionMap
        for (Element element :
                elementsList) {
            if (element.getName().equals("bean")){
                //声明一个bean的map,用来盛放当前bean子元素的容器
                GenericBeanDefinition genericBeanDefinition = new GenericBeanDefinition();
                List<ChildBeanDefinition> childBeanDefinitionList = new ArrayList<>();
                String beanId = element.attributeValue(IocRules.BEAN_RULE.getName());
                String beanClass = element.attributeValue(IocRules.BEAN_RULE.getValue());
                //保证子元素确实存在
                if (!StringUtils.isEmpty(beanId) && !StringUtils.isEmpty(beanClass)) {
                    //当前bean的className
                    genericBeanDefinition.setClassName(beanClass);
                    //当前bean的所有子元素
                    List<Element> elements = element.elements();
                    if (elements != null) {
                        for (Element childrenElement :
                                elements) {
                            //如果匹配set注入规则,则注入到容器
                            if (childrenElement.getName().equals(IocRules.SET_INJECT.getType())) {
                                ChildBeanDefinition childBeanDefinition = new ChildBeanDefinition();
                                childBeanDefinition.setChildrenType(IocRules.SET_INJECT.getType());
                                String name = IocRules.SET_INJECT.getName();
                                String value = IocRules.SET_INJECT.getValue();
                                setChildBeanDefinitionByType(childrenElement, childBeanDefinition, name, value, childBeanDefinitionList);
                            }
                            //如果匹配构造器注入规则,则注入到容器
                            else if (childrenElement.getName().equals(IocRules.CONS_INJECT.getType())) {
                                ChildBeanDefinition childBeanDefinition = new ChildBeanDefinition();
                                childBeanDefinition.setChildrenType(IocRules.CONS_INJECT.getType());
                                String name = IocRules.CONS_INJECT.getName();
                                String value = IocRules.CONS_INJECT.getValue();
                                setChildBeanDefinitionByType(childrenElement, childBeanDefinition, name, value, childBeanDefinitionList);
                            }
                        }

                    } else {
                        log.info("{}下面没有子元素", beanId);
                    }
                    genericBeanDefinition.setChildBeanDefinitionList(childBeanDefinitionList);
                    beanDefinitionXmlMap.put(beanId, genericBeanDefinition);
                }
            }
        }

        return beanDefinitionXmlMap;
    }

    /**
     * @Description 根据指定的xml，获得注解扫描的bean容器
     * @param contextConfigLocation
     * @return
     */
    public List<String> getComponentList(String contextConfigLocation){
        List<String> componentList = new ArrayList<>();
        List<Element> elementsList = getElements(contextConfigLocation);
        for (Element element :
                elementsList) {
            if (element.getName().equals(IocRules.SNAN_RULE.getType())) {
                String packageName = element.attributeValue(IocRules.SNAN_RULE.getName());
                componentList.addAll(resolveComponentList(packageName));
            }
        }

        return componentList;
    }



    /**
     * 根据要扫描的包名，返回有注解扫描的类
     * @param packageName
     * @return
     */
    public List<String> resolveComponentList(String packageName){

        if (StringUtils.isEmpty(packageName)){
            throw new XmlException("请正确设置"+IocRules.SNAN_RULE.getType()+"的属性");
        }
        List<String> componentList = new ArrayList<>();
        List<String> componentListAfter = ScanUtil.getComponentList(packageName);
        componentList.addAll(componentListAfter);
        return  componentList;
    }


    /**
     * 将每个bean的子元素注入容器
     *
     * @param element
     * @param childBeanDefinition
     * @param name
     * @param value
     * @param childBeanDefinitionList
     */
    private void setChildBeanDefinitionByType(Element element, ChildBeanDefinition childBeanDefinition, String name, String value,
                              List<ChildBeanDefinition> childBeanDefinitionList) {

        if (childBeanDefinition != null) {

            childBeanDefinition.setAttributeOne(element.attributeValue(name));
            childBeanDefinition.setAttributeTwo(element.attributeValue(value));
            childBeanDefinitionList.add(childBeanDefinition);

        } else {
            throw new XmlException("未按照格式配置xml文件或者暂不支持改配置属性");
        }

    }

    /**
     * 解析xml的工厂,根据路径名获取根元素下面的所有子元素
     * @param contextConfigLocation
     * @return
     */
    private List<Element> getElements(String contextConfigLocation) {
        // 创建saxReader对象
        SAXReader reader = new SAXReader();
        // 通过read方法读取一个文件 转换成Document对象
        Document document = null;
        String pathName = Constants.PATH + contextConfigLocation;
        try {
            document = reader.read(new File(pathName));
        } catch (DocumentException e) {
            log.error("文件没有找到,{}", pathName);
        }
        //获取根节点元素
        Element node = document.getRootElement();
        //获取所有的bean
        List<Element> elementsList = node.elements();
        return elementsList;

    }


}
