package org.opensingular.form.builder.selection;

import org.opensingular.form.SInstance;
import org.opensingular.singular.commons.lambda.IFunction;
import org.opensingular.form.SType;
import org.opensingular.form.provider.FreemarkerUtil;

import java.io.Serializable;

public class SelectionDisplayBuilder<TYPE extends Serializable, ROOT_TYPE extends SInstance, ELEMENT_TYPE extends SInstance> extends AbstractBuilder {

    public SelectionDisplayBuilder(SType<?> type) {
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
