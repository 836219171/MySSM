package spring.mybatis;

import spring.test.IUserDao;
import spring.test.TestUserDao;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

/**
 * @ClassName InvocationHandlerImpl
 * @Description
 * @Data 2018/7/8
 * @Author xiao liang
 */
public class InvocationHandlerImpl implements InvocationHandler {

    private Object target;

    public InvocationHandlerImpl(Object object){
        this.target = object;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        Object result = null;
        System.out.println("调用开始");
        result = method.invoke(target,args);
        System.out.println("调用结束");
        return result;
    }

    public static void main(String[] args) {
        IUserDao userDao = new TestUserDao();
        InvocationHandlerImpl invocationHandler = new InvocationHandlerImpl(userDao);
        ClassLoader classLoader = invocationHandler.getClass().getClassLoader();
        Class<?>[] interfaces = userDao.getClass().getInterfaces();
        IUserDao iUserDao = (IUserDao) Proxy.newProxyInstance(userDao.getClass().getClassLoader(),interfaces,invocationHandler);
        iUserDao.save();
    }


}
