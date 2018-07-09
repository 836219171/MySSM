package spring.springmvc;

import java.util.LinkedHashMap;

/**
 * @ClassName ModelMap
 * @Description
 * @Data 2018/7/4
 * @Author xiao liang
 */
public class MyModelMap extends LinkedHashMap<String,Object> implements MyModel{


    @Override
    public MyModel addAttribute(String attributeName, Object attributeValue) {
        put(attributeName,attributeValue);
        return this;
    }
}
