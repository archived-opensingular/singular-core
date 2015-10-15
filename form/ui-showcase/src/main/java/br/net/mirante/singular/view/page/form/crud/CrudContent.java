package br.net.mirante.singular.view.page.form.crud;

import java.util.Arrays;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.form.ChoiceRenderer;
import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.panel.Fragment;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.ResourceModel;

import br.net.mirante.singular.dao.form.ExampleDateDTO;
import br.net.mirante.singular.dao.form.TemplateRepository;
import br.net.mirante.singular.util.wicket.datatable.BSDataTable;
import br.net.mirante.singular.util.wicket.datatable.BSDataTableBuilder;
import br.net.mirante.singular.util.wicket.datatable.BaseDataProvider;
import br.net.mirante.singular.util.wicket.feedback.BSFeedbackPanel;
import br.net.mirante.singular.view.SingularWicketContainer;
import br.net.mirante.singular.view.page.form.FormContent;
import br.net.mirante.singular.view.page.form.FormVO;
import br.net.mirante.singular.view.template.Content;

@SuppressWarnings("serial")
public class CrudContent extends Content implements SingularWicketContainer<FormContent, Void> {

	private List<ExampleDateDTO> dataList = new LinkedList<>();
	
	public CrudContent(String id) {
		super(id, false, true);
	}

	protected void onInitialize() {
		super.onInitialize();
		queue(new BSFeedbackPanel("feedback"));
		Form<Object> optionsForm = new Form<>("optionsForm");
		optionsForm.queue(setUpTemplatesOptions());
		queue(optionsForm);
		
		BaseDataProvider<ExampleDateDTO, String> provider = 
				new BaseDataProvider<ExampleDateDTO, String>() {

			public long size() {
				return dataList.size();
			}

			public Iterator<? extends ExampleDateDTO> iterator(int first, int count, 
					String sortProperty, boolean ascending) {
				return dataList.iterator();
			}
		};
		
		BSDataTable<ExampleDateDTO, String> formDataTable = 
				new BSDataTableBuilder<>(provider)
				.appendPropertyColumn(getMessage("label.table.column.key"), 
						"key", ExampleDateDTO::getKey)
				.setRowsPerPage(Long.MAX_VALUE) //TODO: proper pagination
                .build("data-list");
		queue(formDataTable);
		
		
	}

	private DropDownChoice setUpTemplatesOptions() {
		List<SelectOption> options = TemplateRepository.formTemplates().stream().map( t -> {
			return new SelectOption(t.getNomeSimples(),new FormVO(t.getNomeSimples(), t));
		}).collect(Collectors.toList());
		
		ChoiceRenderer choiceRenderer = new ChoiceRenderer("key", "key");
		DropDownChoice formChoices = new DropDownChoice<SelectOption>("options", 
				new SelectOption(null, null),options, choiceRenderer){
			@Override
			protected boolean wantOnSelectionChangedNotifications() {
				return true;
			}
			
			@Override
			protected void onSelectionChanged(SelectOption newSelection) {
				super.onSelectionChanged(newSelection);
				String key = newSelection.getKey();
				dataList.clear();
				for(int i = 0 ; i < key.length(); i++) {
					dataList.add(new ExampleDateDTO(key+"-"+i));
				}
				System.out.println(key);
			}
		};
		return formChoices;
	}
	
	public static class SelectOption<T> implements IModel {
		  private String key;
		  private T value;
		  
		public SelectOption(String key, T value) {
			this.key = key;
			this.value = value;
		}

		public String getKey() {
			return key;
		}

		public void setKey(String key) {
			this.key = key;
		}

		public T getValue() {
			return value;
		}

		public void setValue(T value) {
			this.value = value;
		}

		@Override
		public void detach() {
			// TODO Auto-generated method stub
			
		}

		@Override
		public Object getObject() {
			return this;
		}

		@Override
		public void setObject(Object o) {
			SelectOption s = (SelectOption) o;
			this.setKey(s.getKey());
			this.setValue((T)s.getValue());
		}
		  
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
