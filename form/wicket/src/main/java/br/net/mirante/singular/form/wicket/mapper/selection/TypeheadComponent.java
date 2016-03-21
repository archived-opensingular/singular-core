package br.net.mirante.singular.form.wicket.mapper.selection;

import br.net.mirante.singular.form.mform.SInstance;
import br.net.mirante.singular.form.mform.options.SOptionsConfig;
import br.net.mirante.singular.form.wicket.model.MInstanciaValorModel;
import org.apache.commons.lang3.StringUtils;
import org.apache.wicket.markup.head.IHeaderResponse;
import org.apache.wicket.markup.head.JavaScriptReferenceHeaderItem;
import org.apache.wicket.markup.head.OnDomReadyHeaderItem;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.request.resource.PackageResourceReference;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;

/**
 * Created by nuk on 21/03/16.
 */
public class TypeheadComponent extends Panel {

    private WebMarkupContainer container;

    public TypeheadComponent(String id, IModel<?> model) {
        super(id, model);
    }

    @Override
    protected void onInitialize() {
        super.onInitialize();
        add(container = buildContainer());
    }

    private WebMarkupContainer buildContainer() {
        WebMarkupContainer c = new WebMarkupContainer("typeahead_container");
        c.queue(new TextField("label_field", new MOptionsModel(getDefaultModel())));
        return c;
    }

    @Override
    public void renderHead(IHeaderResponse response) {
        super.renderHead(response);
        response.render(JavaScriptReferenceHeaderItem.forReference(resourceRef("TypeheadComponent.js")));
        response.render(OnDomReadyHeaderItem.forScript(
                "$('#" + container.getMarkupId() + " .typeahead').typeahead( " +
                        "{hint: true, highlight: true, minLength: 0 }, " +
                        "{name: 'states', " +
                        "source: window.substringMatcher(" + jsOptionArray() + ") });"

        ));
    }

    private String jsOptionArray() {
        SInstance instance = (SInstance) getDefaultModelObject();
        Map<String, String> options = instance.getOptionsConfig().listSelectOptions();
        return "['" + StringUtils.join(options.values(), "','") + "']";
    }

    private PackageResourceReference resourceRef(String resourceName) {
        return new PackageResourceReference(getClass(), resourceName);
    }
}

class MOptionsModel extends MInstanciaValorModel {

    public MOptionsModel(IModel instanciaModel) {
        super(instanciaModel);
    }

    SOptionsConfig options(){
        return getMInstancia().getOptionsConfig();
    }

    @Override
    public void setObject(Object object) {
        String key = options().getKeyFromLabel((String) object);
        if(key != null){
            SInstance value = options().getValueFromKey(key);
            if(value != null){
                super.setObject(value.getValue());
            }
        }
    }
}