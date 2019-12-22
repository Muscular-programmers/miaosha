package pers.jun;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.session.data.redis.config.annotation.web.http.EnableRedisHttpSession;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import springfox.documentation.swagger2.annotations.EnableSwagger2;


/**
 * Hello world!
 */

@SpringBootApplication(exclude = {SecurityAutoConfiguration.class})
@EnableSwagger2

public class App {
    public static void main(String[] args) {
        SpringApplication.run(App.class,args);
    }
}
