package br.net.mirante.singular.showcase.view.page.form.crud;

import static br.net.mirante.singular.util.wicket.util.WicketUtils.*;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

import javax.inject.Inject;

import br.net.mirante.singular.form.mform.SType;
import br.net.mirante.singular.form.mform.STypeComposite;
import br.net.mirante.singular.form.mform.core.annotation.AtrAnnotation;
import br.net.mirante.singular.showcase.component.CaseBase;
import br.net.mirante.singular.showcase.component.ShowCaseTable;
import org.apache.commons.lang3.StringUtils;
import org.apache.wicket.MarkupContainer;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.form.AjaxButton;
import org.apache.wicket.extensions.markup.html.repeater.data.table.IColumn;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.form.ChoiceRenderer;
import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.panel.Fragment;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.ResourceModel;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.apache.wicket.util.string.StringValue;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.SAXException;

import br.net.mirante.singular.form.mform.SPackage;
import br.net.mirante.singular.form.util.xml.MElement;
import br.net.mirante.singular.form.util.xml.MParser;
import br.net.mirante.singular.form.wicket.component.BFModalBorder;
import br.net.mirante.singular.form.wicket.enums.ViewMode;
import br.net.mirante.singular.form.wicket.feedback.SFeedbackPanel;
import br.net.mirante.singular.form.wicket.mapper.selection.SelectOption;
import br.net.mirante.singular.showcase.dao.form.ExampleDataDAO;
import br.net.mirante.singular.showcase.dao.form.ExampleDataDTO;
import br.net.mirante.singular.showcase.dao.form.FileDao;
import br.net.mirante.singular.showcase.dao.form.TemplateRepository;
import br.net.mirante.singular.showcase.dao.form.TemplateRepository.TemplateEntry;
import br.net.mirante.singular.showcase.view.SingularWicketContainer;
import br.net.mirante.singular.showcase.view.page.form.FormVO;
import br.net.mirante.singular.showcase.view.template.Content;
import br.net.mirante.singular.util.wicket.datatable.BSDataTable;
import br.net.mirante.singular.util.wicket.datatable.BSDataTableBuilder;
import br.net.mirante.singular.util.wicket.datatable.BaseDataProvider;
import br.net.mirante.singular.util.wicket.datatable.column.BSActionColumn;
import br.net.mirante.singular.util.wicket.output.BOutputPanel;
import br.net.mirante.singular.util.wicket.resource.Icone;
import br.net.mirante.singular.util.wicket.tab.BSTabPanel;
import static br.net.mirante.singular.util.wicket.util.WicketUtils.$m;

@SuppressWarnings("serial")
public class CrudContent extends Content
    implements SingularWicketContainer<CrudContent, Void> {

    private final static Logger LOGGER = LoggerFactory.getLogger(CrudContent.class);

    private BSDataTable<ExampleDataDTO, String> listTable;
    private List<ExampleDataDTO>                dataList = new LinkedList<>();
    private FormVO                              selectedTemplate;

    private final BFModalBorder deleteModal  = new BFModalBorder("deleteModal");
    private final BFModalBorder viewXmlModal = new BFModalBorder("viewXmlModal");
    private Form<?>             deleteForm   = new Form<>("delete-form");

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
        if (!pType.isEmpty()) {
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
        queue(new SFeedbackPanel("feedback", this));
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

    @SuppressWarnings({ "rawtypes", "unchecked" })
    private DropDownChoice setUpTemplatesOptions() {
        List<SelectOption> options = TemplateRepository.get().getEntries().stream()
            .map(t -> new SelectOption(t.getDisplayName(), new FormVO(t)))
            .collect(Collectors.toList());

        ChoiceRenderer choiceRenderer = new ChoiceRenderer("selectLabel", "value");
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
                setResponsePage(FormPage.class, params);
            }

            @Override
            public boolean isVisible() {
                return selectedTemplate != null && selectedTemplate.getKey() != null;
            }

        });
    }

    private BSDataTable<ExampleDataDTO, String> setupDataTable() {
        updateDataList();
        BSDataTableBuilder<ExampleDataDTO, String, IColumn<ExampleDataDTO, String>> builder = new BSDataTableBuilder<>(createDataProvider());
        builder
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
                                .add(FormPage.VIEW_MODE, ViewMode.EDITION));
                    }))
            .appendColumn(new BSActionColumn<ExampleDataDTO, String>($m.ofValue(""))
                .appendAction(getMessage("label.table.column.visualizar"),
                    Icone.EYE,
                    (target, model) -> {
                        setResponsePage(FormPage.class,
                            new PageParameters()
                                .add(FormPage.TYPE_NAME, selectedTemplate.getTypeName())
                                .add(FormPage.MODEL_KEY, model.getObject().getKey())
                                .add(FormPage.VIEW_MODE, ViewMode.VISUALIZATION));
                    }));
        addAnnotationColumnIfNeeded(builder);
        builder.appendColumn(new BSActionColumn<ExampleDataDTO, String>($m.ofValue(""))
                .appendAction(getMessage("label.table.column.delete"),
                    Icone.MINUS, this::deleteSelected))
            .appendColumn(new BSActionColumn<ExampleDataDTO, String>($m.ofValue(""))
                .appendAction(getMessage("label.table.column.visualizar.xml"),
                    Icone.EYE, this::viewXml))
            .setRowsPerPage(Long.MAX_VALUE); //TODO: proper pagination
        return builder.build("data-list");
    }

    private void addAnnotationColumnIfNeeded(BSDataTableBuilder<ExampleDataDTO, String, IColumn<ExampleDataDTO, String>> builder) {
        builder.appendColumn(new BSActionColumn<ExampleDataDTO, String>($m.ofValue("")){
                    @Override
                    public String getCssClass() {
                        return hasAnnotations() ? "" : "hidden";
                    }
                }
            .appendAction(getMessage("label.table.column.analisar"),
                Icone.COMMENT,
                (target, model) -> {
                    setResponsePage(FormPage.class,
                        new PageParameters()
                            .add(FormPage.TYPE_NAME, selectedTemplate.getTypeName())
                            .add(FormPage.MODEL_KEY, model.getObject().getKey())
                            .add(FormPage.VIEW_MODE, ViewMode.VISUALIZATION)
                            .add(FormPage.ANNOTATION_ENABLED, true));
                })
        );
    }

    private boolean hasAnnotations() {
        boolean hasAnntations = false;
        if(selectedTemplate.getType() != null && selectedTemplate.getType() instanceof STypeComposite){
            STypeComposite type = (STypeComposite) selectedTemplate.getType();
            for(SType<?> i : (Collection<SType<?>>)type.getFields()){
                hasAnntations |= i.as(AtrAnnotation::new).isAnnotated();
            }
        }
        return hasAnntations;
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

        final String xmlPersistencia = model.getObject().getXml();
        final String xmlTabulado = getXmlTabulado(xmlPersistencia);
        final String definicao = getDefinicao(model.getObject().getType());

        final BSTabPanel xmlTabs = new BSTabPanel("xmlTabs");
        xmlTabs.addTab(getString("label.xml.tabulado"), new BOutputPanel(BSTabPanel.getTabPanelId(), $m.ofValue(xmlTabulado)));
        xmlTabs.addTab(getString("label.xml.persistencia"), new BOutputPanel(BSTabPanel.getTabPanelId(), $m.ofValue(xmlPersistencia)));
        xmlTabs.addTab(getString("label.definicao"), new BOutputPanel(BSTabPanel.getTabPanelId(), $m.ofValue(definicao)));

        viewXmlModal.addOrReplace(xmlTabs);
        viewXmlModal.show(target);
        viewXmlModal.setSize(BFModalBorder.Size.LARGE);
    }

    private String getXmlTabulado(String xmlString) {
        if (!xmlString.isEmpty()) {
            try {
                final MElement xml = MParser.parse(xmlString);
                final StringWriter sw = new StringWriter();
                final PrintWriter writer = new PrintWriter(sw);
                xml.printTabulado(writer);
                return sw.toString();
            } catch (SAXException | IOException e) {
                LOGGER.error(e.getMessage(), e);
            }
        }
        return StringUtils.EMPTY;
    }

    private String getDefinicao(String typeName) {
        final SPackage pacote = TemplateRepository.get().loadType(typeName).getPacote();
        StringBuilder definicaoOutput = new StringBuilder();
        pacote.debug(definicaoOutput);
        return definicaoOutput.toString();
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
