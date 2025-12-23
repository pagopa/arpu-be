package it.gov.pagopa.arc.config;

import it.gov.pagopa.arc.dto.IamUserInfoDTO;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.cache.autoconfigure.RedisCacheManagerBuilderCustomizer;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.serializer.JacksonJsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import tools.jackson.databind.json.JsonMapper;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;

@Configuration
@EnableCaching
public class RedisConfig {

  public static final String CACHE_NAME_ACCESS_TOKEN = "ACCESS_TOKEN";

  public static final String CACHE_OAUTH2_STATE = "OAUTH2_STATE";

  @Bean
  public RedisCacheManagerBuilderCustomizer redisCacheManagerBuilderCustomizer(
      JsonMapper jsonMapper,
      @Value("${jwt.access-token.expire-in}") int accessTokenExpirationSeconds
  ) {
    Map<String, RedisCacheConfiguration> cacheConfigurations = new HashMap<>();
    cacheConfigurations.put(CACHE_OAUTH2_STATE,RedisCacheConfiguration.defaultCacheConfig()
        .entryTtl(Duration.ofSeconds(300))
        .disableCachingNullValues());
    cacheConfigurations.put(CACHE_NAME_ACCESS_TOKEN,redisJsonSerializationConfiguration(jsonMapper,accessTokenExpirationSeconds,IamUserInfoDTO.class));
    return builder -> builder
        .withInitialCacheConfigurations(cacheConfigurations);
  }

  private RedisCacheConfiguration redisJsonSerializationConfiguration(JsonMapper jsonMapper, int ttl, Class<?> type){
    return RedisCacheConfiguration.defaultCacheConfig()
          .serializeValuesWith(RedisSerializationContext.SerializationPair.fromSerializer(new JacksonJsonRedisSerializer<>(jsonMapper,
              type)))
          .entryTtl(Duration.ofSeconds(ttl))
          .disableCachingNullValues();
  }

}
