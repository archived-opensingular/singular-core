package br.net.mirante.singular.view.page.form;

import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.panel.Fragment;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.ResourceModel;
import org.apache.wicket.request.mapper.parameter.PageParameters;

import br.net.mirante.singular.dao.form.TemplateRepository;
import br.net.mirante.singular.util.wicket.datatable.BSDataTable;
import br.net.mirante.singular.util.wicket.datatable.BSDataTableBuilder;
import br.net.mirante.singular.util.wicket.datatable.BaseDataProvider;
import br.net.mirante.singular.util.wicket.datatable.column.BSActionColumn;
import br.net.mirante.singular.util.wicket.feedback.BSFeedbackPanel;
import br.net.mirante.singular.util.wicket.resource.Icone;
import br.net.mirante.singular.util.wicket.util.WicketUtils;
import br.net.mirante.singular.view.SingularWicketContainer;
import br.net.mirante.singular.view.page.form.crud.CrudPage;
import br.net.mirante.singular.view.template.Content;

@SuppressWarnings("serial")
class ListContent extends Content implements SingularWicketContainer<ListContent, Void> {

    final static List<FormVO> formTypes;

    static {
        formTypes = TemplateRepository.get().getEntries().stream().map(t -> new FormVO(t)).collect(Collectors.toList());
    }

    public ListContent(String id) {
        super(id, false, true);
    }

    @Override
    protected void onInitialize() {
        super.onInitialize();

        queue(new BSFeedbackPanel("feedback"));
        queue(buildFormDataTable());
    }

    private BSDataTable<FormVO, String> buildFormDataTable() {
        BaseDataProvider<FormVO, String> provider =
                new BaseDataProvider<FormVO, String>() {

                    @Override
                    public long size() {
                        return formTypes.size();
                    }

                    @Override
                    public Iterator<? extends FormVO> iterator(int first, int count,
                            String sortProperty, boolean ascending) {
                        return formTypes.iterator();
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
    protected IModel<?> getContentTitlelModel() {
        return new ResourceModel("label.content.title");
    }

    @Override
    protected IModel<?> getContentSubtitlelModel() {
        return new ResourceModel("label.content.subtitle");
    }
}