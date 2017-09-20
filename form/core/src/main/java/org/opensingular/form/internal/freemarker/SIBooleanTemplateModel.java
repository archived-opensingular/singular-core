package org.opensingular.form.internal.freemarker;

import freemarker.template.TemplateBooleanModel;
import freemarker.template.TemplateModelException;
import org.opensingular.form.type.core.SIBoolean;


public class SIBooleanTemplateModel extends SSimpleTemplateModel<SIBoolean> implements TemplateBooleanModel {

    public SIBooleanTemplateModel(SIBoolean instance, FormObjectWrapper formObjectWrapper) {
        super(instance, formObjectWrapper, false);
    }

    public SIBooleanTemplateModel(SIBoolean instance, FormObjectWrapper formObjectWrapper, boolean escapeContentHtml) {
        super(instance, formObjectWrapper, escapeContentHtml);
    }

    @Override
    public boolean getAsBoolean() throws TemplateModelException {
        Boolean v = getInstance().getValueWithDefault();
        return v == null ? false : v;
    }
}