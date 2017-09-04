package org.opensingular.form.internal.freemarker;

import freemarker.template.TemplateModel;
import freemarker.template.TemplateModelException;
import freemarker.template.TemplateSequenceModel;
import org.apache.commons.lang3.StringUtils;
import org.opensingular.form.SIList;


public class SListTemplateModel extends SInstanceTemplateModel<SIList<?>> implements TemplateSequenceModel {
    private final FormObjectWrapper formObjectWrapper;

    public SListTemplateModel(SIList<?> list, FormObjectWrapper formObjectWrapper) {
        super(list, formObjectWrapper, false);
        this.formObjectWrapper = formObjectWrapper;
    }

    public SListTemplateModel(SIList<?> list, FormObjectWrapper formObjectWrapper, boolean escapeContentHtml) {
        super(list, formObjectWrapper, escapeContentHtml);
        this.formObjectWrapper = formObjectWrapper;
    }

    @Override
    public TemplateModel get(int index) throws TemplateModelException {
        return formObjectWrapper.newTemplateModel(getInstance().get(index), escapeContentHtml);
    }

    @Override
    public int size() throws TemplateModelException {
        return getInstance().size();
    }

    @Override
    public String getAsString() throws TemplateModelException {
        return StringUtils.defaultString(getInstance().toStringDisplay());
    }
}