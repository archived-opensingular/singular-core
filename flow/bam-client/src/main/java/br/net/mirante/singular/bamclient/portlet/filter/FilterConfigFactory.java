package br.net.mirante.singular.bamclient.portlet.filter;


import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import br.net.mirante.singular.bamclient.portlet.FilterConfig;

public class FilterConfigFactory {

    public static List<FilterConfig> createConfigForClass(Class clazz) {
        final List<FilterConfig> configs = new ArrayList<>();
        for (Field field : clazz.getDeclaredFields()) {
            if (field.isAnnotationPresent(FilterField.class)) {
                final FilterField filterField = field.getAnnotation(FilterField.class);
                final FilterConfig filterConfig = new FilterConfig();
                filterConfig.setIdentificador(field.getName());
                filterConfig.setLabel(filterField.label());
                if (filterField.type() != FieldType.DEFAULT) {
                    filterConfig.setFieldType(filterField.type());
                } else {
                    filterConfig.setFieldType(identifyType(field));
                }
                filterConfig.setSize(filterField.size().getBootstrapSize());
                configs.add(filterConfig);
            }
        }
        return configs;
    }

    private static FieldType identifyType(Field field) {
        return FieldType.getDefaultTypeForClass(field.getType());
    }

}
