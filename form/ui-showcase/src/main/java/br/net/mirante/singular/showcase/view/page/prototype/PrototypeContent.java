package br.net.mirante.singular.showcase.view.page.prototype;

import br.net.mirante.singular.showcase.view.template.Content;
import br.net.mirante.singular.util.wicket.ajax.ActionAjaxButton;
import br.net.mirante.singular.util.wicket.util.WicketUtils;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.form.AjaxButton;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.ResourceModel;

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
    public PrototypeContent(String id) {
        super(id);
    }

    @Override
    protected void onInitialize() {
        super.onInitialize();
        final IModel<List<String>> fieldsModel = Model.of((Collection) newArrayList());
        ListView fieldList = new ListView("field_list", fieldsModel) {
            @Override
            protected void populateItem(ListItem item) {
                item.add(new Label("field_name", item.getModel()));
            }
        };

        final IModel<String> nameModel = Model.of("");
        final Form newItemForm = new Form("prototype_form");
        newItemForm.add(new TextField("field_name", nameModel));
        newItemForm.add(new ActionAjaxButton("add_btn") {
            @Override
            protected void onAction(AjaxRequestTarget target, Form<?> form) {
                fieldsModel.getObject().add(nameModel.getObject());
                target.add(newItemForm);
            }
        });
        queue(newItemForm);
        queue(fieldList);
    }

    @Override
    protected IModel<?> getContentTitlelModel() {
        return new ResourceModel("label.content.title");
    }

    @Override
    protected IModel<?> getContentSubtitlelModel() {
        return new ResourceModel("label.content.title");
    }
}
