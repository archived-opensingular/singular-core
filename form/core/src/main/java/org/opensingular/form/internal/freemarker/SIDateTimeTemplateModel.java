package org.opensingular.form.internal.freemarker;

import freemarker.template.TemplateDateModel;
import freemarker.template.TemplateModelException;
import org.opensingular.form.type.core.SIDateTime;

import java.util.Date;

public class SIDateTimeTemplateModel extends SSimpleTemplateModel<SIDateTime> implements TemplateDateModel {

    public SIDateTimeTemplateModel(SIDateTime instance, FormObjectWrapper formObjectWrapper) {
        super(instance, formObjectWrapper, false);
    }

    public SIDateTimeTemplateModel(SIDateTime instance, FormObjectWrapper formObjectWrapper, boolean escapeContentHtml) {
        super(instance, formObjectWrapper, escapeContentHtml);
    }


    @Override
    public Date getAsDate() throws TemplateModelException {
        return getInstance().getValue();
    }

    @Override
    public int getDateType() {
        return DATETIME;
    }
}
