package com.javaboy.hystrix.controller;

import com.javaboy.hystrix.config.HelloCommand;
import com.javaboy.hystrix.config.UserCollapseCommand;
import com.javaboy.hystrix.entity.User;
import com.javaboy.hystrix.service.HelloService;
import com.javaboy.hystrix.service.UserService;
import com.netflix.hystrix.HystrixCommand;
import com.netflix.hystrix.HystrixCommandGroupKey;
import com.netflix.hystrix.strategy.concurrency.HystrixRequestContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

@RestController
public class HelloController {

    @Autowired
    HelloService helloService;

    /*这是常规的测试方法*/
    @GetMapping("/hello")
    public String hello() {
       return helloService.hello();
    }

    @Autowired
    RestTemplate restTemplate;

    /*这是请求命令的测试方法*/
//    @GetMapping("/hello2")
//    public void hello2() {
//        HelloCommand helloCommand = new HelloCommand(HystrixCommand.Setter.withGroupKey(HystrixCommandGroupKey.Factory.asKey("javaboy")), restTemplate, "javaboy");
//        String execute = helloCommand.execute();//直接执行
//        System.out.println(execute);
//        HelloCommand helloCommand2 = new HelloCommand(HystrixCommand.Setter.withGroupKey(HystrixCommandGroupKey.Factory.asKey("javaboy")), restTemplate, "javaboy");
//        try {
//            Future<String> queue = helloCommand2.queue();
//            String s = queue.get();
//            System.out.println(s);//先入队，后执行
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        } catch (ExecutionException e) {
//            e.printStackTrace();
//        }
//    }

    /*测试定义缓存删除*/
    @GetMapping("/hello2")
    public void hello2() {
        HystrixRequestContext ctx = HystrixRequestContext.initializeContext();
        HelloCommand helloCommand = new HelloCommand(HystrixCommand.Setter.withGroupKey(HystrixCommandGroupKey.Factory.asKey("javaboy")), restTemplate,"javaboy");
        String execute = helloCommand.execute();//直接执行
        System.out.println(execute);
        HelloCommand helloCommand2 = new HelloCommand(HystrixCommand.Setter.withGroupKey(HystrixCommandGroupKey.Factory.asKey("javaboy")), restTemplate,"javaboy");
        try {
            Future<String> queue = helloCommand2.queue();
            String s = queue.get();
            System.out.println(s);//先入队，后执行
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
        ctx.close();
    }

    /*
    * 调用注解实现请求异步调用的方法
    * */
    @GetMapping("/hello3")
    public void hello3() {
        Future<String> hello2 = helloService.hello2();
        try {
            String s = hello2.get();
            System.out.println(s);
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
    }

//    /*测试请求缓存*/
//    @GetMapping("/hello4")
//    public void hello4() {
//        HystrixRequestContext ctx = HystrixRequestContext.initializeContext();
//        //第一请求完，数据已经缓存下来了.所以第二次调用时不会去使用provider的方法
//        String javaboy = helloService.hello3("javaboy");
//        javaboy = helloService.hello3("javaboy");
//        ctx.close();
//    }

    /*测试删除数据时删除缓存*/
    @GetMapping("/hello4")
    public void hello4() {
        HystrixRequestContext ctx = HystrixRequestContext.initializeContext();
        //第一请求完，数据已经缓存下来了
        String javaboy = helloService.hello3("javaboy");
        //删除数据，同时缓存中的数据也会被删除
       String a =  helloService.deleteUserByName("javaboy");
        System.out.println(a);
        //第二次请求时，虽然参数还是 javaboy，但是缓存数据已经没了，所以这一次，provider 还是会收到请求
        javaboy = helloService.hello3("javaboy");
        ctx.close();
    }


    @Autowired
    UserService userService;

    /*测试请求合并的方法.请求时需要配置两个或以上的提供者*/
    @GetMapping("/hello5")
    public void hello5() throws ExecutionException, InterruptedException {
        HystrixRequestContext ctx = HystrixRequestContext.initializeContext();
        UserCollapseCommand cmd1 = new UserCollapseCommand(userService, 99);
        UserCollapseCommand cmd2 = new UserCollapseCommand(userService, 98);
        UserCollapseCommand cmd3 = new UserCollapseCommand(userService, 97);
        Future<User> q1 = cmd1.queue();
        Future<User> q2 = cmd2.queue();
        Future<User> q3 = cmd3.queue();
        User u1 = q1.get();
        User u2 = q2.get();
        User u3 = q3.get();
        System.out.println(u1);
        System.out.println(u2);
        System.out.println(u3);
//        Thread.sleep(2000);
        UserCollapseCommand cmd4 = new UserCollapseCommand(userService, 96);
        Future<User> q4 = cmd4.queue();
        User u4 = q4.get();
        System.out.println(u4);
        ctx.close();
    }

    /*
    * 测试注解实现请求合并
    * */
    @GetMapping("/hello6")
    public void hello6() throws ExecutionException, InterruptedException {
        HystrixRequestContext ctx = HystrixRequestContext.initializeContext();
        Future<User> q1 = userService.getUserById(99);
        Future<User> q2 = userService.getUserById(98);
        Future<User> q3 = userService.getUserById(97);
        User u1 = q1.get();
        User u2 = q2.get();
        User u3 = q3.get();
        System.out.println(u1);
        System.out.println(u2);
        System.out.println(u3);
        Thread.sleep(2000);
        Future<User> q4 = userService.getUserById(96);
        User u4 = q4.get();
        System.out.println(u4);
        ctx.close();
    }

}
