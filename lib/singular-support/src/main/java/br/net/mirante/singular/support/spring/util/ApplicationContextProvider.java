package br.net.mirante.singular.support.spring.util;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

import java.util.function.Function;
import java.util.function.Supplier;

public class ApplicationContextProvider implements ApplicationContextAware {

    private static ApplicationContext applicationContext;

    private static synchronized void setup(ApplicationContext applicationContext){
        ApplicationContextProvider.applicationContext = applicationContext;
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        ApplicationContextProvider.setup(applicationContext);
    }

    public static ApplicationContext get() {
        return applicationContext;
    }

    public static <T> Supplier<T> supplierOf(Function<ApplicationContext, T> factory) {
        return () -> factory.apply(ApplicationContextProvider.get());
    }

}