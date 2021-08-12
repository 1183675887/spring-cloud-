package com.javaboy.hystrix.config;

import com.netflix.hystrix.HystrixCommand;
import org.springframework.web.client.RestTemplate;

//请求命令就是以继承类的方式来替代前面的注解方式
public class HelloCommand extends HystrixCommand<String> {

    RestTemplate restTemplate;

    String name;

    public HelloCommand(Setter setter, RestTemplate restTemplate, String name) {
        super(setter);
        this.name = name;
        this.restTemplate = restTemplate;
    }

    /*这是运行的方法*/
//    @Override
//    protected String run() throws Exception {
//        int i = 1 / 0;       //这是使用继承方法测试情况2.自己代码出现问题
//        return restTemplate.getForObject("http://provider/hello", String.class);
//    }

    /*
    * 测试方法
    * */
    @Override
    protected String run() throws Exception {
        return restTemplate.getForObject("http://provider/hello2?name={1}", String.class, name);
    }

    /*
    * 实现服务容错/降级,重写此方法
    *这个方法就是请求失败的回调
    *  */
    @Override
    protected String getFallback() {
        return "error-extends:" +getExecutionException().getMessage();
    }

    /*这个是删除缓存*/
    @Override
    protected String getCacheKey() {
        return name;
    }

}
