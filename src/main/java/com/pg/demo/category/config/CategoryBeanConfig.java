package com.pg.demo.category.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.client.RestTemplate;


@Configuration
@PropertySource(value = "file:${secret.config.location}", ignoreResourceNotFound = true)
public class CategoryBeanConfig {
  @Bean
  public RestTemplate restTemplate() {
    RestTemplate rest = new RestTemplate();
    rest.getMessageConverters().add(new MappingJackson2HttpMessageConverter());
    return rest;
  }
}
