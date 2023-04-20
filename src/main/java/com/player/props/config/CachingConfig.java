package com.player.props.config;

import java.lang.reflect.Array;
import java.util.Arrays;

import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.concurrent.ConcurrentMapCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class CachingConfig {

  @Bean(name = "cacheManager")
  public CacheManager cacheManager() {
    ConcurrentMapCacheManager cacheManager = new ConcurrentMapCacheManager();
    cacheManager.setCacheNames(Arrays.asList("gameInfo", "playerInfo", "playerGameInfo", "teamInfo"));
    return cacheManager;
  }
}
