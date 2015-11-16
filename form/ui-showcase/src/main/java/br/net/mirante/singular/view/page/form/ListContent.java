package br.net.mirante.singular.view.page.form;

import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.panel.Fragment;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.ResourceModel;

import br.net.mirante.singular.dao.form.TemplateRepository;
import br.net.mirante.singular.form.mform.MInstancia;
import br.net.mirante.singular.form.mform.MTipo;
import br.net.mirante.singular.form.wicket.UIBuilderWicket;
import br.net.mirante.singular.form.wicket.WicketBuildContext;
import br.net.mirante.singular.form.wicket.model.MInstanceRootModel;
import br.net.mirante.singular.util.wicket.bootstrap.layout.BSGrid;
import br.net.mirante.singular.util.wicket.bootstrap.layout.BSLabel;
import br.net.mirante.singular.util.wicket.datatable.BSDataTable;
import br.net.mirante.singular.util.wicket.datatable.BSDataTableBuilder;
import br.net.mirante.singular.util.wicket.datatable.BaseDataProvider;
import br.net.mirante.singular.util.wicket.datatable.column.BSActionColumn;
import br.net.mirante.singular.util.wicket.feedback.BSFeedbackPanel;
import br.net.mirante.singular.util.wicket.modal.BSModalBorder;
import br.net.mirante.singular.util.wicket.resource.Icone;
import br.net.mirante.singular.util.wicket.util.WicketUtils;
import br.net.mirante.singular.view.SingularWicketContainer;
import br.net.mirante.singular.view.template.Content;

@SuppressWarnings("serial")
class ListContent extends Content implements SingularWicketContainer<ListContent, Void> {

    final static List<FormVO> formTypes;

    private final BSModalBorder parametersModal = new BSModalBorder("parametersModal"),
            previewModal = new BSModalBorder("previewModal");
    private final Form<?> parametersForm = new Form<>("parametersForm");

    private final BSLabel formName = new BSLabel("formLabelName"),
            previewName = new BSLabel("previewName");
    private BSGrid container = new BSGrid("generated");

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
        parametersModal.setSize(BSModalBorder.Size.FIT);
        parametersModal.setTitleText(getMessage("label.table.column.params"));

        queue(parametersForm);
        queue(parametersModal);

        parametersModal.queue(formName);

        previewModal.setSize(BSModalBorder.Size.FULL);
        previewModal.setTitleText(getMessage("label.table.column.preview"));

        previewModal.queue(container);
        previewModal.queue(previewName);

        queue(previewModal);
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
                                .appendAction(getMessage("label.table.column.params"),
                                        Icone.COGS, this::openParameterModal
                                )
                )
                .appendColumn(new BSActionColumn<FormVO, String>(WicketUtils.$m.ofValue(""))
                                .appendAction(getMessage("label.table.column.preview"),
                                        Icone.EYE, this::openPreviewModal
                                )
                )
                .setRowsPerPage(Long.MAX_VALUE) //TODO: proper pagination
                .build("form-list");
    }

    private void openParameterModal(AjaxRequestTarget target, IModel<FormVO> model) {
        FormVO form = model.getObject();
        formName.setDefaultModel(form);
        parametersModal.show(target);
    }

    private void openPreviewModal(AjaxRequestTarget target, IModel<FormVO> model) {
        FormVO form = model.getObject();
        updateContainer(form);
        previewName.setDefaultModel(form);
        target.appendJavaScript("Metronic.init();Page.init();");
        previewModal.show(target);
    }

    private void updateContainer(FormVO form) {
        previewModal.remove(container);
        container = new BSGrid("generated");
        previewModal.queue(container);
        buildContainer(form.getType());
    }

    private void buildContainer(MTipo<?> formType) {
        WicketBuildContext ctx = new WicketBuildContext(container.newColInRow());
        IModel<MInstancia> mInstance = new MInstanceRootModel<MInstancia>(formType.novaInstancia());
        UIBuilderWicket.buildForEdit(ctx, mInstance);
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