package com.aug70.security.sample.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import redis.clients.jedis.JedisPoolConfig;

@Configuration
public class AppConfig {

	@Bean
 	public PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
 	}
	
	@Bean
    public RedisConnectionFactory redisConnectionFactory() {
        JedisConnectionFactory jedisConnectionFactory = new JedisConnectionFactory();
        jedisConnectionFactory.setHostName("localhost");
        jedisConnectionFactory.setPort(6379);
        jedisConnectionFactory.setPassword("");
        jedisConnectionFactory.setUsePool(true);
        jedisConnectionFactory.setPoolConfig(new JedisPoolConfig());
        return jedisConnectionFactory;
    }

}
