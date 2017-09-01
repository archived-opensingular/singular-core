package org.opensingular.form.internal.freemarker;

import freemarker.template.TemplateModelException;
import freemarker.template.TemplateScalarModel;
import org.apache.commons.lang3.StringEscapeUtils;
import org.apache.commons.lang3.StringUtils;
import org.opensingular.form.SISimple;


public class SSimpleTemplateModel<INSTANCE extends SISimple<?>> extends SInstanceTemplateModel<INSTANCE>
        implements TemplateScalarModel {


    public SSimpleTemplateModel(INSTANCE instance, FormObjectWrapper formObjectWrapper) {
        super(instance, formObjectWrapper, false);
    }

    public SSimpleTemplateModel(INSTANCE instance, FormObjectWrapper formObjectWrapper, boolean escapeContentHtml) {
        super(instance, formObjectWrapper, escapeContentHtml);
    }

    @Override
    public String getAsString() throws TemplateModelException {
        if (escapeContentHtml) {
            return StringUtils.defaultString(StringEscapeUtils.escapeHtml4(getInstance().toStringDisplayDefault()));
        } else {
            return StringUtils.defaultString(getInstance().toStringDisplayDefault());
        }
    }
}
