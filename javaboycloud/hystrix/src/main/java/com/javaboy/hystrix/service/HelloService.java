package com.javaboy.hystrix.service;

import com.netflix.hystrix.contrib.javanica.annotation.HystrixCommand;
import com.netflix.hystrix.contrib.javanica.cache.annotation.CacheRemove;
import com.netflix.hystrix.contrib.javanica.cache.annotation.CacheResult;
import com.netflix.hystrix.contrib.javanica.command.AsyncResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.concurrent.Future;

@Service
public class HelloService {

    @Autowired
    RestTemplate restTemplate;

    /**
     * 在这个方法中，我们将发起一个远程调用，去调用 provider 中提供的 /hello 接口
     * 但是，这个调用可能会失败。1.provider连接不到。2.自己代码出现问题
     * 我们在这个方法上添加 @HystrixCommand 注解，配置 fallbackMethod 属性，这个属性表示该方法调用失败时的临时替代方法
     */
    @HystrixCommand(fallbackMethod = "error")
    public String hello() {
        int i = 1 / 0;       //这步就是自己的代码错误，用来测试情况2
         return restTemplate.getForObject("http://provider/hello", String.class);
    }

//    /*这个方法中ignoreExceptions注解就是直接抛异常，而不做降级处理，了解即可*/
//    @HystrixCommand(fallbackMethod = "error", ignoreExceptions = ArithmeticException.class)
//    public String hello() {
//        int i = 1 / 0;       //这步就是自己的代码错误，用来测试情况2
//        return restTemplate.getForObject("http://provider/hello", String.class);
//    }

    /*测试请求缓存*/
    @HystrixCommand(fallbackMethod = "error2")
    @CacheResult//这个注解表示该方法的请求结果会被缓存起来，默认情况下，缓存的 key 就是方法的参数，缓存的 value 就是方法的返回值。
    public String hello3(String name) {
        return restTemplate.getForObject("http://provider/hello2?name={1}", String.class, name);
    }

    /*定义删除数据时也删除缓存的方法.测试时缓存未删除。*/
    @HystrixCommand
    @CacheRemove(commandKey = "hello3")
    public String deleteUserByName(String name) {
        return "缓存已删除";
    }

    public String error2(String name) {
        return "error:javaboy";
    }


    /*
    * 使用注解实现请求异步调用。
    * */
    @HystrixCommand(fallbackMethod = "error")
    public Future<String> hello2() {
        return new AsyncResult<String>() {
            @Override
            public String invoke() {
                return restTemplate.getForObject("http://provider/hello", String.class);
            }
        };
    }

    /**
     * 注意，这个方法名字要和 fallbackMethod 一致
     * 方法返回值也要和对应的方法一致
     */
    public String error(Throwable t) {

        return "error：" + t.getMessage();
    }

}
