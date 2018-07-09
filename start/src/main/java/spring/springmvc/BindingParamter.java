package spring.springmvc;

import javax.servlet.http.HttpServletRequest;
import java.lang.reflect.Parameter;

/**
 * @ClassName BindingRoles
 * @Description
 * @Data 2018/7/4
 * @Author xiao liang
 */
public interface BindingParamter {

     Object bindingParamter(Parameter parameter, HttpServletRequest request) throws IllegalAccessException, InstantiationException, NoSuchMethodException;

}
