package br.net.mirante.singular.form.mform.basic.provider;

import br.net.mirante.singular.form.mform.SAttributeEnabled;
import br.net.mirante.singular.form.mform.STranslatorForAttribute;
import br.net.mirante.singular.form.mform.provider.FilteredPagedProvider;
import br.net.mirante.singular.form.mform.provider.ValueToSInstanceConverter;

public class AtrProvider extends STranslatorForAttribute {

    public AtrProvider() {
    }

    public AtrProvider(SAttributeEnabled alvo) {
        super(alvo);
    }

    public <T, P extends FilteredPagedProvider<T>> AtrProvider provider(P valor) {
        setAttributeValue(SPackageProvider.PROVIDER, valor);
        return this;
    }

    public AtrProvider converter(ValueToSInstanceConverter valor) {
        setAttributeValue(SPackageProvider.CONVERTER, valor);
        return this;
    }

    public FilteredPagedProvider getProvider() {
        return getAttributeValue(SPackageProvider.PROVIDER);
    }

    public ValueToSInstanceConverter getConverter() {
        return getAttributeValue(SPackageProvider.CONVERTER);
    }

}
