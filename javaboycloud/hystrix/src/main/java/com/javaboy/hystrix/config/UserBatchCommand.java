package com.javaboy.hystrix.config;

//类似于HelloCommand
import com.javaboy.hystrix.service.UserService;
import com.netflix.hystrix.HystrixCommand;
import com.netflix.hystrix.HystrixCommandGroupKey;
import com.netflix.hystrix.HystrixCommandKey;

import com.javaboy.hystrix.entity.User;

import java.util.List;

public class UserBatchCommand extends HystrixCommand<List<User>> {

    private List<Integer> ids;

    private UserService userService;

    public UserBatchCommand(List<Integer> ids, UserService userService) {
        super(HystrixCommand.Setter.withGroupKey(HystrixCommandGroupKey.Factory.asKey("batchCmd")).andCommandKey(HystrixCommandKey.Factory.asKey("batchKey")));
        this.ids = ids;
        this.userService = userService;
    }

    @Override
    protected List<User> run() throws Exception {
        return userService.getUsersByIds(ids);
    }

    /*
     * 实现服务容错/降级,重写此方法
     *这个方法就是请求失败的回调
     *  */
    @Override
    protected List<User> getFallback() {
        return null;
    }

    /*这个是删除缓存*/
    @Override
    protected String getCacheKey() {
        return null;
    }

}
