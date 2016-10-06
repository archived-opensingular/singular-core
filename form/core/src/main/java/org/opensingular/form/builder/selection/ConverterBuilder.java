package org.opensingular.form.builder.selection;

import org.opensingular.form.SInstance;
import org.opensingular.form.SType;
import org.opensingular.form.converter.AutoSICompositeConverter;
import org.opensingular.form.converter.EnumSInstanceConverter;
import org.opensingular.form.converter.SInstanceConverter;
import org.opensingular.form.converter.SimpleSInstanceConverter;

import java.io.Serializable;


public class ConverterBuilder<TYPE extends Serializable, ROOT_TYPE extends SInstance, ELEMENT_TYPE extends SInstance> extends AbstractBuilder {

    public ConverterBuilder(SType type) {
        super(type);
    }

    public <X extends Enum<X>> ProviderBuilder<TYPE, ROOT_TYPE> enumConverter(Class<X> enumClass) {
        type.asAtrProvider().converter(new EnumSInstanceConverter<>(enumClass));
        return next();
    }

    public ProviderBuilder<TYPE, ROOT_TYPE> converter(SInstanceConverter<TYPE, ELEMENT_TYPE> converter) {
        type.asAtrProvider().converter(converter);
        return next();
    }

    public ProviderBuilder<TYPE, ROOT_TYPE> autoConverterOf(Class resultClass) {
        type.asAtrProvider().converter(AutoSICompositeConverter.of(resultClass));
        return next();
    }

    public ProviderBuilder<TYPE, ROOT_TYPE> simpleConverter() {
        type.asAtrProvider().converter(new SimpleSInstanceConverter<>());
        return next();
    }

    private ProviderBuilder<TYPE, ROOT_TYPE> next() {
        return new ProviderBuilder<>(type);
    }

}