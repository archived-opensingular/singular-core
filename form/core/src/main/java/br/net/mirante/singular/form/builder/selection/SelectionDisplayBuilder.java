package br.net.mirante.singular.form.builder.selection;

import br.net.mirante.singular.commons.lambda.IFunction;
import br.net.mirante.singular.form.SInstance;
import br.net.mirante.singular.form.SType;
import br.net.mirante.singular.form.provider.FreemarkerUtil;

import java.io.Serializable;

public class SelectionDisplayBuilder<TYPE extends Serializable, ROOT_TYPE extends SInstance, ELEMENT_TYPE extends SInstance> extends AbstractBuilder {

    public SelectionDisplayBuilder(SType type) {
        super(type);
    }

    public ConverterBuilder<TYPE, ROOT_TYPE, ELEMENT_TYPE> selfDisplay() {
        type.asAtrProvider().displayFunction((o) -> o);
        return next();
    }

    public ConverterBuilder<TYPE, ROOT_TYPE, ELEMENT_TYPE> display(String freemarkerTemplate) {
        type.asAtrProvider().displayFunction((o) -> FreemarkerUtil.mergeWithFreemarker(freemarkerTemplate, o));
        return next();
    }

    public ConverterBuilder<TYPE, ROOT_TYPE, ELEMENT_TYPE> display(IFunction<TYPE, String> valor) {
        type.asAtrProvider().displayFunction(valor);
        return next();
    }

    private ConverterBuilder<TYPE, ROOT_TYPE, ELEMENT_TYPE> next() {
        return new ConverterBuilder<>(type);
    }
}
