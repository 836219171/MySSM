package spring.springmvc;

import lombok.Data;

/**
 * @ClassName MyModelAndView
 * @Description
 * @Data 2018/7/4
 * @Author xiao liang
 */
@Data
public class MyModelAndView {

    private String view;
    private MyModelMap modelMap;

    public MyModelAndView(String view) {
        this.view = view;
    }
}
