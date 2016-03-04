package br.net.mirante.singular.showcase.view.page.prototype;

import br.net.mirante.singular.form.mform.SDictionary;
import br.net.mirante.singular.form.mform.SType;
import br.net.mirante.singular.form.mform.context.SFormConfig;
import br.net.mirante.singular.form.mform.core.STypeData;
import br.net.mirante.singular.form.mform.core.STypeString;
import br.net.mirante.singular.form.wicket.mapper.selection.SelectOption;
import br.net.mirante.singular.showcase.view.page.form.FormVO;
import br.net.mirante.singular.showcase.view.template.Content;
import br.net.mirante.singular.util.wicket.ajax.ActionAjaxButton;
import br.net.mirante.singular.util.wicket.util.WicketUtils;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.form.AjaxButton;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.*;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.ResourceModel;

import javax.inject.Inject;
import javax.inject.Named;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import static com.google.common.collect.Lists.newArrayList;

/**
 * Created by nuk on 03/03/16.
 */
public class PrototypeContent extends Content {

    private static final SDictionary dictionary = SDictionary.create();

    private IModel<List<Field>> fieldsModel;
    private DropDownChoice<SelectOption> typeField;
    private TextField nameField;

    public PrototypeContent(String id) {
        super(id);
    }

    @Override
    protected void onInitialize() {
        super.onInitialize();

        fieldsModel = Model.of((Collection) newArrayList());

        final Form newItemForm = new Form("prototype_form");

        newItemForm.add(nameField = new TextField("field_name", Model.of("")));

        newItemForm.add(typeField = createFieldTypeChoices());

        newItemForm.add(new ActionAjaxButton("add_btn") {
            @Override
            protected void onAction(AjaxRequestTarget target, Form<?> form) {
                fieldsModel.getObject().add(new Field(
                        (String) nameField.getModel().getObject(),
                        (String) typeField.getModel().getObject().getValue()));
                target.add(newItemForm);
            }
        });
        newItemForm.add(new ActionAjaxButton("preview_btn"){
            @Override
            protected void onAction(AjaxRequestTarget target, Form<?> form) {
                setResponsePage(new PreviewPage(fieldsModel.getObject()));
            }
        });
        queue(newItemForm);
        queue(createFieldList());
    }

    private ListView createFieldList() {
        return new ListView<Field>("field_list", fieldsModel) {
                @Override
                protected void populateItem(ListItem<Field> item) {
                    item.add(new Label("field_name", item.getModelObject().fieldName));
                    item.add(new Label("field_type", item.getModelObject().typeName));
                }
            };
    }

    private DropDownChoice<SelectOption> createFieldTypeChoices() {
        List<SelectOption> options = newArrayList(
                new SelectOption("Texto",dictionary.getType(STypeString.class).getName()),
                new SelectOption("Data",dictionary.getType(STypeData.class).getName())
        );

        ChoiceRenderer choiceRenderer = new ChoiceRenderer("selectLabel", "value");
        return new DropDownChoice<>("field_options", options.get(0), options, choiceRenderer);
    }


    @Override
    protected IModel<?> getContentTitlelModel() {
        return new ResourceModel("label.content.title");
    }

    @Override
    protected IModel<?> getContentSubtitlelModel() {
        return new ResourceModel("label.content.title");
    }

    public static class Field implements Serializable {
        final public String fieldName, typeName;

        Field(String fieldName, SType type){
            this.fieldName = fieldName;
            this.typeName = type.getName();
        }

        Field(String fieldName, String typeName){
            this.fieldName = fieldName;
            this.typeName = typeName;
        }

        SType type(){
            return dictionary.getType(typeName);
        }
    }
}
