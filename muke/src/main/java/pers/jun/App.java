package pers.jun;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import springfox.documentation.swagger2.annotations.EnableSwagger2;


/**
 * Hello world!
 */

@SpringBootApplication
@ComponentScan(basePackages = {"pers.jun"})
@EnableSwagger2
//@EnableWebMvc
public class App {
    public static void main(String[] args) {

        //System.out.println("Hello World!");
        SpringApplication.run(App.class,args);
    }
}
