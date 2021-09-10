package com.javaboy.openfeign.controller;

import com.javaboy.openfeign.entity.User;
import com.javaboy.openfeign.service.HelloService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

@RestController
public class HelloController {

    @Autowired
    HelloService helloService;

      /*正常测试方法*/
    @GetMapping("/hello")
    public String hello() {
        return helloService.hello();
    }

    /*测试其他方法,需要至少启动两个或以前的provider*/
//    @GetMapping("/hello")
//    public String hello() throws UnsupportedEncodingException {
//        String s = helloService.hello2("测试");
//        System.out.println(s);
//        User user = new User();
//        user.setId(1);
//        user.setUsername("javaboy");
//        user.setPassword("123");
//        User u = helloService.addUser(user);
//        System.out.println(u);
//        helloService.deleteUserById(1);
//        helloService.getUserByName(URLEncoder.encode("测试", "UTF-8"));
//        return helloService.hello();
//    }

}
