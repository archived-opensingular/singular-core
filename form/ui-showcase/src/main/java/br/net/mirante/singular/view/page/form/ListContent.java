package br.net.mirante.singular.view.page.form;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.Transformer;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.panel.Fragment;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.ResourceModel;
import org.reflections.Reflections;

import br.net.mirante.singular.form.mform.MPacote;
import br.net.mirante.singular.util.wicket.datatable.BSDataTableBuilder;
import br.net.mirante.singular.util.wicket.datatable.BaseDataProvider;
import br.net.mirante.singular.util.wicket.feedback.BSFeedbackPanel;
import br.net.mirante.singular.view.SingularWicketContainer;
import br.net.mirante.singular.view.template.Content;

class ListContent extends Content implements SingularWicketContainer<FormContent, Void> {

	final static List<FormDTO> formTypes ;
	
	static{
		Reflections reflections = new Reflections("br");
		Set<Class<? extends MPacote>> subTypes = reflections.getSubTypesOf(MPacote.class);
		System.out.println(">>>>>>>>>>");
		System.out.println(subTypes);
		formTypes = subTypes.stream().map( new Function<Class<? extends MPacote>, FormDTO>() {
			public FormDTO apply(Class<? extends MPacote> mClass) {
				try {
					MPacote obj = mClass.newInstance();
					
					return new FormDTO(obj.getNome(), obj.toString());
				} catch (Exception e) {
					throw new RuntimeException(e);
				}
			}
		}).collect(Collectors.toList()); 
		
	}
	
	public ListContent(String id) {
		super(id);
	}

	@Override
	protected void onInitialize() {
		super.onInitialize();
		add(new BSFeedbackPanel("feedback"));
		
		BaseDataProvider<FormDTO, String> provider = new BaseDataProvider<FormDTO, String>() {

			@Override
			public long size() {
				return formTypes.size();
			}

			@Override
			public Iterator<? extends FormDTO> iterator(int first, int count, String sortProperty, boolean ascending) {
				return formTypes.iterator();
			}
		};
		add(new BSDataTableBuilder<>(provider)
				.appendPropertyColumn(getMessage("label.table.column.key"), "key", FormDTO::getKey)
                .appendPropertyColumn(getMessage("label.table.column.value"), "value", FormDTO::getValue)
                .build("form-list"));
		
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