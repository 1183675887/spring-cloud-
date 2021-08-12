package com.javaboy.openfeign.service;

import com.javaboy.openfeign.config.HelloServiceFallbackFactory;
import com.javaboy.openfeign.entity.User;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

import java.io.UnsupportedEncodingException;

@FeignClient(value = "provider",fallbackFactory = HelloServiceFallbackFactory.class)
public interface HelloService {

    /*这是直接调用了provider中的hello的get方法*/
    @GetMapping("/hello")
    String hello();    //这里的方法名无所谓，随意取

    /*凡是 key/value 形式的参数，一定要标记参数的名称*/
    @GetMapping("/hello2")
    String hello2(@RequestParam("name") String name);

    @PostMapping("/user2")
    User addUser(@RequestBody User user);

    @DeleteMapping("/user2/{id}")
    void deleteUserById(@PathVariable("id") Integer id);

    @GetMapping("/user3")
    void getUserByName(@RequestHeader("name") String name) throws UnsupportedEncodingException;


}
