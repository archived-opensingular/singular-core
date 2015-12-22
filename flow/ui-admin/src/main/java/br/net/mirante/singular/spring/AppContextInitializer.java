package br.net.mirante.singular.spring;

import java.io.IOException;
import java.util.Properties;

import org.springframework.context.ApplicationContextInitializer;
import org.springframework.core.env.PropertiesPropertySource;
import org.springframework.core.io.support.PropertiesLoaderUtils;
import org.springframework.web.context.ConfigurableWebApplicationContext;

import com.google.common.base.Throwables;

public class AppContextInitializer implements ApplicationContextInitializer<ConfigurableWebApplicationContext>{

    @Override
    public void initialize(ConfigurableWebApplicationContext applicationContext) {
        try {
            Properties props = PropertiesLoaderUtils.loadAllProperties("/admin-config.properties");
            PropertiesPropertySource ps = new PropertiesPropertySource("profile", props);
            applicationContext.getEnvironment().getPropertySources().addFirst(ps);
        } catch (IOException e) {
            throw Throwables.propagate(e);
        }
    }

}
