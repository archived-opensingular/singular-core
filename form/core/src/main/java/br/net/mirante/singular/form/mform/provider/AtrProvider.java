package br.net.mirante.singular.form.mform.provider;

import br.net.mirante.singular.commons.lambda.IFunction;
import br.net.mirante.singular.form.mform.SAttributeEnabled;
import br.net.mirante.singular.form.mform.STranslatorForAttribute;
import br.net.mirante.singular.form.mform.converter.SInstanceConverter;

import java.io.Serializable;

public class AtrProvider extends STranslatorForAttribute {

    public AtrProvider() {
    }

    public AtrProvider(SAttributeEnabled alvo) {
        super(alvo);
    }

    public <T> AtrProvider filteredPagedProvider(FilteredPagedProvider<T> valor) {
        return provider(valor);
    }

    public <T extends Serializable> AtrProvider fixedOptionsProvider(SimpleProvider<T> valor) {
        return provider(valor);
    }

    public <T extends Serializable> AtrProvider filteredOptionsProvider(FilteredProvider<T> valor) {
        return provider(valor);
    }

    public <T extends Serializable> AtrProvider provider(Provider<T> valor) {
        setAttributeValue(SPackageProvider.PROVIDER, valor);
        return this;
    }

    public AtrProvider converter(SInstanceConverter valor) {
        setAttributeValue(SPackageProvider.CONVERTER, valor);
        return this;
    }

    public AtrProvider displayFunction(IFunction valor) {
        setAttributeValue(SPackageProvider.DISPLAY_FUNCTION, valor);
        return this;
    }

    public <T> IFunction<T, String> getDisplayFunction() {
        return getAttributeValue(SPackageProvider.DISPLAY_FUNCTION);
    }

    public AtrProvider idFunction(IFunction valor) {
        setAttributeValue(SPackageProvider.ID_FUNCTION, valor);
        return this;
    }

    public <T> IFunction<T, String> getIdFunction() {
        return getAttributeValue(SPackageProvider.ID_FUNCTION);
    }

    public SimpleProvider getSimpleProvider() {
        final Provider provider = getProvider();
        if (provider instanceof SimpleProvider) {
            return (SimpleProvider) provider;
        }
        return null;
    }

    public FilteredPagedProvider getFilteredPagedProvider() {
        return (FilteredPagedProvider) getProvider();
    }

    public FilteredProvider getFilteredProvider() {
        final Provider provider = getProvider();
        if (provider instanceof FilteredProvider) {
            return (FilteredProvider) provider;
        }
        return null;
    }

    public Provider getProvider() {
        return getAttributeValue(SPackageProvider.PROVIDER);
    }

    public SInstanceConverter getConverter() {
        return getAttributeValue(SPackageProvider.CONVERTER);
    }

}