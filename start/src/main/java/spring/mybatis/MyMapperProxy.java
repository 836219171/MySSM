package spring.mybatis;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.List;

/**
 * @ClassName MyMapperProxy
 * @Description
 * @Data 2018/7/8
 * @Author xiao liang
 */
public class MyMapperProxy implements InvocationHandler {
    private MySqlSession mySqlSession;
    private String mybatisXmlName;

   public MyMapperProxy(MySqlSession mySqlSession , String mybatisXmlName){
       this.mySqlSession = mySqlSession;
       this.mybatisXmlName = mybatisXmlName;
   }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {

        XmlBuilderMapper xmlBuilderMapper = new XmlBuilderMapper();
        List<MapperInfo> mapperInfoList = xmlBuilderMapper.buildMapper(mybatisXmlName);
        if (mapperInfoList != null && mapperInfoList.size() != 0){
            for (MapperInfo mapperInfo :
                    mapperInfoList) {
                if (!method.getDeclaringClass().getName().equals(mapperInfo.getInterfaceName())){
                    return null;
                }
                if (method.getName().equals(mapperInfo.getMethodName())){
                    return mySqlSession.selectOne(mapperInfo,args);
                }

            }
        }

        return null;
    }
}
