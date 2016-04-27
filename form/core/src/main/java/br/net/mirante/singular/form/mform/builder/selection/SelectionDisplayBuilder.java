package br.net.mirante.singular.form.mform.builder.selection;

import br.net.mirante.singular.commons.lambda.IFunction;
import br.net.mirante.singular.form.mform.SType;
import br.net.mirante.singular.form.mform.provider.FreemarkerUtil;

import java.io.Serializable;

public class SelectionDisplayBuilder<T extends Serializable> extends AbstractBuilder {

    public SelectionDisplayBuilder(SType type) {
        super(type);
    }

    public ConverterBuilder<T> selfDisplay() {
        type.asAtrProvider().asAtrProvider().displayFunction((o) -> o);
        return next();
    }

    public ConverterBuilder<T> display(String freemarkerTemplate) {
        type.asAtrProvider().asAtrProvider().displayFunction((o) -> FreemarkerUtil.mergeWithFreemarker(freemarkerTemplate, o));
        return next();
    }

    public ConverterBuilder<T> display(IFunction<T, String> valor) {
        type.asAtrProvider().asAtrProvider().displayFunction(valor);
        return next();
    }

    private ConverterBuilder<T> next() {
        return new ConverterBuilder<T>(type);
    }
}
