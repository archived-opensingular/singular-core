package br.net.mirante.singular.form.wicket.mapper.selection;

import br.net.mirante.singular.form.mform.SInstance;
import br.net.mirante.singular.form.mform.basic.view.SViewAutoComplete;
import br.net.mirante.singular.form.mform.options.SOptionsConfig;
import br.net.mirante.singular.form.wicket.behavior.AjaxUpdateSingularFormComponentPanel;
import br.net.mirante.singular.form.wicket.component.SingularFormComponentPanel;
import br.net.mirante.singular.util.wicket.jquery.JQuery;
import br.net.mirante.singular.util.wicket.model.IReadOnlyModel;
import org.apache.wicket.Component;
import org.apache.wicket.ajax.AbstractDefaultAjaxBehavior;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.attributes.CallbackParameter;
import org.apache.wicket.ajax.json.JSONArray;
import org.apache.wicket.ajax.json.JSONObject;
import org.apache.wicket.behavior.AbstractAjaxBehavior;
import org.apache.wicket.markup.head.IHeaderResponse;
import org.apache.wicket.markup.head.JavaScriptReferenceHeaderItem;
import org.apache.wicket.markup.head.OnDomReadyHeaderItem;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.form.HiddenField;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.model.IModel;
import org.apache.wicket.request.IRequestParameters;
import org.apache.wicket.request.Request;
import org.apache.wicket.request.cycle.RequestCycle;
import org.apache.wicket.request.handler.TextRequestHandler;
import org.apache.wicket.request.resource.PackageResourceReference;
import org.apache.wicket.util.string.StringValue;

import java.util.Map;
import java.util.Optional;

import static br.net.mirante.singular.form.wicket.mapper.selection.TypeaheadComponent.generateResultOptions;
import static br.net.mirante.singular.util.wicket.util.WicketUtils.$b;
import static com.google.common.collect.Maps.newHashMap;


/**
 * AutoComplete wicket component using Typeahead library.
 * http://twitter.github.io/typeahead.js/
 * which is abandoned, so better focus on
 * https://github.com/corejavascript/typeahead.js
 * <p>
 * It is build based on configuration placed withing an SViewAutoComplete.Mode object.
 *
 * @author Fabricio Buzeto
 */
public class TypeaheadComponent extends SingularFormComponentPanel<SInstance, String> {

    private static final String BLOODHOUND_SUGGESTION_KEY_NAME   = "key";
    private static final String BLOODHOUND_SUGGESTION_LABEL_NAME = "value";
    private final SViewAutoComplete.Mode fetch;
    private       WebMarkupContainer     container;
    private       AbstractAjaxBehavior   dynamicFetcher;
    private       HiddenField            valueField;
    private       TextField<String>      labelField;

    @SuppressWarnings("unchecked")
    public TypeaheadComponent(String id, IModel<? extends SInstance> model, SViewAutoComplete.Mode fetch) {
        super(id, new MSelectionInstanceModel<>(model));
        this.fetch = fetch;
        add(container = buildContainer());
    }

    protected static String generateResultOptions(Map<String, String> options) {
        JSONArray arr = new JSONArray();
        options.entrySet().forEach((e) -> arr.put(newValue(e.getKey(), e.getValue())));
        return arr.toString();
    }

    protected static JSONObject newValue(String key, String label) {
        JSONObject value = new JSONObject();
        value.put(BLOODHOUND_SUGGESTION_KEY_NAME, key);
        value.put(BLOODHOUND_SUGGESTION_LABEL_NAME, label);
        return value;
    }

    @SuppressWarnings("unchecked")
    private WebMarkupContainer buildContainer() {
        WebMarkupContainer c = new WebMarkupContainer("typeahead_container");
        c.add(labelField = new TextField("label_field",
                (IReadOnlySafeModel) () -> instance() != null ? Optional.ofNullable(instance().getSelectLabel()).orElse("") : ""));
        c.add(valueField = new HiddenField("value_field",
                (IReadOnlySafeModel) () -> instance() != null ? Optional.ofNullable(optionsConfig().getKeyFromOption(instance())).orElse("") : ""));
        add(dynamicFetcher = new BloodhoundDataBehavior((MSelectionInstanceModel) getDefaultModel()));
        return c;
    }


    @Override
    public void renderHead(IHeaderResponse response) {
        super.renderHead(response);
        response.render(JavaScriptReferenceHeaderItem.forReference(resourceRef("TypeaheadComponent.js")));
        response.render(OnDomReadyHeaderItem.forScript(createJSFetcher()));
    }

    private String createJSFetcher() {
        if (fetch == SViewAutoComplete.Mode.STATIC) {
            return staticJSFetch();
        } else {
            return dynamicJSFetch();
        }
    }

    private String staticJSFetch() {
        return "$('#" + container.getMarkupId() + " .typeahead').typeahead( " +
                "{hint: false, highlight: true, minLength: 0}," +
                "{name: 's-select-typeahead', " +
                "display: 'value', " +
                "typeaheadAppendToBody: 'true', " +
                "source: window.substringMatcher(" + jsOptionArray() + ") })\n" +
                createBindExpression() +
                ";";
    }

    private String createBindExpression() {
        return ".bind('typeahead:select', function(ev, suggestion) {\n" +
                "   $('#" + valueField.getMarkupId() + "').val(suggestion['key']);\n" +
                "   $(this).data('openPlease', false);\n" +
                "   $(this).typeahead('close');\n" +
                "})\n" +
                ".bind('typeahead:change', function(ev, suggestion) {\n" +
                "   $(this).data('openPlease', true);\n" +
                "   $(this).typeahead('open');\n" +
                "})\n" +
                ".bind('typeahead:open', function(ev, suggestion) {\n" +
                "   if ($(this).data('openPlease') != true && $(this).typeahead('val') != ''){\n" +
                "       $(this).typeahead('close');\n" +
                "   }\n" +
                "})" +
                ".bind('typeahead:close', function(ev, suggestion) {\n" +
                "    $(this).data('openPlease', false);\n" +
                "})" +
                ".keyup( function(e) {\n" +
                "   if (e.keyCode == 13){\n" +
                "       e.preventDefault();" +
                "   } else {\n" +
                "       $(this).trigger('typeahead:change');\n" +
                "   }" +
                "})" +
                ".focus(function(e) {\n" +
                "   var ttInput = $(this);\n" +
                "   var currentValue = ttInput.val();\n" +
                "   ttInput.val('');\n" +
                "   ttInput.val(currentValue);\n" +
                "})\n" +
                ".blur(function(e) {\n" +
                "   $(this).data('openPlease', false);\n" +
                "   $(this).typeahead('close');\n" +
                "})\n";
    }

    private String dynamicJSFetch() {
        return "$('#" + container.getMarkupId() + " .typeahead').typeahead( " +
                "{limit: Infinity, minLength: 1, hint:false }," +
                "{name: 's-select-typeahead', " +
                "display: 'value', " +
                "source: " + createJSBloodhoundOpbject() + " })\n" +
                createBindExpression() +
                ";";
    }

    private String createJSBloodhoundOpbject() {
        return "new Bloodhound({\n" +
                "  initialize: true,\n" +
                "  clear: true,\n" +
                "  datumTokenizer: Bloodhound.tokenizers.obj.whitespace('value'),\n" +
                "  queryTokenizer: Bloodhound.tokenizers.whitespace,\n" +
                "  cache: false, " +
                "  prefetch: null," +
                "  remote: {\n" +
                "    url: '" + dynamicFetcher.getCallbackUrl() + "&filter=%QUERY',\n" +
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
        return ((MSelectionInstanceModel) getDefaultModel()).getMInstancia();
    }

    private PackageResourceReference resourceRef(String resourceName) {
        return new PackageResourceReference(getClass(), resourceName);
    }

    @Override
    public Class<String> configureAjaxBehavior(AjaxUpdateSingularFormComponentPanel<String> behavior, String valueRequestParameterName) {
        add($b.onReadyScript(comp -> JQuery.$(comp) + ".on('typeahead:selected', \n"
                + "function(event,selection,dataset){ \n"
                + "( \n"
                + behavior.getCallbackFunction(CallbackParameter.converted(valueRequestParameterName, "selection." + BLOODHOUND_SUGGESTION_KEY_NAME))
                + ")(selection); \n"
                + "});\n"));
        return String.class;
    }

    @Override
    public boolean processChildren() {
        return false;
    }

    /**
     * Faz o tratamento do valor recebido via ajax.
     *
     * @param value
     * @param instanceModel
     */
    @Override
    public void ajaxValueToModel(String value, IModel<SInstance> instanceModel) {
        updateModel(value);
    }

    /**
     * suprimindo processamento default dos campos do FormComponentPanel
     */
    @Override
    public void convertInput() {
    }

    /**
     * suprimindo a atualização default de model do FormComponentPanel
     */
    @Override
    public void updateModel() {
        //processando os inputs para forçar a re-renderização deles
        labelField.processInput();
        valueField.processInput();
        updateModel(valueField.getConvertedInput());
    }

    /**
     * Atualizando o model de acordo com a chave no campo hidden 'value'*
     *
     * @param value
     */
    private void updateModel(Object value) {
        if (value != null) {
            MSelectionInstanceModel model = (MSelectionInstanceModel) getDefaultModel();
            String                  label = optionsConfig().getLabelFromKey(value);
            if (label != null) {
                model.setObject(new SelectOption(label, value));
            }
        }
    }

    interface IReadOnlySafeModel<T> extends IReadOnlyModel<T> {
        @Override
        default public void setObject(T object) {
        }
    }
}

/**
 * Behaviour that implements responses compatible with the Bloodhound fetch library.
 * https://github.com/twitter/typeahead.js/blob/master/doc/bloodhound.md
 *
 * @author Fabricio Buzeto
 */
class BloodhoundDataBehavior extends AbstractDefaultAjaxBehavior {
    private MSelectionInstanceModel model;

    public BloodhoundDataBehavior(MSelectionInstanceModel model) {
        this.model = model;
    }

    @Override
    public boolean getStatelessHint(Component component) {
        return false;
    }

    SOptionsConfig options() {
        return model.getMInstancia().getOptionsConfig();
    }

    @Override
    public void respond(AjaxRequestTarget target) {
        requestCycle().scheduleRequestHandlerAfterCurrent(
                new TextRequestHandler("application/json", "utf-8", generateResultOptions(values(filterValue()))));
    }

    private String filterValue() {
        return requestParameter().toString();
    }

    private StringValue requestParameter() {
        IRequestParameters parameters = request().getRequestParameters();
        return parameters.getParameterValue("filter");
    }

    private Request request() {
        return requestCycle().getRequest();
    }

    private Map<String, String> values(String filter) {
        Map<String, String> map = null;
        if (options() != null) {
            map = options().listSelectOptions(filter);
        }
        if (map == null) {
            map = newHashMap();
        }
        return map;
    }

    protected RequestCycle requestCycle() {
        return getComponent().getRequestCycle();
    }


}