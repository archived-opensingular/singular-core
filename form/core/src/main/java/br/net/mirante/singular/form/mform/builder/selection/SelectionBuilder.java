package br.net.mirante.singular.form.mform.builder.selection;

import br.net.mirante.singular.commons.lambda.IFunction;
import br.net.mirante.singular.form.mform.SInstance;
import br.net.mirante.singular.form.mform.SType;
import br.net.mirante.singular.form.mform.provider.FreemarkerUtil;

import java.io.Serializable;


public class SelectionBuilder<T extends Serializable, I extends SInstance> extends AbstractBuilder {

    public SelectionBuilder(SType type) {
        super(type);
    }

    public ProviderBuilder<T, I> selfIdAndDisplay(){
        return selfId().selfDisplay().simpleConverter();
    }

    public SelectionDisplayBuilder<T, I> selfId() {
        type.asAtrProvider().asAtrProvider().idFunction((o) -> o);
        return next();
    }

    public SelectionDisplayBuilder<T, I> id(String freemarkerTemplate) {
        type.asAtrProvider().asAtrProvider().idFunction((o) -> FreemarkerUtil.mergeWithFreemarker(freemarkerTemplate, o));
        return next();
    }

    public SelectionDisplayBuilder<T, I> id(IFunction<T, Serializable> valor) {
        type.asAtrProvider().asAtrProvider().idFunction(valor);
        return next();
    }

    private SelectionDisplayBuilder<T, I> next() {
        return new SelectionDisplayBuilder<>(type);
    }


}
