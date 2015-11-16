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
import br.net.mirante.singular.form.mform.MInstancia;
import br.net.mirante.singular.form.mform.MTipo;
import br.net.mirante.singular.form.mform.ServiceRef;
import br.net.mirante.singular.form.mform.core.attachment.IAttachmentPersistenceHandler;
import br.net.mirante.singular.form.mform.core.attachment.handlers.FileSystemAttachmentHandler;
import br.net.mirante.singular.form.mform.io.MformPersistenciaXML;
import br.net.mirante.singular.form.util.xml.MElement;
import br.net.mirante.singular.form.util.xml.MParser;
import br.net.mirante.singular.form.validation.InstanceValidationContext;
import br.net.mirante.singular.form.validation.ValidationErrorLevel;
import br.net.mirante.singular.form.wicket.UIBuilderWicket;
import br.net.mirante.singular.form.wicket.WicketBuildContext;
import br.net.mirante.singular.form.wicket.model.MInstanceRootModel;
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
    private BSDataTable<ExampleDataDTO, String> listTable;
    private List<ExampleDataDTO> dataList = new LinkedList<>();
    private FormVO selectedTemplate;

    private final BSModalBorder inputModal = new BSModalBorder("inputModal"),
            deleteModal = new BSModalBorder("deleteModal");
    private BSGrid container = new BSGrid("generated");
    private Form<?> inputForm = new Form<>("save-form"),
            deleteForm = new Form<>("delete-form");

    @Inject
    ExampleDataDAO dao;

    IModel<MInstancia> currentInstance;
    ExampleDataDTO currentModel;

    public CrudContent(String id) {
        super(id, false, true);
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
        add(setupInputModal());
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
    }

    @SuppressWarnings({"rawtypes", "unchecked"})
    private DropDownChoice setUpTemplatesOptions() {
        List<SelectOption> options = TemplateRepository.get().getEntries().stream()
                .map(t -> new SelectOption(t.getDisplayName(), new FormVO(t)))
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
            @SuppressWarnings("unchecked")
            protected void onSubmit(AjaxRequestTarget target, Form<?> form) {
                ExampleDataDTO model = new ExampleDataDTO(UUID.randomUUID().toString());
                model.setType(selectedTemplate.getTypeName());
                openInputModal(target, model);
            }

            @Override
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

    private void openInputModal(AjaxRequestTarget target, IModel<ExampleDataDTO> model) {
        currentModel = model.getObject();
        createInstance(selectedTemplate.getTypeName());
        updateContainer();
        target.appendJavaScript("Metronic.init();Page.init();");
        inputModal.show(target);
    }

    private static final ServiceRef<IAttachmentPersistenceHandler> persistanceRef = 
	    new ServiceRef<IAttachmentPersistenceHandler>() {
	public IAttachmentPersistenceHandler get() {
	    return new FileSystemAttachmentHandler("/tmp");
	}
    };
    
    private void createInstance(String nomeDoTipo) {
        MTipo<?> tipo = TemplateRepository.get().loadType(nomeDoTipo);
        currentInstance = new MInstanceRootModel<MInstancia>(tipo.novaInstancia());
	currentInstance.getObject().getDocument().setAttachmentPersistenceHandler(persistanceRef);
        populateInstance(tipo);

    }

    private void populateInstance(final MTipo<?> tipo) {
        if (currentModel.getXml() == null)
            return;
        try {
            MElement xml = MParser.parse(currentModel.getXml());
            MInstancia instance = MformPersistenciaXML.fromXML(tipo, xml);
            currentInstance = new MInstanceRootModel<MInstancia>(instance);
            currentInstance.getObject().getDocument().setAttachmentPersistenceHandler(persistanceRef);
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

        inputModal.add(inputForm
                .add(new FencedFeedbackPanel("feedback", inputForm)
                        .add(new Behavior() {
                            @Override
                            public void onConfigure(Component component) {
                                component.setVisible(((FencedFeedbackPanel) component).anyMessage());
                            }
                        }))
                .add(new SaveButton("save-btn"))
                .add(new AjaxButton("cancel-btn"){
                    protected void onSubmit(AjaxRequestTarget target, Form<?> form) {
                	inputModal.hide(target);
                    }
                })
                
        )
    	;

        return inputModal;
    }
    
    private final class SaveButton extends AjaxButton {
	private SaveButton(String id) {
	    super(id);
	}

	@Override
	protected void onSubmit(AjaxRequestTarget target, Form<?> form) {
	    MInstancia trueInstance = currentInstance.getObject();
	    MElement rootXml = MformPersistenciaXML.toXML(trueInstance);

	    try {
		addValidationErrors(target, form, trueInstance, rootXml);
	    } catch (Exception e) {
		target.add(form);
		return;
	    }
	    currentModel.setXml(printXml(rootXml));
	    dao.save(currentModel);
	    updateListTableFromModal(target);
	    inputModal.hide(target);
	}

	private void addValidationErrors(AjaxRequestTarget target, Form<?> form, MInstancia trueInstance,
		MElement rootXml) throws Exception {
	    runDefaultValidators(form, trueInstance);
	    validateEmptyForm(form, rootXml);
	}

	private void validateEmptyForm(Form<?> form, MElement rootXml) {
	    if (rootXml == null) {
		form.error(getMessage("form.message.empty").getString());
		throw new RuntimeException("Has empty form");
	    }
	}

	private void runDefaultValidators(Form<?> form, MInstancia trueInstance) {
	    InstanceValidationContext validationContext = new InstanceValidationContext(trueInstance);
	    InstanceValidationUtils.associateErrorsToComponents(validationContext, form);

	    if (validationContext.hasErrorsAboveLevel(ValidationErrorLevel.WARNING)) {
		throw new RuntimeException("Has form errors");
	    }
	}

	private String printXml(MElement rootXml) {
	    StringWriter buffer = new StringWriter();
	    rootXml.printTabulado(new PrintWriter(buffer));
	    String xml = buffer.toString();
	    return xml;
	}
    }

    private void deleteSelected(AjaxRequestTarget target, IModel<ExampleDataDTO> model) {
        currentModel = model.getObject();
        deleteModal.show(target);
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
