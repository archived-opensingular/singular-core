package br.net.mirante.singular.form.mform.builder.selection;

import br.net.mirante.singular.form.mform.SInstance;
import br.net.mirante.singular.form.mform.SType;
import br.net.mirante.singular.form.mform.converter.AutoSICompositeConverter;
import br.net.mirante.singular.form.mform.converter.EnumSInstanceConverter;
import br.net.mirante.singular.form.mform.converter.SInstanceConverter;
import br.net.mirante.singular.form.mform.converter.SimpleSInstanceConverter;

import java.io.Serializable;


public class ConverterBuilder<TYPE extends Serializable, ROOT_TYPE extends SInstance, ELEMENT_TYPE extends SInstance> extends AbstractBuilder {

    public ConverterBuilder(SType type) {
        super(type);
    }

    public <X extends Enum<X>> ProviderBuilder<TYPE, ROOT_TYPE> enumConverter(Class<X> enumClass) {
        type.asAtrProvider().asAtrProvider().converter(new EnumSInstanceConverter<>(enumClass));
        return next();
    }

    public ProviderBuilder<TYPE, ROOT_TYPE> converter(SInstanceConverter<TYPE, ELEMENT_TYPE> converter) {
        type.asAtrProvider().asAtrProvider().converter(converter);
        return next();
    }

    public ProviderBuilder<TYPE, ROOT_TYPE> autoConverterOf(Class resultClass) {
        type.asAtrProvider().asAtrProvider().converter(AutoSICompositeConverter.of(resultClass));
        return next();
    }

    public ProviderBuilder<TYPE, ROOT_TYPE> simpleConverter() {
        type.asAtrProvider().asAtrProvider().converter(new SimpleSInstanceConverter<>());
        return next();
    }

    private ProviderBuilder<TYPE, ROOT_TYPE> next() {
        return new ProviderBuilder<>(type);
    }

}