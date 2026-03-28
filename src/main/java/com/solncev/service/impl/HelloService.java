package com.solncev.service.impl;

import org.springframework.stereotype.Service;

@Service
public class HelloService {

    public String sayHello(String name) {
        return "Hello, %s".formatted(name);
    }
}
