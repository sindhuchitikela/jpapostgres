package com.pg.demo.category;

import com.pg.demo.category.config.CategoryBeanConfig;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Import;


@SpringBootApplication
@ComponentScan(
    basePackages = {"com.pg"}
)
@Import({CategoryBeanConfig.class})
public class CategoryApplication {
  public static void main(String[] args) {
    SpringApplication.run(CategoryApplication.class, args);
  }
}
