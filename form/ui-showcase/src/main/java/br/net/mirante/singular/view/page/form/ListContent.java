package br.net.mirante.singular.view.page.form;

import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.panel.Fragment;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.ResourceModel;
import org.reflections.Reflections;

import br.net.mirante.singular.form.mform.MDicionario;
import br.net.mirante.singular.form.mform.MIComposto;
import br.net.mirante.singular.form.mform.MPacote;
import br.net.mirante.singular.form.mform.MTipo;
import br.net.mirante.singular.form.mform.MTipoComposto;
import br.net.mirante.singular.form.mform.exemplo.curriculo.MPacoteCurriculo;
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
import br.net.mirante.singular.view.page.form.examples.ExamplePackage;
import br.net.mirante.singular.view.template.Content;

@SuppressWarnings("serial")
class ListContent extends Content implements SingularWicketContainer<FormContent, Void> {

	final static List<FormVO> formTypes ;
	
	private final BSModalBorder parametersModal = new BSModalBorder("parametersModal"),
								previewModal = new BSModalBorder("previewModal");
	private final Form<?> parametersForm = new Form<>("parametersForm");
	
	private final Collection<FieldVO> fields = new ArrayList<>();
	private final BSLabel formName = new BSLabel("formLabelName"),
						  previewName = new BSLabel("previewName");
	private BSGrid container = new BSGrid("generated");
	
	private static final MDicionario dicionario = MDicionario.create();
	
	static{
		dicionario.carregarPacote(MPacoteCurriculo.class);
		dicionario.carregarPacote(ExamplePackage.class);
//		loadAllPackages(dicionario);
//		formTypes = dicionario.getTipos().stream()
//			.filter( t -> {
//				return t instanceof MTipoComposto;
//			})
//			.map(t -> {
//				return new FormVO(t.getNomeSimples(), (MTipoComposto)t);
//			})
//			.collect(Collectors.toList());
		formTypes = new LinkedList<FormVO>(){{
			add(new FormVO("Currículo",(MTipoComposto<?>) dicionario.getTipo(MPacoteCurriculo.TIPO_CURRICULO)));
			add(new FormVO("Pedido",(MTipoComposto<?>) dicionario.getTipo(ExamplePackage.Types.ORDER.name)));
		}};
	}

	private static void loadAllPackages(MDicionario dicionario) {
		Reflections reflections = new Reflections("br");
		Set<Class<? extends MPacote>> subTypes = reflections.getSubTypesOf(MPacote.class);
		subTypes.stream()
			.filter( new Predicate<Class<? extends MPacote>>() {
				public boolean test(Class<? extends MPacote> mClass) {
					int modifiers = mClass.getModifiers();
					return !Modifier.isAbstract(modifiers) && 
							!Modifier.isAbstract(modifiers); 
				}
			})
			.forEach( mClass -> {
					try {
						dicionario.carregarPacote(mClass);
					} catch (Exception e) {
//						throw new RuntimeException(e);
					}
				}
			);
	}
	
	public ListContent(String id) {
		super(id);
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
		return formType.getCampos().stream().map(t -> {
			MTipo<?> campo = formType.getCampo(t);
			return new FieldVO(t, campo.getClasseInstancia().getName());
		}).collect(Collectors.toList());
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