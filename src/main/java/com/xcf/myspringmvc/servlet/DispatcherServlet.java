package com.xcf.myspringmvc.servlet;

import com.xcf.myspringmvc.annotation.*;
import com.xcf.myspringmvc.controller.UserController;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebInitParam;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author xuechaofu
 * @date 2018/12/27 11:06
 */
@WebServlet(name = "dispatcherServlet", urlPatterns = "/", loadOnStartup = 1,
        initParams = {@WebInitParam(name = "base-package", value = "com.xcf.myspringmvc")})
public class DispatcherServlet extends HttpServlet {

    //扫描的基包
    private String basePackage = "";
    //基包下面所有的带包路径限定类名
    private List<String> packageNames = new ArrayList<>();
    //注解实例化 注解上的名称：实例化对象
    private Map<String, Object> instanceMap = new HashMap<>();
    //带包路径的权限定名称：注解上的名称
    private Map<String, String> nameMap = new HashMap<>();
    /**
     * URL地址和方法的映射关系  springMVC就是方法调用链
     */
    private Map<String, Method> urlMethodMap = new HashMap<>();

    /**
     * Method和权限定类名映射关系 主要时为了通过Method找到该方法的对象利用反射执行
     */
    private Map<Method, String> methodPackageMap = new HashMap<>();

    /**
     * 第一，我们应该去扫描基包下的类，得到信息A
     * 第二，对于@Controller/@Service/@Repository注解而言，我们需要拿到对应的名称，并初始化它们修饰的类，形成映射关系B
     * 第三，我们还得扫描类中的字段，如果发现有@Qualifier的话，我们需要完成注入
     * 第四，我们还需要扫描@RequestMapping，完成URL到某一个Controller的某一个方法上的映射关系C
     * 其实，Spring MVC的处理流程，就是类似这样的！
     *
     * @param config
     * @throws ServletException
     */
    @Override
    public void init(ServletConfig config) throws ServletException {
        basePackage = config.getInitParameter("base-package");
        try {
            //1.扫描基包得到全部的带包路径权限定名
            this.scanBasePackage(basePackage);
            //2.把带有@Controller，@Service，@Repository的类实例化放入Map中，key为注解上的名称
            this.instance(packageNames);
            //3.spring ioc注入
            this.springIOC();
            //4.完成URL地址与方法的映射关系
            this.handlerUrlMethodMap();

        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InstantiationException e) {
            e.printStackTrace();
        }


    }


    /**
     * 扫描基包 注意，基包是X.Y.Z的形式，而URL是X/Y/Z的形式，需要转换。
     *
     * @param basePackage
     */
    private void scanBasePackage(String basePackage) {

        URL url = this.getClass().getClassLoader().getResource(basePackage.replaceAll("\\.", "/"));
        File basePackageFile = new File(url.getPath());
        System.out.println("scan:" + basePackageFile);
        File[] childFiles = basePackageFile.listFiles();
        for (File file : childFiles) {
            if (file.isDirectory()) {
                //目录继续递归扫描
                scanBasePackage(basePackage + "." + file.getName());
            } else if (file.isFile()) {
                //类似这种：com.xcf.myspring.service.impl.UserServiceImpl.class 去掉class
                packageNames.add(basePackage + "." + file.getName().split("\\.")[0]);
            }
        }
    }

    /**
     * 实例化 从这里你可以看出，我们完成了被注解标注的类的实例化，以及和注解名称的映射。
     *
     * @param packageNames
     * @throws ClassNotFoundException
     * @throws IllegalAccessException
     * @throws InstantiationException
     */
    private void instance(List<String> packageNames) throws ClassNotFoundException, IllegalAccessException, InstantiationException {
        if (packageNames.size() < 1) {
            return;
        }

        for (String string : packageNames) {
            Class c = Class.forName(string);

            if (c.isAnnotationPresent(Controller.class)) {
                Controller controller = (Controller) c.getAnnotation(Controller.class);
                String controllerName = controller.value();

                instanceMap.put(controllerName, c.newInstance());
                nameMap.put(string, controllerName);
                System.out.println("Controller : " + string + ",value : " + controller.value());
            } else if (c.isAnnotationPresent(Service.class)) {
                Service service = (Service) c.getAnnotation(Service.class);
                String serviceName = service.value();

                instanceMap.put(serviceName, c.newInstance());
                nameMap.put(string, serviceName);
                System.out.println("Service : " + string + ",value : " + service.value());
            } else if (c.isAnnotationPresent(Repository.class)) {
                Repository repository = (Repository) c.getAnnotation(Repository.class);
                String repositoryName = repository.value();

                instanceMap.put(repositoryName, c.newInstance());
                nameMap.put(string, repositoryName);
                System.out.println("Repository : " + string + ",value : " + repository.value());
            }
        }
    }

    /**
     * 总说的Spring IOC 依赖注入
     *
     * @throws ClassNotFoundException
     * @throws IllegalAccessException
     */
    private void springIOC() throws ClassNotFoundException, IllegalAccessException {

        for (Map.Entry<String, Object> entry : instanceMap.entrySet()) {
            Field[] fields = entry.getValue().getClass().getDeclaredFields();

            for (Field field : fields) {
                if (field.isAnnotationPresent(Qualifier.class)) {
                    String name = field.getAnnotation(Qualifier.class).value();
                    field.setAccessible(true);
                    field.set(entry.getValue(), instanceMap.get(name));
                }
            }
        }
    }

    /**
     * URL映射处理 URL，我们需要提取出来，映射到Controller的Method上。
     *
     * @throws ClassNotFoundException
     */
    private void handlerUrlMethodMap() throws ClassNotFoundException {
        if (packageNames.size() < 1) {
            return;
        }

        for (String string : packageNames) {
            Class c = Class.forName(string);
            if (c.isAnnotationPresent(Controller.class)) {
                Method[] methods = c.getMethods();
                StringBuffer baseUrl = new StringBuffer();
                if (c.isAnnotationPresent(RequestMapping.class)) {
                    RequestMapping requestMapping = (RequestMapping) c.getAnnotation(RequestMapping.class);
                    baseUrl.append(requestMapping.value());
                }
                for (Method method : methods) {
                    if (method.isAnnotationPresent(RequestMapping.class)) {
                        RequestMapping requestMapping = (RequestMapping) method.getAnnotation(RequestMapping.class);
                        baseUrl.append(requestMapping.value());
                        urlMethodMap.put(baseUrl.toString(), method);
                        methodPackageMap.put(method, string);
                    }
                }
            }
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        doPost(req, resp);
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String uri = req.getRequestURI();
        String contextPath = req.getContextPath();
        String path = uri.replaceAll(contextPath, "");
        //通过path找到Method
        Method method = urlMethodMap.get(path);
        if (method != null) {
            //通过Method拿到Controller对象，准备放射执行
            String packageName = methodPackageMap.get(method);
            String controllerName = nameMap.get(packageName);

            //拿到Controller对象
            UserController userController = (UserController) instanceMap.get(controllerName);

            try {
                method.setAccessible(true);
                method.invoke(userController);
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            } catch (InvocationTargetException e) {
                e.printStackTrace();
            }


        }
    }
}
