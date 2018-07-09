package spring.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import javax.xml.bind.Element;

/**
 * Created by Xiao Liang on 2018/6/27.
 */
@Target({ ElementType.FIELD, ElementType.METHOD, ElementType.TYPE })
@Retention(RetentionPolicy.RUNTIME)
public @interface UserAnnotattion {
    String userName() default "lilei";
    String passWord() default "xiaobi";
}


