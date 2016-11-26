package org.opensingular.server.commons.cache;

import org.springframework.cache.interceptor.SimpleKeyGenerator;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.stream.Collectors;
import java.util.stream.Stream;


public class SingularKeyGenerator extends SimpleKeyGenerator {

    @Override
    public Object generate(Object target, Method method, Object... params) {
        if (params.length == 0) {
            String methodName       = method.getName();
            String methodReturnType = method.getReturnType().getName();
            String parameters       = Stream.of(method.getParameters()).map(Parameter::getName).collect(Collectors.joining());
            return (methodName.hashCode() + methodReturnType.hashCode() + parameters.hashCode()) / 2;
        } else {
            return super.generate(target, method, params);
        }
    }

}