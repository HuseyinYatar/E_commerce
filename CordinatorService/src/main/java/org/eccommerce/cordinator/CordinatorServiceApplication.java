package org.eccommerce.cordinator;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableScheduling
@SpringBootApplication
public class CordinatorServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(CordinatorServiceApplication.class, args);
    }

}
