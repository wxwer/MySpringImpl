手写简易版仿Springboot框架，实现了IOC，AOP，MVC，Mybatis等模块，此外还实现事务管理，能够连接外置Jedis,Kafka等客户端，内嵌了tomcat服务器和Freemarker解析器，可进行简单的增删改查和实现动态页面。所有注解基本和Springboot的一毛一样，不需要进行XML配置，简单易用，代码简单结构清晰，有比较详细的代码注释。  
  
1.实现了IOC容器和依赖注入，用单例模式实现Bean工厂类，通过反射自动进行Service,Component,Controller类的自动生成和注入，通过三级缓存解决了循环依赖问题，有多种注解方式进行注入，@Autowired，@Qualifier，@Resource的语义和用法与Spring的一样，此外还实现了@Configuration中的配置的Bean的自动注入，通过手动配置能够连接外部Jedis,Kafka等客服端。  
  
2.通过CGLib动态代理实现了AOP（面向切面编程），使用注解来配置切面，有@Before，@After，@Around，@Afterthrowing，@AfterReturning等多种通知，可通过@Pointcut配置切点。能实现对同一个方法的多次增强，并通过通知的order变量配置增强的顺序。通过动态代理还实现了Spring的事务管理，在类或者方法上加上@Transactional即可获得事务管理功能，可配置事务的四种隔离级别、回滚异常类和六种传播行为(Propagation.NESTED仍存在问题)，使用ThreadLocal<Connection>缓存连接和ThreadLocal<Stack<Connection>>缓存上级事务连接来实现事务的传播行为，并保证事务中的操作获取到的是同一个连接，可进行事务提交，出错时自动进行事务回滚。  
      
3.MVC模块实现了DispatcherServlet解析和处理请求的主要流程，能够从HttpServletRequest对@RequestParam，@PathVariable，@RequestBody等注解的方法参数进行解析，能够实现HandlerMapping和HanderAdapter的初始化，请求到来时将请求映射为hander后，通过handerAdapter来实际处理请求，handerAdapter对请求处理完成之后根据实际返回的类型来进行解析，并返回给用户；如果有@ResponseBody则解析为Json字符串返回，如果返回一个ModelAndView对象，则使用内置的Freemarker解析器进行解析，解析渲染之后将html页面返回给用户。  
  
4.实现了类似于Mybatis的功能，通过在@Mapper声明的接口上使用注解@Select，@Update，@Insert，@Delete来方法上声明要执行的sql语句，就可以通过动态代理来生成实际的代理对象，通过调用Executor来执行实际的Sql语句，生成的代理对象通过IOC容器也可以自动注入到其他对象的属性中。Sql语句的参数的注入使用了#{ }和${ }两种方式，语义和Mybatis的一样。通过JDBC来进行低层的数据库连接，通过阻塞队列实现了内置的数据库连接池，可配置是否使用。实现了查询结果集ResultSet到实际对象或对象列表的映射，能自动进行基本数据类型和包装类型属性的注入。通过TransactionManager来进行连接的获取的事务的实现，可进行事务隔离级别的配置，与AOP中的代理工厂配合使用实现事务管理。


  
包的说明：  
com.wang.spring包下实现了Spring的IOC,AOP,MVC的功能模块；    
com.wang.mybatis包下实现了仿Mybatis的功能模块，可通过注解进行数据库增删改查，完成对象关系映射；    
com.wang.demo包下是一个简单的通过mysql和redis实现的注册登录例子。  

例子运行方法：  
1.修改resources下的application.properties配置文件，修改数据库的url，账号，密码的配置，修改com.wang.demo.configuration包下的Jedis Bean的手动配置。  
2.在数据库中生成表，字段名和类型见User类，字段名一定要相等。(后续会增加别名功能）  
3.启动主类Application.java，使用postman进行接口测试。  


