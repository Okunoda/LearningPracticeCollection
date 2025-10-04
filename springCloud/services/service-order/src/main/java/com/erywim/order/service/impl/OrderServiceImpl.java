package com.erywim.order.service.impl;

import com.alibaba.nacos.shaded.com.google.common.collect.Lists;
import com.erywim.order.bean.Order;
import com.erywim.order.service.OrderService;
import com.erywim.product.bean.Product;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.cloud.client.loadbalancer.LoadBalancerClient;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;

@Service
@Slf4j
public class OrderServiceImpl implements OrderService {

    @Autowired
    private DiscoveryClient discoveryClient;

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private LoadBalancerClient loadBalancerClient;

    @Override
    public Order createOrder(Long id, Long userId) {
        Order order = new Order();
//        Product product = getProductFromRemote(id);
//        Product product = getProductFromRemoteWithLoadBalance(id);
        Product product = getProductFromRemoteWithLoadBalanceAnnotation(id);

        order.setId(1L);
        order.setUserId(userId);
        order.setNickName("user-" + userId);
        order.setAddress("address-" + userId);
        order.setTotalAmount(product.getPrice().multiply(new BigDecimal(product.getNum())));

        order.setProducts(Lists.newArrayList(product));
        return order;
    }

    //v2-使用 loadBalance 负载均衡 注解获取地址。注解加在restTemplate 的 Bean 上
    private Product getProductFromRemoteWithLoadBalanceAnnotation(Long id) {
        String url = "http://service-product/getProduct/" + id;
        ResponseEntity<Product> response = restTemplate.getForEntity(url, Product.class);

        Product body = response.getBody();
        return body;
    }


    //v2-使用 loadBalance 负载均衡获取地址
    private Product getProductFromRemoteWithLoadBalance(Long id) {
        ServiceInstance instance = loadBalancerClient.choose("service-product");
        String url = "http://" + instance.getHost() + ":" + instance.getPort() + "/getProduct/" + id;
        ResponseEntity<Product> response = restTemplate.getForEntity(url, Product.class);
        log.info("远程调用url:{}", url);

        Product body = response.getBody();
        return body;
    }


    //v1-使用 nacos 获取远程调用地址
    private Product getProductFromRemote(Long id) {
        ServiceInstance instance = discoveryClient.getInstances("service-product").get(0);

        String url = "http://" + instance.getHost() + ":" + instance.getPort() + "/getProduct/" + id;

        ResponseEntity<Product> response = restTemplate.getForEntity(url, Product.class);

        Product body = response.getBody();
        return body;
    }
}
