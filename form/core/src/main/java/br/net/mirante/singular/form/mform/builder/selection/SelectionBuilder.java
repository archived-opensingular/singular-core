package br.net.mirante.singular.form.mform.builder.selection;

import br.net.mirante.singular.commons.lambda.IFunction;
import br.net.mirante.singular.form.mform.SInstance;
import br.net.mirante.singular.form.mform.SType;
import br.net.mirante.singular.form.mform.provider.FreemarkerUtil;

import java.io.Serializable;


public class SelectionBuilder<TYPE extends Serializable, ROOT_TYPE extends SInstance, ELEMENT_TYPE extends SInstance> extends AbstractBuilder {

    public SelectionBuilder(SType type) {
        super(type);
    }

    public ProviderBuilder<TYPE, ROOT_TYPE> selfIdAndDisplay(){
        return selfId().selfDisplay().simpleConverter();
    }

    public SelectionDisplayBuilder<TYPE, ROOT_TYPE, ELEMENT_TYPE> selfId() {
        type.asAtrProvider().asAtrProvider().idFunction((o) -> o);
        return next();
    }

    public SelectionDisplayBuilder<TYPE, ROOT_TYPE, ELEMENT_TYPE> id(String freemarkerTemplate) {
        type.asAtrProvider().asAtrProvider().idFunction((o) -> FreemarkerUtil.mergeWithFreemarker(freemarkerTemplate, o));
        return next();
    }

    public SelectionDisplayBuilder<TYPE, ROOT_TYPE, ELEMENT_TYPE> id(IFunction<TYPE, Object> valor) {
        type.asAtrProvider().asAtrProvider().idFunction(valor);
        return next();
    }

    private SelectionDisplayBuilder<TYPE, ROOT_TYPE, ELEMENT_TYPE> next() {
        return new SelectionDisplayBuilder<>(type);
    }


}
