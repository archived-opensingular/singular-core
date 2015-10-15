package br.net.mirante.singular.view.page.form;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.panel.Fragment;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.ResourceModel;

import br.net.mirante.singular.dao.form.TemplateRepository;
import br.net.mirante.singular.form.mform.MDicionario;
import br.net.mirante.singular.form.mform.MIComposto;
import br.net.mirante.singular.form.mform.MTipo;
import br.net.mirante.singular.form.mform.MTipoComposto;
import br.net.mirante.singular.form.wicket.UIBuilderWicket;
import br.net.mirante.singular.form.wicket.WicketBuildContext;
import br.net.mirante.singular.form.wicket.model.MInstanciaRaizModel;
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
class ListContent extends Content implements SingularWicketContainer<FormContent, Void> {

	final static List<FormVO> formTypes ;
	static final MDicionario dicionario ;
	
	private final BSModalBorder parametersModal = new BSModalBorder("parametersModal"),
								previewModal = new BSModalBorder("previewModal");
	private final Form<?> parametersForm = new Form<>("parametersForm");
	
	private final Collection<FieldVO> fields = new ArrayList<>();
	private final BSLabel formName = new BSLabel("formLabelName"),
						  previewName = new BSLabel("previewName");
	private BSGrid container = new BSGrid("generated");
	
	
	static {
		dicionario = TemplateRepository.dicionario();
		formTypes = TemplateRepository.formTemplates().stream().map( t -> {
			return new FormVO(t.getNomeSimples(), t);
		}).collect(Collectors.toList());
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
		
		
		BaseDataProvider<FieldVO, String> provider_ = 
				new BaseDataProvider<FieldVO, String>() {
			public long size() {
				return fields.size();
			}

			public Iterator<? extends FieldVO> iterator(int first, int count, 
					String sortProperty, boolean ascending) {
				return fields.iterator();
			}
		};
		parametersModal.queue(new BSDataTableBuilder<>(provider_)
				.appendPropertyColumn(getMessage("label.table.column.field"), 
						"name", FieldVO::getName)
				.appendPropertyColumn(getMessage("label.table.column.type"), 
						"type", FieldVO::getType)
				.setRowsPerPage(Long.MAX_VALUE) //TODO: proper pagination
				.build("parameters-list")
				);
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

			public long size() {
				return formTypes.size();
			}

			public Iterator<? extends FormVO> iterator(int first, int count, 
					String sortProperty, boolean ascending) {
				return formTypes.iterator();
			}
		};
		
		BSDataTable<FormVO, String> formDataTable = new BSDataTableBuilder<>(provider)
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
		return formDataTable;
	}

	private void openParameterModal(AjaxRequestTarget target, IModel<FormVO> model) {
		FormVO form = model.getObject(); 
		updateFields(form);
		formName.setDefaultModel(form);
		parametersModal.show(target);
	}

	private FormVO updateFields(FormVO form) {
		MTipoComposto<?> formType = form.getValue();
		fields.clear();
		if(formType != null){
			fields.addAll(convertCampos2FieldVO(formType)); 
		}
		return form;
	}

	private List<FieldVO> convertCampos2FieldVO(MTipoComposto<?> formType) {
		LinkedList<FieldVO> fields = new LinkedList<>();
		addAllFields(formType, fields);
		return fields;
	}

	private void addAllFields(MTipoComposto<?> formType, LinkedList<FieldVO> fields) {
		formType.getCampos().forEach( t -> {
			MTipo<?> campo = formType.getCampo(t);
			fields.add(new FieldVO(t, campo.getClasseInstancia().getName()));
			if(campo instanceof MTipoComposto){
				addAllFields((MTipoComposto<?>) campo, fields);
			}
		});
	}
	
	private void openPreviewModal(AjaxRequestTarget target, IModel<FormVO> model) {
		FormVO form = model.getObject();
		updateContainer(form);
		previewName.setDefaultModel(form);
		previewModal.show(target);
	}

	private void updateContainer(FormVO form) {
		previewModal.remove(container);
		container = new BSGrid("generated");
		previewModal.queue(container);
		buildContainer(form.getValue());
	}

	private void buildContainer(MTipoComposto<?> formType) {
		WicketBuildContext ctx = new WicketBuildContext(container.newColInRow());
		IModel<MIComposto> mInstance = new MInstanciaRaizModel<MIComposto>() {
		    @SuppressWarnings("unchecked")
			protected MTipo<MIComposto> getTipoRaiz() {
		        return (MTipo<MIComposto>) dicionario.getTipo(formType.getNome());
		    }
		};
		UIBuilderWicket.buildForEdit(ctx, mInstance);
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