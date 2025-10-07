package com.erywim.service.impl;

import com.erywim.service.IpCounterService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class IpCounterServiceImpl implements IpCounterService {

    @Autowired
    private HttpServletRequest request;
    private final Map<String ,Integer> ipMap = new ConcurrentHashMap<>();

    @Override
    public void record() {
        //记录ip
        String ip = request.getRemoteAddr();
        //记录访问次数
        Integer ipCount = ipMap.getOrDefault(ip, 0);
        //更新
        ipMap.put(ip, ipCount + 1);
        //输出
        ipMap.forEach((k, v) -> {
            //打印规范的详细日志
            System.out.println("ip是：" + k + "访问了：" + v + "次");

        });
    }
}
