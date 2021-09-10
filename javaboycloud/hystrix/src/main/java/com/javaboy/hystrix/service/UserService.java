package com.javaboy.hystrix.service;

import com.javaboy.hystrix.entity.User;
import com.netflix.hystrix.contrib.javanica.annotation.HystrixCollapser;
import com.netflix.hystrix.contrib.javanica.annotation.HystrixCommand;
import com.netflix.hystrix.contrib.javanica.annotation.HystrixProperty;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.Future;

//测试请求合并
@Service
public class UserService {
    @Autowired
    RestTemplate restTemplate;

//      /*常规的请求合并*/
//    public List<User> getUsersByIds(List<Integer> ids) {
//        User[] users = restTemplate.getForObject("http://provider/user/{1}", User[].class, StringUtils.join(ids, ","));
//        return Arrays.asList(users);
//    }


    /*使用注解方式来请求合并*/
    @HystrixCollapser(batchMethod = "getUsersByIds",collapserProperties = {@HystrixProperty(name = "timerDelayInMilliseconds",value = "200")})
    public Future<User> getUserById(Integer id) {
        return null;
    }

    @HystrixCommand
    public List<User> getUsersByIds(List<Integer> ids) {
        User[] users = restTemplate.getForObject("http://provider/user/{1}", User[].class, StringUtils.join(ids, ","));
        return Arrays.asList(users);
    }


}
