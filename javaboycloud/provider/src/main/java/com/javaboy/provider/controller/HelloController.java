package com.javaboy.provider.controller;


import com.javaboy.provider.entity.User;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.Date;

@RestController
public class HelloController {

    @Value("${server.port}")
    Integer port;

    //这是测试消费者与提供者
    @GetMapping("/hello")
    public String hello() {
        return "hello javaboy:" + port;
    }

    /*
    *测试RestTemplate的get方法
    * */
    @GetMapping("/hello2")
    public String hello2(String name) {
        System.out.println(new Date() + ">>>" + name);      //这步打印是验证缓存问题。
        return "hello " + name;
    }

    /*
    * 测试RestTemplate的post方法
    * */
    @PostMapping("/user1")
    public User addUser1(User user) {
        return user;
    }

    @PostMapping("/user2")
    public User addUser2(@RequestBody User user) {
        return user;
    }

    /*
    * 测试RestTemplate的put方法
    * */
    @PutMapping("/user1")
    public void updateUser1(User user) {
        System.out.println(user);
    }

    @PutMapping("/user2")
    public void updateUser2(@RequestBody User user) {
        System.out.println(user);
    }

    /*
     * 测试RestTemplate的delete方法
     * */
    @DeleteMapping("/user1")
    public void deleteUser1(Integer id) {
       System.out.println(id);
    }

    @DeleteMapping("/user2/{id}")
    public void deleteUser2(@PathVariable Integer id) {
       System.out.println(id);
    }

    /*测试heander，注意要转码*/
    @GetMapping("/user3")
    public void getUserByName(@RequestHeader String name) throws UnsupportedEncodingException {
        System.out.println(URLDecoder.decode(name, "UTF-8"));
    }

}
