package spring.springmvc;

import spring.Utils.StringUtils;
import spring.annotation.MyRequstParam;
import spring.exception.springmvcException;

import javax.servlet.http.HttpServletRequest;
import java.lang.reflect.Parameter;

/**
 * @ClassName BindingByMyRequstParam
 * @Description 参数注解是MyMyRequstParam时，绑定数据的类
 * @Data 2018/7/4
 * @Author xiao liang
 */
public class BindingByMyRequstParam implements BindingParamter {

    @Override
    public Object bindingParamter(Parameter parameter, HttpServletRequest request) {

        MyRequstParam myRequstParam = parameter.getAnnotation(MyRequstParam.class);
        String MyRequstParamValue = myRequstParam.value();
        String parameterType = parameter.getType().getSimpleName();
        //测试，看看是什么，初步估计是空
        String parameter1 = request.getParameter(MyRequstParamValue);
        if (StringUtils.isEmpty(parameter1)) {
            throw new springmvcException("绑定参数异常");
        }

        if (parameterType.equals("String")) {
            //parameter1赋值
            return parameter1;

        } else if (parameterType.equals("Integer") || parameterType.equals("int")) {
          Integer binddingParameter =  Integer.valueOf(parameter1);
          return binddingParameter;
        }
        return null;


    }


}
