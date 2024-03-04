package com.fin.payment.global.config;

import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RedissionConfig {

	@Value(value = "${spring.redis.host}")
	private String redisHost;

	@Value(value = "${spring.redis.port}")
	private int redisPort;

	@Value(value = "${spring.redis.password}")
	private String redisPassword;

	private static final String REDISSON_HOST_PREFIX = "redis://";

	@Bean
	public RedissonClient redissonClient() {
		RedissonClient redisson = null;
		Config config = new Config();
		config.useSingleServer().setAddress(REDISSON_HOST_PREFIX + redisHost + ":" + redisPort);
		redisson = Redisson.create(config);
		return redisson;
	}
}
