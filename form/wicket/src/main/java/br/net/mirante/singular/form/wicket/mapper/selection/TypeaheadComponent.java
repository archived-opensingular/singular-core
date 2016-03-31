package br.net.mirante.singular.form.wicket.mapper.selection;

import br.net.mirante.singular.form.mform.SIComposite;
import br.net.mirante.singular.form.mform.SISimple;
import br.net.mirante.singular.form.mform.SInstance;
import br.net.mirante.singular.form.mform.basic.view.SViewAutoComplete;
import br.net.mirante.singular.form.mform.options.SOptionsConfig;
import br.net.mirante.singular.form.wicket.model.MInstanciaValorModel;
import org.apache.wicket.Component;
import org.apache.wicket.ajax.AbstractDefaultAjaxBehavior;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.json.JSONArray;
import org.apache.wicket.ajax.json.JSONObject;
import org.apache.wicket.behavior.AbstractAjaxBehavior;
import org.apache.wicket.markup.head.IHeaderResponse;
import org.apache.wicket.markup.head.JavaScriptReferenceHeaderItem;
import org.apache.wicket.markup.head.OnDomReadyHeaderItem;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.form.HiddenField;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.request.IRequestParameters;
import org.apache.wicket.request.Request;
import org.apache.wicket.request.cycle.RequestCycle;
import org.apache.wicket.request.http.WebResponse;
import org.apache.wicket.request.resource.PackageResourceReference;
import org.apache.wicket.util.string.StringValue;

import java.io.Serializable;
import java.util.Map;

import static br.net.mirante.singular.form.wicket.mapper.selection.TypeaheadComponent.generateResultOptions;
import static com.google.common.collect.Lists.newArrayList;
import static com.google.common.collect.Maps.newHashMap;

/**
 * AutoComplete wicket component using Typeahead library.
 * http://twitter.github.io/typeahead.js/
 * which is abandoned, so better focus on
 * https://github.com/corejavascript/typeahead.js
 *
 * It is build based on configuration placed withing an SViewAutoComplete.Mode object.
 *
 * @author Fabricio Buzeto
 */
public class TypeaheadComponent extends Panel {

    private final SViewAutoComplete.Mode fetch;
    private WebMarkupContainer container;
    private AbstractAjaxBehavior dynamicFetcher;
    private HiddenField valueField;

    public TypeaheadComponent(String id, IModel<?> model, SViewAutoComplete.Mode fetch) {
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

        c.queue(new TextField("label_field", new Model(){
            @Override
            public Serializable getObject() {
                IModel<?> parentModel = TypeaheadComponent.this.getDefaultModel();
                SInstance instance = (SInstance) parentModel.getObject();
                if(instance().getValue() != null ){ return instance().getSelectLabel();    }
                return "";
            }
        }));
        c.queue(valueField = new HiddenField("value_field", options));
        add(dynamicFetcher = new BloodhoundDataBehavior(options));
        return c;
    }

    @Override
    public void renderHead(IHeaderResponse response) {
        super.renderHead(response);
        response.render(JavaScriptReferenceHeaderItem.forReference(resourceRef("TypeaheadComponent.js")));
        response.render(OnDomReadyHeaderItem.forScript(createJSFetcher()));
    }

    private String createJSFetcher() {
        if(fetch == SViewAutoComplete.Mode.STATIC){ return staticJSFetch();
        }else { return dynamicJSFetch();    }
    }

    private String staticJSFetch() {
        return "$('#" + container.getMarkupId() + " .typeahead').typeahead( " +
                "{hint: true, highlight: true, minLength: 0 }, " +
                "{name: 's-select-typeahead', " +
                "display: 'value', "+
                "source: window.substringMatcher(" + jsOptionArray() + ") })\n" +
                createBindExpression()+
                ";";
    }

    private String createBindExpression() {
        return ".bind('typeahead:select', function(ev, suggestion) {\n" +
                "  console.log(ev, suggestion); \n"+
                "   $('#" + valueField.getMarkupId() + "').val(suggestion['key']).change();\n" +
                "})";
    }
    private String dynamicJSFetch() {
        return "$('#" + container.getMarkupId() + " .typeahead').typeahead( " +
                "{limit: Infinity, minLength: 0, hint:false }," +
                "{name: 's-select-typeahead', " +
                "display: 'value', " +
                "source: " + createJSBloodhoundOpbject() + " })\n" +
                createBindExpression() +
                ";";
    }

    private String createJSBloodhoundOpbject() {
        return "new Bloodhound({\n" +
                    "  datumTokenizer: Bloodhound.tokenizers.obj.whitespace('value'),\n" +
                    "  queryTokenizer: Bloodhound.tokenizers.whitespace,\n" +
                    "  prefetch: '"+dynamicFetcher.getCallbackUrl()+"',\n" +
                    "  remote: {\n" +
                    "    url: '"+dynamicFetcher.getCallbackUrl()+"&filter=%QUERY',\n" +
                    "    wildcard: '%QUERY'\n" +
                    "  }\n" +
                    "})";
    }

    private String jsOptionArray() {
        return generateResultOptions(optionsConfigMap());
    }

    private Map<String, String> optionsConfigMap() {
        return optionsConfig().listSelectOptions();
    }

    private SOptionsConfig optionsConfig() {
        return instance().getOptionsConfig();
    }

    private SInstance instance() {
        return (SInstance) getDefaultModelObject();
    }

    protected static String generateResultOptions(Map<String, String> options) {
        JSONArray arr = new JSONArray();
        options.entrySet().forEach((e) -> arr.put(newValue(e.getKey(),e.getValue())));
        return arr.toString();
    }

    protected static JSONObject newValue(String key, String label) {
        JSONObject value = new JSONObject();
        value.put("key", key);
        value.put("value", label);
        return value;
    }

    private PackageResourceReference resourceRef(String resourceName) {
        return new PackageResourceReference(getClass(), resourceName);
    }

}

/**
 * Model for selecting data based on SOptionsConfig.
 *
 * @author Fabricio Buzeto
 */
class MOptionsModel extends MInstanciaValorModel {

    public MOptionsModel(IModel model) {
        super(model);
    }

    SOptionsConfig options(){
        return getMInstancia().getOptionsConfig();
    }

    @Override
    public void setObject(Object object) {
        super.setObject(defineValue((String)object));
    }

    @Override
    public Object getObject() {
        return options().getKeyFromOption(getMInstancia());
    }

    public Class getObjectClass() {
        if (SIComposite.class.isAssignableFrom(super.getObjectClass())) return String.class;
        return super.getObjectClass();
    }

    private Object defineValue(String object) {
        if(object != null){
            SInstance value = options().getValueFromKey(object);
            if(value != null && value instanceof SISimple){
                return value.getValue();
            }else{
                return value;
            }
        }
        return null;
    }
}

/**
 * Behaviour that implements responses compatible with the Bloodhound fetch library.
 * https://github.com/twitter/typeahead.js/blob/master/doc/bloodhound.md
 *
 * @author Fabricio Buzeto
 */
class BloodhoundDataBehavior extends AbstractDefaultAjaxBehavior {
    private MOptionsModel model;

    public BloodhoundDataBehavior(MOptionsModel model) {
        this.model = model;
    }

    @Override
    public boolean getStatelessHint(Component component) {
        return false;
    }

    SOptionsConfig options(){
        return model.options();
    }

    @Override
    public void respond(AjaxRequestTarget target) {
        String r = generateResultOptions(values(filterValue()));
        requestCycle().scheduleRequestHandlerAfterCurrent(null);

        WebResponse response = (WebResponse) requestCycle().getResponse();
        response.setHeader("Content-Type", "application/json; charset=utf8");
        response.write(r.toString());
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

    private Map<String, String> values(String filter) {
        if(options() == null) return newHashMap();
        Map<String, String> map = options().listSelectOptions(filter);
        if(map == null) return newHashMap();
        return map;
    }

    protected RequestCycle requestCycle() {
        return getComponent().getRequestCycle();
    }


}