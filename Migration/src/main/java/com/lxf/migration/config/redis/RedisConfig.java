package com.lxf.migration.config.redis;


import jdk.jfr.Name;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.RedisPassword;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

@Configuration
public class RedisConfig {
    @Autowired
    private org.springframework.core.env.Environment env;

    private Integer port;
    private String host;
    private String username;
    private String password;

    @Bean
    public RedisTemplate<String, String> redisTemplate(@Qualifier("redisConnectionFactory") RedisConnectionFactory redisConnectionFactory) {
        RedisTemplate<String, String> template = new RedisTemplate<>();
        template.setConnectionFactory(redisConnectionFactory);

        RedisSerializer<String> stringSerializer = new StringRedisSerializer();
        template.setKeySerializer(stringSerializer);
        template.setValueSerializer(stringSerializer);
        template.setHashKeySerializer(stringSerializer);
        template.setHashValueSerializer(stringSerializer);

        return template;
    }


    @Bean
    @Name(value = "redisConnectionFactory")
    @Lazy
    public LettuceConnectionFactory redisConnectionFactory() {

        host = env.getProperty("spring.data.redis.host");

        String redisPortString = env.getProperty("spring.data.redis.port");

        try {
            port = Integer.parseInt(redisPortString);
        } catch (NumberFormatException e) {
            e.printStackTrace();
        }
        password = env.getProperty("spring.data.redis.password");

        username = env.getProperty("spring.data.redis.username");


        RedisStandaloneConfiguration redisConfig = new RedisStandaloneConfiguration(host, port);

        if (password != null && !password.isEmpty()) {
            redisConfig.setPassword(password);
        }

        if (username != null && !username.isEmpty()) {
            redisConfig.setUsername(username);
        }


        return new LettuceConnectionFactory(redisConfig);
    }


}