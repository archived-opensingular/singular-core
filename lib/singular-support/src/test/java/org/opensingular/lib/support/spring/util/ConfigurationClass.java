package org.opensingular.lib.support.spring.util;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ConfigurationClass {
    @Bean
    public SimpleBeanClass simpleBeanClass(){return new SimpleBeanClass();}
}
