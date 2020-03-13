package com.chapter6.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.*;


@Configuration

public class McvConfig extends WebMvcConfigurerAdapter implements WebMvcConfigurer{
////视图跳转
//    @Override
//    public void addViewControllers(ViewControllerRegistry registry) {
//        registry.addViewController("/").setViewName("login");
//    }


    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/**").addResourceLocations("classpath:/");
        super.addResourceHandlers(registry);
    }

    @Override
      public void addCorsMappings(CorsRegistry registry) {
                 registry.addMapping("/**")
                         .allowedOrigins("*")
                         .allowedMethods("GET", "HEAD", "POST", "PUT", "DELETE", "OPTIONS")
                        .allowCredentials(true)
                        .maxAge(3600)
                        .allowedHeaders("*");
          }
}

