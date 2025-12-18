package com.example.camunda;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import io.camunda.zeebe.spring.client.annotation.Deployment;

@SpringBootApplication

@Deployment(resources = "classpath:fetch_picture.bpmn")
public class CamundaClientApplication {

    public static void main(String[] args) {
        SpringApplication.run(CamundaClientApplication.class, args);
    }
}
