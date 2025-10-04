package com.erywim.order.service.impl;

import com.alibaba.nacos.shaded.com.google.common.collect.Lists;
import com.erywim.order.bean.Order;
import com.erywim.order.service.OrderService;
import com.erywim.product.bean.Product;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;

@Service
public class OrderServiceImpl implements OrderService {

    @Autowired
    private DiscoveryClient discoveryClient;

    @Autowired
    private RestTemplate restTemplate;

    @Override
    public Order createOrder(Long id, Long userId) {
        Order order = new Order();


        Product product = getProductFromRemote(id);

        order.setId(1L);
        order.setUserId(userId);
        order.setNickName("user-" + userId);
        order.setAddress("address-" + userId);
        order.setTotalAmount(product.getPrice().multiply(new BigDecimal(product.getNum())));

        order.setProducts(Lists.newArrayList(product));
        return order;
    }


    private Product getProductFromRemote(Long id){
        ServiceInstance instance = discoveryClient.getInstances("service-product").get(0);

        String url = "http://" + instance.getHost() + ":" + instance.getPort() + "/getProduct/" + id;

        ResponseEntity<Product> response = restTemplate.getForEntity(url, Product.class );

        Product body = response.getBody();
        return body;


    }
}
