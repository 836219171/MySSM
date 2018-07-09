package spring.mybatis;

import lombok.extern.slf4j.Slf4j;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import spring.constants.Constants;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * @ClassName XmlBuilder
 * @Description
 * @Data 2018/7/8
 * @Author xiao liang
 */
@Slf4j
public class XmlBuilderMapper {

    public List<MapperInfo> buildMapper(String xmlMapperPath){
        List<MapperInfo> mapperInfoList = new ArrayList<>();
        MapperInfo mapperInfo = new MapperInfo();
        // 创建saxReader对象
        SAXReader reader = new SAXReader();
        // 通过read方法读取一个文件 转换成Document对象
        Document document = null;
        String pathName = Constants.PATH + xmlMapperPath;
        try {
            document = reader.read(new File(pathName));
        } catch (DocumentException e) {
            log.error("文件没有找到,{}", pathName);
        }
        //获取根节点元素
        Element node = document.getRootElement();
        mapperInfo.setInterfaceName(node.attributeValue("namespace"));
        //获取所有的bean
        List<Element> elementsList = node.elements();
        for (Element element :
                elementsList) {

            if ("select".equals(element.getName())){
                mapperInfo.setMethodName(element.attributeValue("id"));
                mapperInfo.setResultClassName(element.attributeValue("resultType"));
                mapperInfo.setSqlContent(element.getText());
            }

            mapperInfoList.add(mapperInfo);
        }

        return mapperInfoList;
    }

    public static void main(String[] args) {
        new XmlBuilderMapper().buildMapper(Constants.mybatisConfigLocation);

    }

}
