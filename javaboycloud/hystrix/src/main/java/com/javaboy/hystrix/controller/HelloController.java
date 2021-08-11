package com.javaboy.hystrix.controller;

import com.javaboy.hystrix.config.HelloCommand;
import com.javaboy.hystrix.service.HelloService;
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

    @Autowired
    RestTemplate restTemplate;

    /*这是常规的测试方法*/
    @GetMapping("/hello")
    public String hello() {
       return helloService.hello();
    }

    /*这是请求命令的测试方法*/
    @GetMapping("/hello2")
    public void hello2() {
        HelloCommand helloCommand = new HelloCommand(HystrixCommand.Setter.withGroupKey(HystrixCommandGroupKey.Factory.asKey("javaboy")), restTemplate);
        String execute = helloCommand.execute();//直接执行
        System.out.println(execute);
        HelloCommand helloCommand2 = new HelloCommand(HystrixCommand.Setter.withGroupKey(HystrixCommandGroupKey.Factory.asKey("javaboy")), restTemplate);
        try {
            Future<String> queue = helloCommand2.queue();
            String s = queue.get();
            System.out.println(s);//先入队，后执行
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
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

    /*测试请求缓存*/
    @GetMapping("/hello4")
    public void hello4() {
        HystrixRequestContext ctx = HystrixRequestContext.initializeContext();
        //第一请求完，数据已经缓存下来了
        String javaboy = helloService.hello3("javaboy");
        javaboy = helloService.hello3("javaboy");
        ctx.close();
    }

}
