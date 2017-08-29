package org.opensingular.form.internal.freemarker;

import freemarker.template.TemplateDateModel;
import freemarker.template.TemplateModelException;
import org.opensingular.form.type.core.SITime;

import java.util.Date;

public class SITimeTemplateModel extends SSimpleTemplateModel<SITime> implements TemplateDateModel {

    public SITimeTemplateModel(SITime instance, FormObjectWrapper formObjectWrapper) {
        super(instance, formObjectWrapper, false);
    }

    public SITimeTemplateModel(SITime instance, FormObjectWrapper formObjectWrapper, boolean escapeContentHtml) {
        super(instance, formObjectWrapper, escapeContentHtml);
    }

    @Override
    public Date getAsDate() throws TemplateModelException {
        return getInstance().getValue();
    }

    @Override
    public int getDateType() {
        return TIME;
    }
}
