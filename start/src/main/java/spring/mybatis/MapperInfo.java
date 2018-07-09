package spring.mybatis;

import lombok.Data;

/**
 * @ClassName MapperInfo
 * @Description
 * @Data 2018/7/8
 * @Author xiao liang
 */
@Data
public class MapperInfo {

    private String interfaceName;
    private String sqlContent;
    private String methodName;
    private String resultClassName;
}
