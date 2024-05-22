package com.nhung.shoptn.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.nio.file.Path;
import java.nio.file.Paths;

@Configuration
public class MvcConfig implements WebMvcConfigurer {
    @Override
    public void addViewControllers(ViewControllerRegistry registry) {
//        WebMvcConfigurer.super.addViewControllers(registry);
//        registry.addViewController("/admin").setViewName("admin/index");
    }

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        Path imageUploadDir = Paths.get("./src/main/resources/static/images");
        String imageUploadPath = imageUploadDir.toFile().getAbsolutePath();
        registry.addResourceHandler("/images/**").addResourceLocations("file:/"+ imageUploadPath + "/");
    }

}
