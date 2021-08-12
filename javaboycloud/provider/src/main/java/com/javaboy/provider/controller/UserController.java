package com.javaboy.provider.controller;

import com.javaboy.provider.entity.User;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

//这是测试请求合并的方法
@RestController
public class UserController {

    @GetMapping("/user/{ids}")//假设 consumer 传过来的多个 id 的格式是 1,2,3,4....
    public List<User> getUserByIds(@PathVariable String ids) {
        System.out.println(ids);
        String[] split = ids.split(",");
        List<User> users = new ArrayList<>();
        for (String s : split) {
            User u = new User();
            u.setId(Integer.parseInt(s));
            users.add(u);
        }
        return users;
    }
}
