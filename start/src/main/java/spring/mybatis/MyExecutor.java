package spring.mybatis;

import lombok.extern.slf4j.Slf4j;
import spring.Utils.GetMethodName;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @ClassName MyExecutor
 * @Description 执行器
 * @Data 2018/7/8
 * @Author xiao liang
 */
@Slf4j
public class MyExecutor {

    public <T> T query(MapperInfo mapperInfo, Object[] paremeters) {
        //属性只支持string
        String preSql = mapperInfo.getSqlContent();
        String rgex = "#\\{.*?}";
        String sql = null;
        String resultClassName = mapperInfo.getResultClassName();
        Class<?> aClass = null;
        Field[] fields = null;
        Method[] methods = null;
        Object object = null;
        //Preparement注入参数的顺序
        int orderPre = 0;
        Pattern pattern = Pattern.compile(rgex);
        Matcher m = pattern.matcher(preSql);
        Connection connection = null;
        ResultSet rs = null;
        PreparedStatement preparedStatement = null;
        while (m.find()) {
            orderPre++;
        }
        sql = m.replaceAll("?");
        try {
            aClass = Class.forName(resultClassName);
            fields = aClass.getDeclaredFields();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        try {
            JDBCUtils jdbcUtils = new JDBCUtils();
             connection = jdbcUtils.getConnection();
            preparedStatement = connection.prepareStatement(sql);
            for (int i = 1; i <= orderPre; i++) {
                preparedStatement.setObject(i, paremeters[i - 1]);
            }
            rs = preparedStatement.executeQuery();
            object = aClass.newInstance();
            while (rs.next()) {
                int i = 1;
                for (Field field :
                        fields) {
                    String setMethodNameByField = GetMethodName.getSetMethodNameByField(field.getName());

                    Method method2 = aClass.getMethod(setMethodNameByField, field.getType());

                    if (field.getType().getSimpleName().equals("String")) {
                        method2.invoke(object, rs.getString(i));
                    } else if (field.getType().getSimpleName().equals("Integer")) {
                        method2.invoke(object, rs.getInt(i));
                    }
                    i++;
                }
            }
            return (T) object;

        } catch (SQLException e) {
            log.error("sql语句异常，请检查sql{}", sql);
            e.printStackTrace();
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        }
        finally {
            JDBCUtils.colseResource(connection,preparedStatement,rs);
        }


        return null;
    }


}
