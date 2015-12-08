package br.net.mirante.singular.view.page.form.crud;

import br.net.mirante.singular.dao.form.ExampleDataDAO;
import br.net.mirante.singular.dao.form.ExampleDataDTO;
import br.net.mirante.singular.dao.form.FileDao;
import br.net.mirante.singular.dao.form.TemplateRepository;
import br.net.mirante.singular.dao.form.TemplateRepository.TemplateEntry;
import br.net.mirante.singular.form.mform.MPacote;
import br.net.mirante.singular.form.wicket.mapper.SelectOption;
import br.net.mirante.singular.util.wicket.datatable.BSDataTable;
import br.net.mirante.singular.util.wicket.datatable.BSDataTableBuilder;
import br.net.mirante.singular.util.wicket.datatable.BaseDataProvider;
import br.net.mirante.singular.util.wicket.datatable.column.BSActionColumn;
import br.net.mirante.singular.util.wicket.feedback.BSFeedbackPanel;
import br.net.mirante.singular.util.wicket.modal.BSModalBorder;
import br.net.mirante.singular.util.wicket.resource.Icone;
import br.net.mirante.singular.view.SingularWicketContainer;
import br.net.mirante.singular.view.page.form.FormVO;
import br.net.mirante.singular.view.template.Content;
import org.apache.wicket.MarkupContainer;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.form.AjaxButton;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.ChoiceRenderer;
import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.panel.Fragment;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.ResourceModel;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.apache.wicket.util.string.StringValue;

import javax.inject.Inject;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

import static br.net.mirante.singular.util.wicket.util.WicketUtils.$m;

@SuppressWarnings("serial")
public class CrudContent extends Content
        implements SingularWicketContainer<CrudContent, Void> {

    private BSDataTable<ExampleDataDTO, String> listTable;
    private List<ExampleDataDTO> dataList = new LinkedList<>();
    private FormVO selectedTemplate;

    private final BSModalBorder deleteModal = new BSModalBorder("deleteModal");
    private final BSModalBorder viewXmlModal = new BSModalBorder("viewXmlModal");
    private Form<?> deleteForm = new Form<>("delete-form");

    @Inject
    ExampleDataDAO dao;

    @Inject
    FileDao filePersistence;

    private ExampleDataDTO currentModel;

    public CrudContent(String id, StringValue pType) {
        super(id, false, true);
        setActiveTemplate(pType);
    }

    private void setActiveTemplate(StringValue pType) {
        if(!pType.isEmpty()){
            String strType = pType.toString();
            TemplateEntry t = TemplateRepository.get().findEntryByType(strType);
            selectedTemplate = new FormVO(t);
        } else {
            selectedTemplate = new FormVO(null, null);
        }
    }

    @Override
    protected void onInitialize() {
        super.onInitialize();
        queue(new BSFeedbackPanel("feedback"));
        Form<Object> optionsForm = new Form<>("optionsForm");
        optionsForm.queue(setUpTemplatesOptions());
        queue(optionsForm);
        queue(setUpInsertButton());
        listTable = setupDataTable();
        queue(listTable);
        deleteModal.queue(deleteForm.queue(new AjaxButton("delete-btn") {
            @Override
            protected void onSubmit(AjaxRequestTarget target, Form<?> form) {
                dao.remove(currentModel);
                currentModel = null;
                updateListTableFromModal(target);
                deleteModal.hide(target);
            }
        }));
        queue(deleteModal);
        queue(viewXmlModal);
    }

    @SuppressWarnings({"rawtypes", "unchecked"})
    private DropDownChoice setUpTemplatesOptions() {
        List<SelectOption> options = TemplateRepository.get().getEntries().stream()
                .map(t -> new SelectOption(t.getDisplayName(), new FormVO(t)))
                .collect(Collectors.toList());

        ChoiceRenderer choiceRenderer = new ChoiceRenderer("key", "key");
        return new DropDownChoice<SelectOption>("options",
                new SelectOption(selectedTemplate.getKey(), selectedTemplate),
                options, choiceRenderer) {
            @Override
            protected boolean wantOnSelectionChangedNotifications() {
                return true;
            }

            @Override
            protected void onSelectionChanged(SelectOption newSelection) {
                FormVO value = (FormVO) newSelection.getValue();
                selectedTemplate = value;
                updateDataList();
            }

        };
    }

    private void updateDataList() {
        dataList = dao.list(selectedTemplate.getTypeName());
    }

    private MarkupContainer setUpInsertButton() {
        return new Form<>("form").add(new AjaxButton("insert") {
            @Override
            protected void onSubmit(AjaxRequestTarget target, Form<?> form) {
                PageParameters params = new PageParameters().add(
                    FormPage.TYPE_NAME, selectedTemplate.getTypeName());
                setResponsePage(FormPage.class,params);
            }

            @Override
            public boolean isVisible() {
                return selectedTemplate != null && selectedTemplate.getKey() != null;
            }

        });
    }

    private BSDataTable<ExampleDataDTO, String> setupDataTable() {
        updateDataList();
        return new BSDataTableBuilder<>(createDataProvider())
                .appendPropertyColumn(getMessage("label.table.column.key"),
                        "key", ExampleDataDTO::getKey)
                .appendColumn(new BSActionColumn<ExampleDataDTO, String>($m.ofValue(""))
                        .appendAction(getMessage("label.table.column.edit"),
                                Icone.PENCIL_SQUARE,
                                (target, model) -> {
                                    setResponsePage(FormPage.class,
                                            new PageParameters()
                                                    .add(FormPage.TYPE_NAME, selectedTemplate.getTypeName())
                                                    .add(FormPage.MODEL_KEY, model.getObject().getKey())
                                    );
                                }
                        )
                )
                .appendColumn(new BSActionColumn<ExampleDataDTO, String>($m.ofValue(""))
                        .appendAction(getMessage("label.table.column.delete"),
                                Icone.MINUS, this::deleteSelected
                        )
                )
                .appendColumn(new BSActionColumn<ExampleDataDTO, String>($m.ofValue(""))
                        .appendAction(getMessage("label.table.column.visualizar.xml"),
                                Icone.EYE, this::viewXml
                        )
                )
                .setRowsPerPage(Long.MAX_VALUE) //TODO: proper pagination
                .build("data-list");
    }

    private BaseDataProvider<ExampleDataDTO, String> createDataProvider() {
        return new BaseDataProvider<ExampleDataDTO, String>() {

            @Override
            public long size() {
                return dataList.size();
            }

            @Override
            public Iterator<? extends ExampleDataDTO> iterator(int first, int count,
                    String sortProperty, boolean ascending) {
                return dataList.iterator();
            }
        };
    }

    private void deleteSelected(AjaxRequestTarget target, IModel<ExampleDataDTO> model) {
        currentModel = model.getObject();
        deleteModal.show(target);
    }

    private void viewXml(AjaxRequestTarget target, IModel<ExampleDataDTO> model) {

        MPacote pacote = TemplateRepository.get().loadType(model.getObject().getType()).getPacote();

        StringBuilder sb = new StringBuilder();
        pacote.debug(sb);

        viewXmlModal.addOrReplace(new Label("xmlCode", $m.property(model, "xml")));
        viewXmlModal.addOrReplace(new Label("definicao", sb.toString()));
        viewXmlModal.show(target);
        viewXmlModal.setSize(BSModalBorder.Size.FIT);
    }

    private void updateListTableFromModal(AjaxRequestTarget target) {
        updateDataList();
        target.add(listTable);
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
