package spring.annotation;

import java.lang.annotation.*;

/**
 * Created by Xiao Liang on 2018/6/27.
 * @Desrciption 绑定简单参数类型，暂时支持String，Integer
 */
@Target({ ElementType.PARAMETER })
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface MyRequstParam {

    String value() default "";


}


