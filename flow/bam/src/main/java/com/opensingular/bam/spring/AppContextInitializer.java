/*
 * Copyright (c) 2016, Singular and/or its affiliates. All rights reserved.
 * Singular PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package com.opensingular.bam.spring;

import java.io.File;
import java.io.IOException;
import java.util.Optional;
import java.util.Properties;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.core.env.PropertiesPropertySource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PropertiesLoaderUtils;
import org.springframework.stereotype.Component;
import org.springframework.web.context.ConfigurableWebApplicationContext;

import com.google.common.base.Throwables;

public class AppContextInitializer implements ApplicationContextInitializer<ConfigurableWebApplicationContext>{

    private static final String FLOW_BAM_CONFIG = "flow-bam-config.properties";
    private static final String FLOW_BAM_SECURITY = "flow-bam-security.xml";

    @Override
    public void initialize(ConfigurableWebApplicationContext applicationContext) {
        try {
            Properties props = getFlowBamConfig(applicationContext);

            Resource resource = getResource(applicationContext, FLOW_BAM_SECURITY);
            props.put("FLOW_BAM_SECURITY", resource.exists() ? resource.getURL() : "");
            
            PropertiesPropertySource ps = new PropertiesPropertySource("singularAdmin", props);
            applicationContext.getEnvironment().getPropertySources().addFirst(ps);
            
        } catch (IOException e) {
            throw Throwables.propagate(e);
        }
    }

    private static Resource getResource(ApplicationContext applicationContext, String fileName) {
        String singularHome = Optional.ofNullable(System.getenv("SINGULAR_HOME")).orElse(System.getProperty("SINGULAR_HOME"));
        if(StringUtils.isBlank(singularHome)){
            singularHome = "classpath:"+fileName;
        } else if(singularHome.endsWith(File.separator)){
            singularHome = "file:"+singularHome+"conf"+File.separator+ fileName;
        } else {
            singularHome = "file:"+singularHome+ File.separator+"conf"+File.separator+ fileName;
        }
        Resource resource = applicationContext.getResource(singularHome);
        if(!resource.exists()){
            resource = applicationContext.getResource("classpath:"+ fileName);
        }
        return resource;
    }

    public static Properties getFlowBamConfig(ApplicationContext applicationContext){
        try {
            Resource resource = getResource(applicationContext, FLOW_BAM_CONFIG);
            Properties props = PropertiesLoaderUtils.loadAllProperties("/"+FLOW_BAM_CONFIG);
            PropertiesLoaderUtils.fillProperties(props, resource);
            return props;
        } catch (IOException e) {
            throw Throwables.propagate(e);
        }
    }

    @Component("singularAdmin")
    public static class SingularProperties extends Properties implements ApplicationContextAware{

        public SingularProperties() {
            super();
        }
        
        @Override
        public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
            putAll(getFlowBamConfig(applicationContext));
        }
    }
}
