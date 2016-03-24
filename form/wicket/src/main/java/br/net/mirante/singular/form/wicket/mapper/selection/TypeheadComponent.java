package br.net.mirante.singular.form.wicket.mapper.selection;

import br.net.mirante.singular.form.mform.SInstance;
import br.net.mirante.singular.form.mform.basic.view.SViewAutoComplete;
import br.net.mirante.singular.form.mform.options.SOptionsConfig;
import br.net.mirante.singular.form.mform.options.SOptionsProvider;
import br.net.mirante.singular.form.wicket.model.MInstanciaValorModel;
import org.apache.commons.lang3.StringUtils;
import org.apache.wicket.ajax.AbstractDefaultAjaxBehavior;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.json.JSONArray;
import org.apache.wicket.ajax.json.JSONObject;
import org.apache.wicket.behavior.AbstractAjaxBehavior;
import org.apache.wicket.markup.head.IHeaderResponse;
import org.apache.wicket.markup.head.JavaScriptReferenceHeaderItem;
import org.apache.wicket.markup.head.OnDomReadyHeaderItem;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.request.IRequestParameters;
import org.apache.wicket.request.Request;
import org.apache.wicket.request.cycle.RequestCycle;
import org.apache.wicket.request.http.WebResponse;
import org.apache.wicket.request.resource.PackageResourceReference;
import org.apache.wicket.util.string.StringValue;

import java.util.Collection;
import java.util.Map;

import static com.google.common.collect.Lists.newArrayList;

/**
 * Created by nuk on 21/03/16.
 */
public class TypeheadComponent extends Panel {

    private final SViewAutoComplete.Mode fetch;
    private WebMarkupContainer container;
    private AbstractAjaxBehavior dynamicFetcher;

    public TypeheadComponent(String id, IModel<?> model, SViewAutoComplete.Mode fetch) {
        super(id, model);
        this.fetch = fetch;
    }

    @Override
    protected void onInitialize() {
        super.onInitialize();
        add(container = buildContainer());
    }

    private WebMarkupContainer buildContainer() {
        WebMarkupContainer c = new WebMarkupContainer("typeahead_container");
        MOptionsModel options = new MOptionsModel(getDefaultModel());
        c.queue(new TextField("label_field", options));
        add(dynamicFetcher = new BloodhoundDataBehavior(options));
        return c;
    }

    @Override
    public void renderHead(IHeaderResponse response) {
        super.renderHead(response);
        response.render(JavaScriptReferenceHeaderItem.forReference(resourceRef("TypeheadComponent.js")));
        String fetchJS = "";
        if(fetch == SViewAutoComplete.Mode.STATIC){
            fetchJS = staticJSFetch();
        }else {
            String fetcher = "new Bloodhound({\n" +
                    "  datumTokenizer: Bloodhound.tokenizers.obj.whitespace('value'),\n" +
                    "  queryTokenizer: Bloodhound.tokenizers.whitespace,\n" +
//                    "  prefetch: 'http://twitter.github.io/typeahead.js/data/films/post_1960.json',\n" +
                    "  prefetch: '"+dynamicFetcher.getCallbackUrl()+"',\n" +
                    "  remote: {\n" +
                    "    url: '"+dynamicFetcher.getCallbackUrl()+"&filter=%QUERY',\n" +
                    "    wildcard: '%QUERY'\n" +
                    "  }\n" +
                    "})";

            fetchJS = "$('#" + container.getMarkupId() + " .typeahead').typeahead( " +
                    "null," +
                    "{name: 'select', " +
                    "display: 'value', " +
                    "source: "+fetcher+" });";
        }
        response.render(OnDomReadyHeaderItem.forScript(fetchJS));
    }

    private String staticJSFetch() {
        return "$('#" + container.getMarkupId() + " .typeahead').typeahead( " +
                "{hint: true, highlight: true, minLength: 0 }, " +
                "{name: 'select', " +
                "source: window.substringMatcher(" + jsOptionArray() + ") });";
    }

    private String jsOptionArray() {
        Map<String, String> options = optionsConfigMap();
        return "['" + StringUtils.join(options.values(), "','") + "']";
    }

    private Map<String, String> optionsConfigMap() {
        return optionsConfig().listSelectOptions();
    }

    private SOptionsConfig optionsConfig() {
        return ((SInstance) getDefaultModelObject()).getOptionsConfig();
    }

    private PackageResourceReference resourceRef(String resourceName) {
        return new PackageResourceReference(getClass(), resourceName);
    }

}

class MOptionsModel extends MInstanciaValorModel {

    public MOptionsModel(IModel model) {
        super(model);
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

class BloodhoundDataBehavior extends AbstractDefaultAjaxBehavior {
    private MOptionsModel model;

    public BloodhoundDataBehavior(MOptionsModel model) {
        this.model = model;
    }

    SOptionsConfig options(){
        return model.options();
    }

    @Override
    public void respond(AjaxRequestTarget target) {

        JSONArray r = createResponse();

        requestCycle().scheduleRequestHandlerAfterCurrent(null); //TODO: Fabs: Test this

        WebResponse response = (WebResponse) requestCycle().getResponse();
        response.setHeader("Content-Type", "application/json; charset=utf8");
        response.write(r.toString());
    }

    private JSONArray createResponse() {
        JSONArray r = new JSONArray();
        values(filterValue()).forEach((x) -> r.put(newValue(x)));
        return r;
    }

    private String filterValue() {
        return requestParameter().toString(null);
    }

    private StringValue requestParameter() {
        IRequestParameters parameters = request().getRequestParameters();
        return parameters.getParameterValue("filter");
    }

    private Request request() {
        return requestCycle().getRequest();
    }

    private Collection<String> values(String filter) {
        if(options() == null) return newArrayList();
        Map<String, String> map = options().listSelectOptions(filter);
        if(map == null) return newArrayList();
        return map.values();
    }

    protected RequestCycle requestCycle() {
        return getComponent().getRequestCycle();
    }

    private JSONObject newValue(String label) {
        JSONObject value = new JSONObject();
        value.put("value", label);
        return value;
    }
}