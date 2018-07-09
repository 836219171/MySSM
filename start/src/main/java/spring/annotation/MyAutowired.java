package spring.annotation;

import java.lang.annotation.*;

/**
 * Created by Xiao Liang on 2018/6/27.
 */
@Target({
    ElementType.CONSTRUCTOR, ElementType.METHOD, ElementType.PARAMETER, ElementType.FIELD, ElementType.ANNOTATION_TYPE
})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface MyAutowired {
    boolean required() default true;
}


