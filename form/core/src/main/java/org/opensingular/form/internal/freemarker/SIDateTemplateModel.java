package org.opensingular.form.internal.freemarker;

import freemarker.template.TemplateDateModel;
import freemarker.template.TemplateModelException;
import org.opensingular.form.type.core.SIDate;

import java.util.Date;

public class SIDateTemplateModel extends SSimpleTemplateModel<SIDate> implements TemplateDateModel {

    public SIDateTemplateModel(SIDate instance, FormObjectWrapper formObjectWrapper) {
        super(instance, formObjectWrapper, false);
    }

    public SIDateTemplateModel(SIDate instance, FormObjectWrapper formObjectWrapper, boolean escapeContentHtml) {
        super(instance, formObjectWrapper, escapeContentHtml);
    }

    @Override
    public Date getAsDate() throws TemplateModelException {
        return getInstance().getValue();
    }

    @Override
    public int getDateType() {
        return DATE;
    }
}
