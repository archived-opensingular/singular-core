package br.net.mirante.singular.form.wicket.mapper.selection;

import br.net.mirante.singular.form.mform.SInstance;
import br.net.mirante.singular.form.wicket.model.MInstanciaValorModel;
import br.net.mirante.singular.util.wicket.util.WicketUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.wicket.Component;
import org.apache.wicket.markup.head.CssReferenceHeaderItem;
import org.apache.wicket.markup.head.IHeaderResponse;
import org.apache.wicket.markup.head.JavaScriptReferenceHeaderItem;
import org.apache.wicket.markup.head.OnDomReadyHeaderItem;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.border.Body;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.request.resource.PackageResourceReference;
import org.json.JSONObject;

import java.util.LinkedHashMap;

import static br.net.mirante.singular.util.wicket.util.WicketUtils.$m;

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
        MInstanciaValorModel<Object> model = new MInstanciaValorModel<>((IModel<? extends SInstance>) getDefaultModel());
        c.queue(new TextField("label_field", model));
        return c;
    }

    @Override
    public void renderHead(IHeaderResponse response) {
        super.renderHead(response);
        response.render(JavaScriptReferenceHeaderItem.forReference(resourceRef("TypeheadComponent.js")));
        SInstance instance = (SInstance) getDefaultModelObject();
        LinkedHashMap<String, String> options = instance.getOptionsConfig().listSelectOptions();
        String join = StringUtils.join(options.values(), "','");
        response.render(OnDomReadyHeaderItem.forScript(
            "$('#"+container.getMarkupId()+" .typeahead').typeahead( " +
            "{hint: true, highlight: true, minLength: 0 }, " +
            "{name: 'states', source: window.substringMatcher(['"+join+"']) });"

        ));
    }

    private PackageResourceReference resourceRef(String resourceName) {
        return new PackageResourceReference(getClass(), resourceName);
    }
}