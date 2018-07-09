package spring.servlet;

import lombok.extern.slf4j.Slf4j;
import spring.factory.InitBean;
import spring.springmvc.Binding;
import spring.springmvc.Handler;
import spring.springmvc.MyModelAndView;
import spring.springmvc.ViewResolver;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static spring.springmvc.BindingRequestAndModel.bindingRequestAndModel;

/**
 * Created by Xiao Liang on 2018/6/27.
 */
@WebServlet(name = "MyDispatcherServlet")
@Slf4j
public class MyDispatcherServlet extends HttpServlet {

    /**
     * 初始化servlet，将bean容器和HandlerMapping放到servlet的全局变量中
     */
    @Override
    public void init() {
        InitBean initBean = new InitBean();
        initBean.initBeans();
        //根据bean容器中注册的bean获得HandlerMapping
        Map<String, Method> bindingRequestMapping = Handler.bindingRequestMapping(initBean.beanContainerMap);
        ServletContext servletContext = this.getServletContext();
        servletContext.setAttribute("beanContainerMap", initBean.beanContainerMap);
        servletContext.setAttribute("bindingRequestMapping", bindingRequestMapping);
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        try {
            doDispatch(request, response);
        } catch (Exception e) {
            log.error("控制器处理异常");
            e.printStackTrace();
        }
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        doPost(request, response);

    }
    //接收到请求后转发到相应的方法上
    private void doDispatch(HttpServletRequest request, HttpServletResponse response) throws IOException, InvocationTargetException, IllegalAccessException, InstantiationException {
        ServletContext servletContext = this.getServletContext();
        //获取扫描controller注解后url和方法绑定的mapping，也就是handlerMapping
        Map<String, Method> bindingRequestMapping =
                (Map<String, Method>) servletContext.getAttribute("bindingRequestMapping");
        //获取实例化的bean容器
        Map<String, Object> beanContainerMap = (Map<String, Object>) servletContext.getAttribute("beanContainerMap");
        String url = request.getServletPath();
        Set<Map.Entry<String, Method>> entries = bindingRequestMapping.entrySet();
        List<Object> resultParameters = Binding.bingdingMethodParamters(bindingRequestMapping, request);
        for (Map.Entry<String, Method> entry :
                entries) {
            if (url.equals(entry.getKey())) {
                Method method = entry.getValue();
                Class<?> returnType = method.getReturnType();
                    //如果返回值是MyModelAndView，开始绑定
                if ("MyModelAndView".equals(returnType.getSimpleName())){
                    Object object = beanContainerMap.get(method.getDeclaringClass().getName());
                    //获取springmvc.xml中配置的视图解析器
                    ViewResolver viewResolver = (ViewResolver) beanContainerMap.get("spring.springmvc.ViewResolver");
                    String prefix = viewResolver.getPrefix();
                    String suffix = viewResolver.getSuffix();
                    MyModelAndView myModelAndView = (MyModelAndView) method.invoke(object, resultParameters.toArray());
                    //将request和model中的数据绑定
                    bindingRequestAndModel(myModelAndView,request);
                    String returnViewName = myModelAndView.getView();
                    //返回的路径
                    String resultAddress = prefix + returnViewName + suffix;
                    System.out.println(resultAddress);
                    try {
                      //  request.setAttribute("test","testModel");
                        request.getRequestDispatcher(resultAddress).forward(request,response);
                    } catch (ServletException e) {
                        e.printStackTrace();
                    }
                }

            }
        }


    }



}
