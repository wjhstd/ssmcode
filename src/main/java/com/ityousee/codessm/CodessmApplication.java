package com.ityousee.codessm;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.RequestMapping;

@SpringBootApplication
@RequestMapping("/")
public class CodessmApplication {

    public static void main(String[] args) {
        SpringApplication.run(CodessmApplication.class, args);
    }
    public String getMessage(){
        return "hello ityousee ggsmm";
    }
}
