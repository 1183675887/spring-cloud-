package com.javaboy.provider.controller;


import com.javaboy.provider.entity.User;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseBody;

/*
* 这是测试用户注册后重定向接口
* */
@Controller
public class RegisterController {

    @PostMapping("/register")
    public String register(User user) {
        return "redirect:http://provider/loginPage?username=" + user.getUsername();
    }

    @GetMapping("/loginPage")
    @ResponseBody
    public String loginPage(String username) {
        return "loginPage:" + username;
    }

}
