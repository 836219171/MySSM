package spring.Utils;

/**
 * @ClassName ConvertUtis
 * @Description 根据传入的属性和类名，将属性名强转为类名的属性
 * @Data 2018/7/4
 * @Author xiao liang
 */
public class ConvertUtis {


    public static Object  convert(String className,String parameter){

        if (className.equals("String")){
            return parameter;
        }
        else if (className.equals("Integer")){
            return Integer.valueOf(parameter);
        }
        else if (className.equals("int")){
            return Integer.valueOf(parameter);
        }
        else if (className.equals("Float")){
            return Float.valueOf(parameter);
        }
        else if (className.equals("Double")){
            return Integer.valueOf(parameter);
        }

        else if (className.equals("Long")){
            return Long.valueOf(parameter);
        }
        else if (className.equals("Short")){
            return Short.valueOf(parameter);
        }
        else if (className.equals("Byte")){
            return Byte.valueOf(parameter);
        }
        else if (className.equals("Boolean")){
            return Boolean.valueOf(parameter);
        }

        return null;

    }
}
