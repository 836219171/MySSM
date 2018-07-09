package spring.annotation;

import java.lang.annotation.*;

/**
 * Created by Xiao Liang on 2018/6/27.
 */
@Target({ ElementType.TYPE })
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface MyController {

    /**
     * The value may indicate a suggestion for a logical component name,
     * to be turned into a Spring bean in case of an autodetected component.
     * @return the suggested component name, if any (or empty String otherwise)
     */
    String value() default "";
}

