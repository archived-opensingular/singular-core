package org.opensingular.singular.form.provider;

import br.net.mirante.singular.commons.lambda.IFunction;
import org.opensingular.singular.form.SAttributeEnabled;
import org.opensingular.singular.form.SInstance;
import org.opensingular.singular.form.STranslatorForAttribute;
import org.opensingular.singular.form.converter.SInstanceConverter;

import java.io.Serializable;

public class AtrProvider extends STranslatorForAttribute {

    public AtrProvider() {
    }

    public AtrProvider(SAttributeEnabled alvo) {
        super(alvo);
    }

    public <T extends Serializable> AtrProvider filteredProvider(FilteredProvider<T> valor) {
        return provider(valor);
    }

    public <T extends Serializable, I extends SInstance> AtrProvider filteredOptionsProvider(TextQueryProvider<T, I> valor) {
        return provider(valor);
    }

    public <T extends Serializable, I extends SInstance> AtrProvider provider(Provider<T, I> valor) {
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

    public <T extends Serializable> IFunction<T, String> getDisplayFunction() {
        return getAttributeValue(SPackageProvider.DISPLAY_FUNCTION);
    }

    public <T extends Serializable, X > AtrProvider idFunction(IFunction<T, X> valor) {
        setAttributeValue(SPackageProvider.ID_FUNCTION, valor);
        return this;
    }

    public <T> IFunction<T, Object> getIdFunction() {
        return getAttributeValue(SPackageProvider.ID_FUNCTION);
    }

    public FilteredProvider getFilteredProvider() {
        return (FilteredProvider) getProvider();
    }

    public Provider getProvider() {
        return getAttributeValue(SPackageProvider.PROVIDER);
    }

    public SInstanceConverter getConverter() {
        return getAttributeValue(SPackageProvider.CONVERTER);
    }

}