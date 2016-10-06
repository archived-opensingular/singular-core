package org.opensingular.singular.server.commons.wicket.view.form;


import org.opensingular.form.context.SFormConfig;
import org.opensingular.form.service.IFormService;
import org.opensingular.singular.server.commons.wicket.view.template.Content;
import org.opensingular.singular.server.commons.wicket.view.template.Template;
import org.apache.wicket.model.IModel;

import javax.inject.Inject;
import javax.inject.Named;

public class ReadOnlyFormPage extends Template {

    @Inject
    @Named("formConfigWithDatabase")
    private SFormConfig<String> singularFormConfig;

    @Inject
    private IFormService formService;

    private final IModel<Long> formVersionEntityPK;

    public ReadOnlyFormPage(IModel<Long> formVersionEntityPK) {
        this.formVersionEntityPK = formVersionEntityPK;
    }

    @Override
    protected Content getContent(String id) {
        return new ReadOnlyFormContent(id, formVersionEntityPK, formService, singularFormConfig);
    }

    @Override
    protected boolean withMenu() {
        return false;
    }
}