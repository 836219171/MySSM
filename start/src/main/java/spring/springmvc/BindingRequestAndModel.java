package spring.springmvc;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;
import java.util.Set;

/**
 * @ClassName BindingRequestAndModel
 * @Description
 * @Data 2018/7/6
 * @Author xiao liang
 */
public class BindingRequestAndModel {


    public static void bindingRequestAndModel(MyModelAndView myModelAndView, HttpServletRequest request) {

        MyModelMap myModelMap = myModelAndView.getModelMap();
        if (!myModelMap.isEmpty()){
            Set<Map.Entry<String, Object>> entries1 = myModelMap.entrySet();
            for (Map.Entry<String, Object> entryMap :
                    entries1) {
                String key = entryMap.getKey();
                Object value = entryMap.getValue();
                request.setAttribute(key,value);
            }
        }

    }
}
