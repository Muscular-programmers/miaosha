package com.jun;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;


/**
 * Hello world!
 */

@SpringBootApplication
@ComponentScan(basePackages = {"com.jun"})
public class App {
    public static void main(String[] args) {

        //System.out.println("Hello World!");
        SpringApplication.run(App.class,args);
    }
}
