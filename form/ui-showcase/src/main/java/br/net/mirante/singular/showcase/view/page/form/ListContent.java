/*
 * Copyright (c) 2016, Mirante and/or its affiliates. All rights reserved.
 * Mirante PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package br.net.mirante.singular.showcase.view.page.form;

import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;

import javax.inject.Inject;
import javax.inject.Named;

import br.net.mirante.singular.showcase.component.ShowCaseType;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.panel.Fragment;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.ResourceModel;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.apache.wicket.util.string.StringValue;

import br.net.mirante.singular.form.wicket.feedback.SFeedbackPanel;
import br.net.mirante.singular.showcase.dao.form.ShowcaseTypeLoader;
import br.net.mirante.singular.showcase.view.SingularWicketContainer;
import br.net.mirante.singular.showcase.view.page.form.crud.CrudPage;
import br.net.mirante.singular.showcase.view.template.Content;
import br.net.mirante.singular.util.wicket.datatable.BSDataTable;
import br.net.mirante.singular.util.wicket.datatable.BSDataTableBuilder;
import br.net.mirante.singular.util.wicket.datatable.BaseDataProvider;
import br.net.mirante.singular.util.wicket.datatable.column.BSActionColumn;
import br.net.mirante.singular.util.wicket.resource.Icone;
import br.net.mirante.singular.util.wicket.util.WicketUtils;

@SuppressWarnings("serial")
class ListContent extends Content implements SingularWicketContainer<ListContent, Void> {

    private List<FormVO> formTypes;

    @Inject
    @Named("showcaseTypeLoader")
    ShowcaseTypeLoader showcaseTypeLoader;

    public ListContent(String id) {
        super(id, false, true);
    }

    private List<FormVO> getFormTypes() {
        if (formTypes == null) {
            formTypes = showcaseTypeLoader.getEntries().stream().filter(this::verificarTipo).map(t -> new FormVO(t)).collect(Collectors.toList());
        }
        return formTypes;
    }

    private boolean verificarTipo(ShowcaseTypeLoader.TemplateEntry templateEntry) {
        final StringValue tipoValue = getPage().getPageParameters().get(ListPage.PARAM_TIPO);
        ShowCaseType tipo;
        if (tipoValue.isNull() || tipoValue.toString().equals(ShowCaseType.FORM.toString())) {
            tipo = ShowCaseType.FORM;
        } else {
            tipo = ShowCaseType.STUDIO;
        }
        return tipo.equals(templateEntry.getTipo());
    }

    @Override
    protected void onInitialize() {
        super.onInitialize();

        queue(new SFeedbackPanel("feedback", this));
        queue(buildFormDataTable());
    }

    private BSDataTable<FormVO, String> buildFormDataTable() {
        BaseDataProvider<FormVO, String> provider =
                new BaseDataProvider<FormVO, String>() {

                    @Override
                    public long size() {
                return getFormTypes().size();
                    }

                    @Override
                    public Iterator<? extends FormVO> iterator(int first, int count,
                            String sortProperty, boolean ascending) {
                return getFormTypes().iterator();
                    }
                };

        return new BSDataTableBuilder<>(provider)
                .appendPropertyColumn(getMessage("label.table.column.form"),
                        "key", FormVO::getKey)
                .appendColumn(new BSActionColumn<FormVO, String>(WicketUtils.$m.ofValue(""))
                                .appendAction(getMessage("label.table.column.preview"),
                                        Icone.ROCKET, this::goToDemo
                                )
                )
                .setRowsPerPage(Long.MAX_VALUE) //TODO: proper pagination
                .build("form-list");
    }

    private void goToDemo(AjaxRequestTarget target, IModel<FormVO> model) {
        FormVO form = model.getObject();
        PageParameters params = new PageParameters()
            .add(CrudPage.TYPE_NAME, form.getTypeName());
        setResponsePage(CrudPage.class, params);
    }

    @Override
    protected WebMarkupContainer getBreadcrumbLinks(String id) {
        return new Fragment(id, "breadcrumbForm", this);
    }

    @Override
    protected IModel<?> getContentTitleModel() {
        return new ResourceModel("label.content.title");
    }

    @Override
    protected IModel<?> getContentSubtitleModel() {
        return new ResourceModel("label.content.subtitle");
    }
}