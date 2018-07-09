package spring.springmvc;

import lombok.extern.slf4j.Slf4j;
import spring.Utils.AnnotationUtils;
import spring.Utils.ConvertUtis;
import spring.Utils.GetMethodName;
import spring.Utils.StringUtils;
import spring.annotation.MyModelAttribute;
import spring.exception.springmvcException;

import javax.servlet.http.HttpServletRequest;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;

/**
 * @ClassName BindingByMyModelAttribute
 * @Description 参数注解是MyModelAttribute时，绑定数据的类
 * @Data 2018/7/4
 * @Author xiao liang
 */
@Slf4j
public class BindingByMyModelAttribute implements   BindingParamter {

    @Override
    public Object bindingParamter(Parameter parameter, HttpServletRequest request) throws IllegalAccessException, InstantiationException, NoSuchMethodException {
        MyModelAttribute myModelAttribute = parameter.getAnnotation(MyModelAttribute.class);
        Class<?> aClass = parameter.getType();
        if (!AnnotationUtils.isEmpty(myModelAttribute)){
            if (!aClass.getSimpleName().equals(myModelAttribute.value())){
                throw new springmvcException("实体类绑定异常，请重新检查");
            }
        }

        Field[] fields = aClass.getDeclaredFields();
        Object object = aClass.newInstance();
        for (Field field :
                fields) {
            String parameter1 = request.getParameter(field.getName());
            if (!StringUtils.isEmpty(parameter1)){
                Object setObject = ConvertUtis.convert(field.getType().getSimpleName(),parameter1);
                String methodName = GetMethodName.getSetMethodNameByField(field.getName());
                Method method = aClass.getMethod(methodName, field.getType());
                try {
                    method.invoke(object,setObject);

                } catch (InvocationTargetException e) {
                    log.error("{}属性赋值异常",field.getName());
                    e.printStackTrace();
                }

            }
        }


        return object;

    }




}
