package org.opensingular.form.internal.freemarker;

import freemarker.template.TemplateModelException;
import freemarker.template.TemplateNumberModel;
import org.opensingular.form.type.core.SINumber;

public class SNumberTemplateModel<INSTANCE extends SINumber<?>> extends SSimpleTemplateModel<INSTANCE>
        implements TemplateNumberModel {

    public SNumberTemplateModel(INSTANCE instance, FormObjectWrapper formObjectWrapper) {
        super(instance, formObjectWrapper, false);
    }

    public SNumberTemplateModel(INSTANCE instance, FormObjectWrapper formObjectWrapper, boolean escapeContentHtml) {
        super(instance, formObjectWrapper, escapeContentHtml);
    }

    @Override
    public Number getAsNumber() throws TemplateModelException {
        return getInstance().getValue();
    }

}