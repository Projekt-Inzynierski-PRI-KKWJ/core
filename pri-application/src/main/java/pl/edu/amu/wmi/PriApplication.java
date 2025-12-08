package pl.edu.amu.wmi;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan(basePackages = {"pl.edu.amu.wmi", "pl.edu.amu.projectmarket"})
public class PriApplication {

    public static void main(String[] args) {
        SpringApplication.run(PriApplication.class, args);
    }

}