package org.nikolait.crmsystem;

import org.springframework.boot.SpringApplication;

public class TestCrmSystemApplication {

    public static void main(String[] args) {
        SpringApplication.from(CrmSystemApplication::main).with(TestcontainersConfiguration.class).run(args);
    }

}
