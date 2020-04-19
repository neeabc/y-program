package com.offcn.seckill.service;

import org.springframework.context.support.ClassPathXmlApplicationContext;

import java.io.IOException;

public class SearchServiceMain {
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
