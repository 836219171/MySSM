package spring.factory;

import lombok.Data;
import java.util.List;
/**
 * Created by Xiao Liang on 2018/6/29.
 * 用来存放xml中注入的bean
 */
@Data
public class GenericBeanDefinition {
    /**
     * className和xml中的class对应
     */
    private String className;

    /**
     *  这是bean下面的属性集合
     */
    private List<ChildBeanDefinition> childBeanDefinitionList;
}


