package io.anjola.recommendationservice.config;

import io.anjola.recommendationservice.persistence.RecommendationEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.ReactiveRedisOperations;
import org.springframework.data.redis.core.ReactiveRedisTemplate;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.GenericToStringSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.data.redis.serializer.StringRedisSerializer;

@Configuration
@EnableCaching
public class RedisConfiguration {

    private static final Logger LOG = LoggerFactory.getLogger(RedisConfiguration.class);

    @Value("${spring.redis.host}")
    private String redisHost;

    @Value("${spring.redis.port}")
    private int redisPort;

    @Value("${spring.redis.password}")
    private String redisPassword;

    @Bean
    public LettuceConnectionFactory lettuceConnectionFactory(){
        RedisStandaloneConfiguration redisStandaloneConfiguration = new RedisStandaloneConfiguration();
        redisStandaloneConfiguration.setHostName(redisHost);
        redisStandaloneConfiguration.setPort(redisPort);
        redisStandaloneConfiguration.setPassword(redisPassword);

        return new LettuceConnectionFactory((redisStandaloneConfiguration));
    }

    @Bean
    public ReactiveRedisOperations<String, RecommendationEntity> reactiveRedisOperations(LettuceConnectionFactory lettuceConnectionFactory){
        RedisSerializationContext<String, RecommendationEntity> serializationContext = RedisSerializationContext
                .<String, RecommendationEntity>newSerializationContext()
                .key(new StringRedisSerializer())
                .value(new GenericToStringSerializer<>(RecommendationEntity.class))
                .hashKey(new StringRedisSerializer())
                .hashValue(new GenericJackson2JsonRedisSerializer())
                .build();

        LOG.info("Connected to Redis on: " + redisHost + ":" + redisPort);

        return new ReactiveRedisTemplate<>(lettuceConnectionFactory, serializationContext);
    }

}
