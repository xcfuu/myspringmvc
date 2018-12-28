# 简易实现SpringMVC

- 第一，我们应该去扫描基包下的类，得到信息A
- 第二，对于@Controller/@Service/@Repository注解而言，我们需要拿到对应的名称，并初始化它们修饰的类，形成映射关系B
- 第三，我们还得扫描类中的字段，如果发现有@Qualifier的话，我们需要完成注入
- 第四，我们还需要扫描@RequestMapping，完成URL到某一个Controller的某一个方法上的映射关系C
- 其实，Spring MVC的处理流程，就是类似这样的！
