package br.net.mirante.singular.bamclient.portlet.filter;


import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import br.net.mirante.singular.bamclient.portlet.FilterConfig;

public class FilterConfigFactory {

    public static List<FilterConfig> createConfigForClass(Class clazz) {
        final List<FilterConfig> filterConfigs = new ArrayList<>();
        for (Field f : clazz.getDeclaredFields()) {
            if (f.isAnnotationPresent(FilterField.class)) {

                final FilterField ff = f.getAnnotation(FilterField.class);
                final FilterConfig fc = new FilterConfig();

                fc.setIdentifier(f.getName());
                fc.setLabel(ff.label());

                if (ff.type() != FieldType.DEFAULT) {
                    fc.setFieldType(ff.type());
                } else {
                    fc.setFieldType(identifyType(f));
                }

                final RestOptionsProvider optionsProvider = ff.optionsProvider();

                if (!optionsProvider.endpoint().isEmpty()) {
                    fc.setRestEndpoint(optionsProvider.endpoint());
                    fc.setRestReturnType(optionsProvider.returnType());
                } else {
                    final String[] options = ff.options();
                    if (options.length > 0 && !options[0].isEmpty()) {
                        fc.setOptions(ff.options());
                    }
                }

                fc.setSize(ff.size().getBootstrapSize());
                filterConfigs.add(fc);
            }
        }
        return filterConfigs;
    }

    private static FieldType identifyType(Field field) {
        return FieldType.getDefaultTypeForClass(field.getType());
    }

}
