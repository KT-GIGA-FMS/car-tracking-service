package com.kt_giga_fms.car_tracking.config;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.kt_giga_fms.car_tracking.dto.CarTrackingResponse;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

@Configuration
public class RedisConfig {

	@Bean
	public RedisTemplate<String, CarTrackingResponse> carTrackingRedisTemplate(RedisConnectionFactory connectionFactory) {
		RedisTemplate<String, CarTrackingResponse> template = new RedisTemplate<>();
		template.setConnectionFactory(connectionFactory);

		ObjectMapper objectMapper = new ObjectMapper();
		objectMapper.registerModule(new JavaTimeModule());
		objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
		objectMapper.setVisibility(PropertyAccessor.ALL, JsonAutoDetect.Visibility.ANY);

		GenericJackson2JsonRedisSerializer valueSerializer = new GenericJackson2JsonRedisSerializer(objectMapper);

		template.setKeySerializer(new StringRedisSerializer());
		template.setValueSerializer(valueSerializer);
		template.setHashKeySerializer(new StringRedisSerializer());
		template.setHashValueSerializer(valueSerializer);
		template.afterPropertiesSet();
		return template;
	}
}
