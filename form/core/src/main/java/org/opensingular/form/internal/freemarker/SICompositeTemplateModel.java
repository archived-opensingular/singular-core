package org.opensingular.form.internal.freemarker;

import freemarker.template.TemplateModel;
import freemarker.template.TemplateModelException;
import org.apache.commons.lang3.StringUtils;
import org.opensingular.form.SIComposite;
import org.opensingular.form.SInstance;

import java.util.Collection;


public class SICompositeTemplateModel extends SInstanceTemplateModel<SIComposite> {

    private final FormObjectWrapper formObjectWrapper;

    public SICompositeTemplateModel(SIComposite composite, FormObjectWrapper formObjectWrapper) {
        super(composite, formObjectWrapper, false);
        this.formObjectWrapper = formObjectWrapper;
    }

    public SICompositeTemplateModel(SIComposite composite, FormObjectWrapper formObjectWrapper, boolean escapeContentHtml) {
        super(composite, formObjectWrapper, escapeContentHtml);
        this.formObjectWrapper = formObjectWrapper;
    }

    @Override
    public TemplateModel get(String key) throws TemplateModelException {
        TemplateModel model;
        if (isInvertedPriority()) {
            model = super.get(key);
            if (model == null) {
                model = getTemplateFromField(key);
            }
        } else {
            model = getTemplateFromField(key);
            if (model == null) {
                model = super.get(key);
            }
        }
        return model;
    }

    private TemplateModel getTemplateFromField(String key) {
        return getInstance().getFieldOpt(key).map(instance -> formObjectWrapper.newTemplateModel(instance, escapeContentHtml)).orElse(null);
    }

    @Override
    public boolean isEmpty() throws TemplateModelException {
        return getInstance().isEmptyOfData();
    }

    @Override
    public String getAsString() throws TemplateModelException {
        return StringUtils.defaultString(getInstance().toStringDisplay());
    }

    @Override
    protected Object getValue() {
        return new SInstanceCollectionTemplateModel((Collection<SInstance>) getInstance().getValue(), escapeContentHtml, formObjectWrapper);
    }
}
