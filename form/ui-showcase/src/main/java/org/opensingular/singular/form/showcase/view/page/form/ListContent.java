/*
 * Copyright (c) 2016, Mirante and/or its affiliates. All rights reserved.
 * Mirante PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package org.opensingular.singular.form.showcase.view.page.form;

import static org.opensingular.singular.util.wicket.util.WicketUtils.$b;
import static org.opensingular.singular.util.wicket.util.WicketUtils.$m;

import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;

import javax.inject.Inject;
import javax.inject.Named;

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.panel.Fragment;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.ResourceModel;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.apache.wicket.util.string.StringValue;

import org.opensingular.singular.form.wicket.feedback.SFeedbackPanel;
import org.opensingular.singular.form.showcase.component.ShowCaseType;
import org.opensingular.singular.form.showcase.dao.form.ShowcaseTypeLoader;
import org.opensingular.singular.form.showcase.view.SingularWicketContainer;
import org.opensingular.singular.form.showcase.view.page.form.crud.CrudPage;
import org.opensingular.singular.form.showcase.view.template.Content;
import org.opensingular.singular.util.wicket.datatable.BSDataTable;
import org.opensingular.singular.util.wicket.datatable.BSDataTableBuilder;
import org.opensingular.singular.util.wicket.datatable.BaseDataProvider;
import org.opensingular.singular.util.wicket.datatable.column.BSActionColumn;
import org.opensingular.singular.util.wicket.resource.Icone;

@SuppressWarnings("serial")
public class ListContent extends Content implements SingularWicketContainer<ListContent, Void> {

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
        final StringValue tipoValue = getPage().getPageParameters().get(ShowCaseType.SHOWCASE_TYPE_PARAM);
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

        final BSDataTable<FormVO, String> dataTable = new BSDataTableBuilder<>(provider)
                .appendPropertyColumn(getMessage("label.table.column.form"),
                        "key", FormVO::getKey)
                .appendColumn(new BSActionColumn<FormVO, String>($m.ofValue(""))
                        .appendAction(getMessage("label.table.column.preview"),
                                Icone.ROCKET, this::goToDemo
                        )
                )
                .setRowsPerPage(Long.MAX_VALUE) //TODO: proper pagination
                .setStripedRows(false)
                .add($b.classAppender("worklist"))
                .build("form-list");
        return dataTable;
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