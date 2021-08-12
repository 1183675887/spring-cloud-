package com.javaboy.consumer.controller;

import com.javaboy.consumer.entity.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

@RestController
public class UserHelloController {

    //这是利用 HttpUrlConnection 来发起的请求，直接写死port
    @GetMapping("/hello1")
    public String hello1() {

        HttpURLConnection con = null;
        try {
            URL url = new URL("http://localhost:1113/hello");
            con = (HttpURLConnection) url.openConnection();
            if (con.getResponseCode() == 200) {
                BufferedReader br = new BufferedReader(new InputStreamReader(con.getInputStream()));
                String s = br.readLine();
                br.close();
                return s;
            }
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return "error";
    }


    /*
     * 以下是利用Eureka Client 提供的 DiscoveryClient 工具
     * */

    @Autowired
    DiscoveryClient discoveryClient;

    /*
    *这是使用http中的HttpUrlConnection调用
    @GetMapping("/hello2")
    public String hello2() {
        List<ServiceInstance> list = discoveryClient.getInstances("provider");
        ServiceInstance instance = list.get(0);
        String host = instance.getHost();
        int port = instance.getPort();
        StringBuffer sb = new StringBuffer();
        sb.append("http://")
                .append(host)
                .append(":")
                .append(port)
                .append("/hello");
        HttpURLConnection con = null;
        try {
            URL url = new URL(sb.toString());
            con = (HttpURLConnection) url.openConnection();
            if (con.getResponseCode() == 200) {
                BufferedReader br = new BufferedReader(new InputStreamReader(con.getInputStream()));
                String s = br.readLine();
                br.close();
                return s;
            }
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "error";

    }
*/


     /*
     * 以下是使用RestTemplate
     * */

    @Autowired
    @Qualifier("restTemplateOne")
    RestTemplate restTemplateOne;

    @GetMapping("/hello2")
    public String hello2() {
        List<ServiceInstance> list = discoveryClient.getInstances("provider");
        ServiceInstance instance = list.get(0);
        String host = instance.getHost();
        int port = instance.getPort();
        StringBuffer sb = new StringBuffer();
        sb.append("http://")
                .append(host)
                .append(":")
                .append(port)
                .append("/hello");
        String s = restTemplateOne.getForObject(sb.toString(), String.class);
        return s;
    }








    /*
    * 手动实现负载均衡
    int count = 0;
    @GetMapping("/hello3")
    public String hello3() {
        List<ServiceInstance> list = discoveryClient.getInstances("provider");
        ServiceInstance instance = list.get((count++) % list.size());
        String host = instance.getHost();
        int port = instance.getPort();
        StringBuffer sb = new StringBuffer();
        sb.append("http://")
                .append(host)
                .append(":")
                .append(port)
                .append("/hello");
        HttpURLConnection con = null;
        try {
            URL url = new URL(sb.toString());
            con = (HttpURLConnection) url.openConnection();
            if (con.getResponseCode() == 200) {
                BufferedReader br = new BufferedReader(new InputStreamReader(con.getInputStream()));
                String s = br.readLine();
                br.close();
                return s;
            }
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "error";
    }
   * */


     /*
     * 使用Ribbon实现负载均衡
     * */
     @Autowired
     @Qualifier("restTemplate")
     RestTemplate restTemplate;

    @GetMapping("/hello3")
    public String hello3() {
       return restTemplate.getForObject("http://provider/hello", String.class);
    }

    /*
    * 这是测试RestTemplate的get方法
    * */
    @GetMapping("/hello4")
    public void hello4() {
        String s1 = restTemplate.getForObject("http://provider/hello2?name={1}", String.class, "javaboy");
        System.out.println(s1);
        ResponseEntity<String> responseEntity = restTemplate.getForEntity("http://provider/hello2?name={1}", String.class, "javaboy");
        String body = responseEntity.getBody();
        System.out.println("body:" + body);
        HttpStatus statusCode = responseEntity.getStatusCode();
        System.out.println("HttpStatus:" + statusCode);
        int statusCodeValue = responseEntity.getStatusCodeValue();
        System.out.println("statusCodeValue:" + statusCodeValue);
        HttpHeaders headers = responseEntity.getHeaders();
        Set<String> keySet = headers.keySet();
        System.out.println("--------------header-----------");
        for (String s : keySet) {
            System.out.println(s + ":" + headers.get(s));
        }
    }

    //get方法不同的传参方式
    @GetMapping("/hello5")
    public void hello5() throws UnsupportedEncodingException {
        String s1 = restTemplate.getForObject("http://provider/hello2?name={1}", String.class, "javaboy");
        System.out.println(s1);
        Map<String, Object> map = new HashMap<>();
        map.put("name", "zhangsan");
        s1 = restTemplate.getForObject("http://provider/hello2?name={name}", String.class, map);
        System.out.println(s1);
        String url = "http://provider/hello2?name=" + URLEncoder.encode("张三", "UTF-8");
        URI uri = URI.create(url);
        s1 = restTemplate.getForObject(uri, String.class);
        System.out.println(s1);
    }


    /*
    * 这是测试RestTemplate的post方法
    * */
    @GetMapping("/hello6")
    public void hello6() {
        MultiValueMap<String, Object> map = new LinkedMultiValueMap<>();
        map.add("username", "javaboy");
        map.add("password", "123");
        map.add("id", 99);
        User user = restTemplate.postForObject("http://provider/user1", map, User.class);
        System.out.println(user);

        user.setId(98);
        user = restTemplate.postForObject("http://provider/user2", user, User.class);
        System.out.println(user);
    }

      //这是测试用户注册接口重定向
    @GetMapping("/hello7")
    public void hello7() {
        MultiValueMap<String, Object> map = new LinkedMultiValueMap<>();
        map.add("username", "javaboy");
        map.add("password", "123");
        map.add("id", 99);
        URI uri = restTemplate.postForLocation("http://provider/register", map);
        System.out.println(uri);
        String s = restTemplate.getForObject(uri, String.class);
        System.out.println(s);
    }


    /*
    *测试RestTemplate的put方法
    *  */
    @GetMapping("/hello8")
    public void hello8() {
        MultiValueMap<String, Object> map = new LinkedMultiValueMap<>();
        map.add("username", "javaboy");
        map.add("password", "123");
        map.add("id", 99);
        restTemplate.put("http://provider/user1", map);
        User user = new User();
        user.setId(98);
        user.setUsername("zhangsan");
        user.setPassword("456");
        restTemplate.put("http://provider/user2", user);
    }

    /*
     *测试RestTemplate的delete方法
     *  */
    @GetMapping("/hello9")
    public void hello9() {
        restTemplate.delete("http://provider/user1?id={1}", 99);
        restTemplate.delete("http://provider/user2/{1}", 99);
    }

}
