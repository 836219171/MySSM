package spring.Utils;

/**
 * @ClassName isBasicType
 * @Description 判断是不是基本数据类型
 * @Data 2018/7/4
 * @Author xiao liang
 */
public class isBasicTypeUtils {
    public static boolean  isBasicType(String typeName){
        if (typeName.equals("String")){
            return true;
        }
        else if(typeName.equals("Integer")){
            return true;
        }
        else if(typeName.equals("int")){
            return true;
        }
        else if(typeName.equals("Long")){
            return true;
        }
        else if(typeName.equals("Short")){
            return true;
        }
        else if(typeName.equals("Float")){
            return true;
        }
        else if(typeName.equals("Double")){
            return true;
        }
        else if(typeName.equals("Byte")){
            return true;
        }
        return false;
    }
}
