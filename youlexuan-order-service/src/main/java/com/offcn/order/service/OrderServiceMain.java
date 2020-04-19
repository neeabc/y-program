package com.offcn.order.service;

import org.springframework.context.support.ClassPathXmlApplicationContext;

import java.io.IOException;

public class OrderServiceMain {
    public static void main(String[] args) {
        ClassPathXmlApplicationContext context =
                new ClassPathXmlApplicationContext("classpath*:spring/applicationContext-*.xml");
        context.start();
        try {
            System.in.read();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
