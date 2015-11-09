package br.net.mirante.singular.view.page.form.crud;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import javax.inject.Inject;

import org.apache.wicket.Component;
import org.apache.wicket.MarkupContainer;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.form.AjaxButton;
import org.apache.wicket.behavior.Behavior;
import org.apache.wicket.feedback.FencedFeedbackPanel;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.form.ChoiceRenderer;
import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.panel.Fragment;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.ResourceModel;

import br.net.mirante.singular.dao.form.ExampleDataDAO;
import br.net.mirante.singular.dao.form.ExampleDataDTO;
import br.net.mirante.singular.dao.form.TemplateRepository;
import br.net.mirante.singular.form.mform.MDicionario;
import br.net.mirante.singular.form.mform.MIComposto;
import br.net.mirante.singular.form.mform.MTipo;
import br.net.mirante.singular.form.mform.MTipoComposto;
import br.net.mirante.singular.form.mform.io.MformPersistenciaXML;
import br.net.mirante.singular.form.util.xml.MElement;
import br.net.mirante.singular.form.util.xml.MParser;
import br.net.mirante.singular.form.validation.InstanceValidationContext;
import br.net.mirante.singular.form.validation.ValidationErrorLevel;
import br.net.mirante.singular.form.wicket.UIBuilderWicket;
import br.net.mirante.singular.form.wicket.WicketBuildContext;
import br.net.mirante.singular.form.wicket.model.MInstanciaRaizModel;
import br.net.mirante.singular.form.wicket.validation.InstanceValidationUtils;
import br.net.mirante.singular.util.wicket.bootstrap.layout.BSGrid;
import br.net.mirante.singular.util.wicket.datatable.BSDataTable;
import br.net.mirante.singular.util.wicket.datatable.BSDataTableBuilder;
import br.net.mirante.singular.util.wicket.datatable.BaseDataProvider;
import br.net.mirante.singular.util.wicket.datatable.column.BSActionColumn;
import br.net.mirante.singular.util.wicket.feedback.BSFeedbackPanel;
import br.net.mirante.singular.util.wicket.modal.BSModalBorder;
import br.net.mirante.singular.util.wicket.resource.Icone;
import br.net.mirante.singular.util.wicket.util.WicketUtils;
import br.net.mirante.singular.view.SingularWicketContainer;
import br.net.mirante.singular.view.page.form.FormVO;
import br.net.mirante.singular.view.template.Content;

@SuppressWarnings("serial")
public class CrudContent extends Content implements SingularWicketContainer<CrudContent, Void> {

    private static final MDicionario dicionario = TemplateRepository.dicionario();

    private BSDataTable<ExampleDataDTO, String> listTable;
    private List<ExampleDataDTO> dataList = new LinkedList<>();
    transient private MTipoComposto<?> selectedTemplate;

    private final BSModalBorder inputModal = new BSModalBorder("inputModal"),
            deleteModal = new BSModalBorder("deleteModal");
    private BSGrid container = new BSGrid("generated");
    private Form<?> inputForm = new Form<>("save-form"),
            deleteForm = new Form<>("delete-form");

    @Inject
    ExampleDataDAO dao;

    IModel<MIComposto> currentInstance;
    ExampleDataDTO currentModel;

    public CrudContent(String id) {
        super(id, false, true);
    }

    protected void onInitialize() {
        super.onInitialize();
        queue(new BSFeedbackPanel("feedback"));
        Form<Object> optionsForm = new Form<>("optionsForm");
        optionsForm.queue(setUpTemplatesOptions());
        queue(optionsForm);
        queue(setUpInsertButton());
        listTable = setupDataTable();
        queue(listTable);
        queue(setupInputModal());
        deleteModal.queue(deleteForm.queue(new AjaxButton("delete-btn") {
            protected void onSubmit(AjaxRequestTarget target, Form<?> form) {
                dao.remove(currentModel);
                currentModel = null;
                updateListTableFromModal(target);
                deleteModal.hide(target);
            }
        }));
        queue(deleteModal);
    }

    @SuppressWarnings({"rawtypes", "unchecked"})
    private DropDownChoice setUpTemplatesOptions() {
        List<SelectOption> options = TemplateRepository.formTemplates().stream()
                .map(t -> new SelectOption(t.getNomeSimples(), new FormVO(t.getNomeSimples(), t)))
                .collect(Collectors.toList());

        ChoiceRenderer choiceRenderer = new ChoiceRenderer("key", "key");
        return new DropDownChoice<SelectOption>("options",
                new SelectOption(null, null), options, choiceRenderer) {
            @Override
            protected boolean wantOnSelectionChangedNotifications() {
                return true;
            }

            @Override
            protected void onSelectionChanged(SelectOption newSelection) {
                FormVO value = (FormVO) newSelection.getValue();
                selectedTemplate = value.getValue();
                updateDataList();
            }

        };
    }

    private void updateDataList() {
        dataList = dao.list(selectedTemplate.getNome());
    }

    private MarkupContainer setUpInsertButton() {
        return new Form<>("form").add(new AjaxButton("insert") {
            @SuppressWarnings("unchecked")
            protected void onSubmit(AjaxRequestTarget target, Form<?> form) {
                ExampleDataDTO model = new ExampleDataDTO(UUID.randomUUID().toString());
                model.setType(selectedTemplate.getNome());
                openInputModal(target, model);
            }

            public boolean isVisible() {
                return selectedTemplate != null;
            }

        });
    }

    private BSDataTable<ExampleDataDTO, String> setupDataTable() {
        return new BSDataTableBuilder<>(createDataProvider())
                .appendPropertyColumn(getMessage("label.table.column.key"),
                        "key", ExampleDataDTO::getKey)
                .appendColumn(new BSActionColumn<ExampleDataDTO, String>(WicketUtils.$m.ofValue(""))
                        .appendAction(getMessage("label.table.column.edit"),
                                Icone.PENCIL_SQUARE, this::openInputModal
                        )
                )
                .appendColumn(new BSActionColumn<ExampleDataDTO, String>(WicketUtils.$m.ofValue(""))
                        .appendAction(getMessage("label.table.column.delete"),
                                Icone.MINUS, this::deleteSelected
                        )
                )
                .setRowsPerPage(Long.MAX_VALUE) //TODO: proper pagination
                .build("data-list");
    }

    private BaseDataProvider<ExampleDataDTO, String> createDataProvider() {
        return new BaseDataProvider<ExampleDataDTO, String>() {

            public long size() {
                return dataList.size();
            }

            public Iterator<? extends ExampleDataDTO> iterator(int first, int count,
                    String sortProperty, boolean ascending) {
                return dataList.iterator();
            }
        };
    }

    @SuppressWarnings("unchecked")
    private void openInputModal(AjaxRequestTarget target, IModel<ExampleDataDTO> model) {
        currentModel = model.getObject();
        createInstance(selectedTemplate.getNome());
        updateContainer();
        target.appendJavaScript("Metronic.init();Page.init();");
        inputModal.show(target);
    }

    @SuppressWarnings("unchecked")
    private void createInstance(String nomeDoTipo) {
        currentInstance = new MInstanciaRaizModel<MIComposto>() {
            protected MTipo<MIComposto> getTipoRaiz() {
                return (MTipo<MIComposto>) dicionario.getTipo(nomeDoTipo);
            }
        };
        populateInstance((MTipo<MIComposto>) dicionario.getTipo(nomeDoTipo));

    }

    private void populateInstance(final MTipo<MIComposto> tipo) {
        if (currentModel.getXml() == null)
            return;
        try {
            MElement xml = MParser.parse(currentModel.getXml());
            MIComposto instance = MformPersistenciaXML.fromXML(tipo, xml);
            currentInstance = new MInstanciaRaizModel<MIComposto>(instance) {
                protected MTipo<MIComposto> getTipoRaiz() {
                    return (MTipo<MIComposto>) instance.getMTipo();
                }
            };
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private void updateContainer() {
        inputForm.remove(container);
        container = new BSGrid("generated");
        inputForm.queue(container);
        buildContainer();
    }

    private void buildContainer() {
        WicketBuildContext ctx = new WicketBuildContext(container.newColInRow());
        UIBuilderWicket.buildForEdit(ctx, currentInstance);
    }

    private BSModalBorder setupInputModal() {
        inputModal.setSize(BSModalBorder.Size.FULL);
        inputModal.setTitleText(getMessage("label.form.title"));

        inputModal.queue(inputForm
                .queue(new FencedFeedbackPanel("feedback", inputForm)
                        .add(new Behavior() {
                            @Override
                            public void onConfigure(Component component) {
                                component.setVisible(((FencedFeedbackPanel) component).anyMessage());
                            }
                        }))
                .queue(new AjaxButton("save-btn") {
                    @Override
                    protected void onSubmit(AjaxRequestTarget target, Form<?> form) {
                        StringWriter buffer = new StringWriter();
                        MIComposto trueInstance = currentInstance.getObject();

                        InstanceValidationContext validationContext = new InstanceValidationContext(trueInstance);
                        InstanceValidationUtils.associateErrorsToComponents(validationContext, form);

                        if (validationContext.hasErrorsAboveLevel(ValidationErrorLevel.WARNING)) {
                            target.add(form);
                            return;
                        }

                        MformPersistenciaXML.toXML(trueInstance).printTabulado(
                                new PrintWriter(buffer));
                        currentModel.setXml(buffer.toString());
                        dao.save(currentModel);
                        updateListTableFromModal(target);
                        inputModal.hide(target);
                    }
                }));

        return inputModal;
    }

    private void deleteSelected(AjaxRequestTarget target, IModel<ExampleDataDTO> model) {
        currentModel = model.getObject();
        deleteModal.show(target);
    }

    private void updateListTableFromModal(AjaxRequestTarget target) {
        updateDataList();
        target.add(listTable);
    }

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
