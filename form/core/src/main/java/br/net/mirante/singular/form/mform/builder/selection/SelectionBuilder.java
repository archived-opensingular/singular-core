package br.net.mirante.singular.form.mform.builder.selection;

import br.net.mirante.singular.commons.lambda.IFunction;
import br.net.mirante.singular.form.mform.SType;
import br.net.mirante.singular.form.mform.provider.FreemarkerUtil;

import java.io.Serializable;


public class SelectionBuilder<T extends Serializable> extends AbstractBuilder {

    public SelectionBuilder(SType type) {
        super(type);
    }

    public ProviderBuilder<T> selfIdAndDisplay(){
        return selfId().selfDisplay().simpleConverter();
    }

    public SelectionDisplayBuilder<T> selfId() {
        type.asAtrProvider().asAtrProvider().idFunction((o) -> o);
        return next();
    }

    public SelectionDisplayBuilder<T> id(String freemarkerTemplate) {
        type.asAtrProvider().asAtrProvider().idFunction((o) -> FreemarkerUtil.mergeWithFreemarker(freemarkerTemplate, o));
        return next();
    }

    public SelectionDisplayBuilder<T> id(IFunction<T, String> valor) {
        type.asAtrProvider().asAtrProvider().idFunction(valor);
        return next();
    }

    private SelectionDisplayBuilder<T> next() {
        return new SelectionDisplayBuilder<T>(type);
    }


}
