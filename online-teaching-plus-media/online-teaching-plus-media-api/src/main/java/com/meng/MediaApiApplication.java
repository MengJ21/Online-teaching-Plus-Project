package com.meng;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan("com.meng.media.mapper")
public class MediaApiApplication {

    public static void main(String[] args) {
        SpringApplication.run(MediaApiApplication.class, args);
    }

}
