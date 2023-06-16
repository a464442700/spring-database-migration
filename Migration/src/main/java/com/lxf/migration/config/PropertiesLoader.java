package com.lxf.migration.config;
import org.springframework.core.io.DefaultResourceLoader;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Component;
import java.io.IOException;
import java.util.Properties;


public class PropertiesLoader {
    public Properties getProperties() throws IOException {
        ResourceLoader resourceLoader = new DefaultResourceLoader();
        Resource resource = resourceLoader.getResource("file:./myApplication.yml");
        Properties properties = new Properties();
        properties.load(resource.getInputStream());
        System.out.println("properties:"+properties);
        return properties;
    }
}