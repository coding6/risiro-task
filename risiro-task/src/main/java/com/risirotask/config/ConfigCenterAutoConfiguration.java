package com.risirotask.config;

import com.ctrip.framework.apollo.ConfigService;
import com.risirotask.core.metrics.config.ApolloConfigCenter;
import com.risirotask.core.metrics.config.DefaultConfigCenter;
import com.risirotask.core.metrics.config.ConfigCenter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

/**
 * @author coding6
 * @create 2025/9/24
 * @description
 */
@Configuration
public class ConfigCenterAutoConfiguration {

    @Bean
    @ConditionalOnProperty(prefix = "risiro-task.config-center", name = "type", havingValue = "apollo")
    @ConditionalOnClass(ConfigService.class)
    @Primary
    public ConfigCenter apolloConfigCenter(@Value("${risiro-task.config-center.namespace:application}") String namespace) {
        return new ApolloConfigCenter(namespace);
    }

    @Bean
    @ConditionalOnMissingBean(ConfigService.class)
    public ConfigCenter defaultConfigCenter() {
        return new DefaultConfigCenter();
    }
}
