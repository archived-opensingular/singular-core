package br.net.mirante.singular.form.wicket.mapper.selection;

import static br.net.mirante.singular.form.wicket.mapper.selection.TypeaheadComponent.generateResultOptions;
import static br.net.mirante.singular.util.wicket.util.WicketUtils.$b;
import static com.google.common.collect.Maps.newLinkedHashMap;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Stream;

import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.wicket.Component;
import org.apache.wicket.ajax.AbstractDefaultAjaxBehavior;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.json.JSONArray;
import org.apache.wicket.ajax.json.JSONObject;
import org.apache.wicket.markup.head.CssReferenceHeaderItem;
import org.apache.wicket.markup.head.HeaderItem;
import org.apache.wicket.markup.head.IHeaderResponse;
import org.apache.wicket.markup.head.JavaScriptReferenceHeaderItem;
import org.apache.wicket.markup.head.OnDomReadyHeaderItem;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.request.IRequestParameters;
import org.apache.wicket.request.Request;
import org.apache.wicket.request.cycle.RequestCycle;
import org.apache.wicket.request.handler.TextRequestHandler;
import org.apache.wicket.request.resource.PackageResourceReference;
import org.apache.wicket.util.string.StringValue;

import br.net.mirante.singular.commons.lambda.IFunction;
import br.net.mirante.singular.form.SInstance;
import br.net.mirante.singular.form.SingularFormException;
import br.net.mirante.singular.form.converter.SInstanceConverter;
import br.net.mirante.singular.form.provider.Provider;
import br.net.mirante.singular.form.provider.ProviderContext;
import br.net.mirante.singular.form.util.transformer.Value;
import br.net.mirante.singular.form.view.SViewAutoComplete;
import br.net.mirante.singular.form.wicket.model.AbstractMInstanceAwareModel;
import br.net.mirante.singular.form.wicket.model.IMInstanciaAwareModel;
import br.net.mirante.singular.form.wicket.util.WicketFormProcessing;
import br.net.mirante.singular.util.wicket.template.SingularTemplate;


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
public class TypeaheadComponent extends Panel {

    public static final CssReferenceHeaderItem CSS_REFERENCE = CssReferenceHeaderItem.forReference(new PackageResourceReference(TypeaheadComponent.class, "TypeaheadComponent.css"){
        @Override
        public List<HeaderItem> getDependencies() {
            return SingularTemplate.getDefaultCSSUrls();
        }
    });

    private static final long serialVersionUID = -3639240121493651170L;

    private static final String BLOODHOUND_SUGGESTION_KEY_NAME   = "key";
    private static final String BLOODHOUND_SUGGESTION_LABEL_NAME = "value";

    private final Map<String, TypeaheadCache> cache = new HashMap<>();

    private final SViewAutoComplete.Mode      fetch;
    private final IModel<? extends SInstance> model;
    private       WebMarkupContainer          container;
    private       BloodhoundDataBehavior      dynamicFetcher;
    private       TextField<String>           valueField;
    private       TextField<String>           labelField;

    public TypeaheadComponent(String id, IModel<? extends SInstance> model, SViewAutoComplete.Mode fetch) {
        super(id);
        this.model = model;
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
        c.add(labelField = new TextField<>("label_field", new Model<String>() {

            private String lastDisplay;
            private Object lastValue;

            @Override
            public String getObject() {
                if (instance().isEmptyOfData()) {
                    return null;
                } else {
                    if (!Value.dehydrate(instance()).equals(lastValue)) {
                        lastValue = Value.dehydrate(instance());
                        final SInstanceConverter<Serializable, SInstance> converter = instance().asAtrProvider().getConverter();
                        if (converter != null) {
                            final Serializable converted = converter.toObject(instance());
                            if (converted != null) {
                                lastDisplay = instance().asAtrProvider().getDisplayFunction().apply(converted);
                            }
                        }
                    }
                    return lastDisplay;
                }
            }
        }));
        c.add(valueField = new TextField<>("value_field", new AbstractMInstanceAwareModel<String>() {

            private String lastId;
            private Object lastValue;

            @Override
            public SInstance getMInstancia() {
                return IMInstanciaAwareModel.optionalCast(model).map(IMInstanciaAwareModel::getMInstancia).orElse(null);
            }

            @Override
            public String getObject() {
                if (instance().isEmptyOfData()) {
                    return null;
                }
                if (!Value.dehydrate(instance()).equals(lastValue)) {
                    lastValue = Value.dehydrate(instance());
                    final IFunction<Object, Object> idFunction = instance().asAtrProvider().getIdFunction();
                    final SInstanceConverter<Serializable, SInstance>        converter  = instance().asAtrProvider().getConverter();
                    if (idFunction != null && converter != null && !instance().isEmptyOfData()) {
                        final Serializable converted = converter.toObject(instance());
                        if (converted != null) {
                            lastId = String.valueOf(idFunction.apply(converted));
                        }
                    }
                }
                return lastId;
            }

            @Override
            public void setObject(String key) {
                if (StringUtils.isEmpty(key)) {
                    getRequestCycle().setMetaData(WicketFormProcessing.MDK_SKIP_VALIDATION_ON_REQUEST, true);
                    getMInstancia().clearInstance();
                } else {
                    final Serializable val = getValueFromChace(key).map(TypeaheadCache::getTrueValue).orElse(getValueFromProvider(key).orElse(null));
                    if (val != null) {
                        instance().asAtrProvider().getConverter().fillInstance(getMInstancia(), val);
                    } else {
                        getMInstancia().clearInstance();
                    }
                }
            }

        }));
        add(dynamicFetcher = new BloodhoundDataBehavior(model, cache));
        return c;
    }

    private Optional<TypeaheadCache> getValueFromChace(String key) {
        return Optional.ofNullable(cache.get(key));
    }

    private Optional<Serializable> getValueFromProvider(String key) {

        final Stream<Serializable> stream;
        final Provider<Serializable, SInstance> provider = instance().asAtrProvider().getProvider();
        final ProviderContext<SInstance> providerContext = new ProviderContext<>();

        providerContext.setInstance(instance());

        if (dynamicFetcher != null) {
            providerContext.setQuery(dynamicFetcher.getFilterModel().getObject());
        } else {
            providerContext.setQuery(StringUtils.EMPTY);
        }

        if (provider != null) {
            stream = provider.load(providerContext).stream();
        } else {
            throw new SingularFormException("Nenhum provider foi informado");
        }

        return stream.filter(o -> instance().asAtrProvider().getIdFunction().apply(o).equals(key)).findFirst();
    }

    @Override
    public void renderHead(IHeaderResponse response) {
        super.renderHead(response);
        response.render(JavaScriptReferenceHeaderItem.forReference(resourceRef("TypeaheadComponent.js")));
        response.render(OnDomReadyHeaderItem.forScript(createJSFetcher()));
        response.render(CSS_REFERENCE);
    }

    private String createJSFetcher() {
        if (fetch == SViewAutoComplete.Mode.STATIC) {
            return staticJSFetch();
        } else {
            return dynamicJSFetch();
        }
    }

    private String staticJSFetch() {
        String js = "";
        js += " $('#" + labelField.getMarkupId() + "').typeahead('destroy');";
        js += " $('#" + labelField.getMarkupId() + "').val('" + ObjectUtils.defaultIfNull(labelField.getModelObject(), "") + "');";
        js += " $('#" + labelField.getMarkupId() + "').typeahead( ";
        js += "     { ";
        js += "          highlight: true,";
        js += "          minLength: 0,";
        js += "          hint:false";
        js += "      },";
        js += "     {";
        js += "        name : 's-select-typeahead', ";
        js += "        display: 'value', ";
        js += "        typeaheadAppendToBody: 'true',";
        js += "        limit: Infinity,";// não limita os resultados exibidos
        js += "        source: window.substringMatcher(" + jsOptionArray() + ") ";
        js += "     }";
        js += " );";
        js += " SingularTypeahead.configure('" + container.getMarkupId() + "','" + valueField.getMarkupId() + "');";
        return js;
    }

    private String dynamicJSFetch() {
        String js = "";
        js += " $('#" + container.getMarkupId() + " .typeahead').typeahead( ";
        js += "     { ";
        js += "          limit: Infinity,";
        js += "          minLength: 1,";
        js += "          hint:false";
        js += "      },";
        js += "     {";
        js += "        name : 's-select-typeahead', ";
        js += "        display: 'value', ";
        js += "        limit: Infinity,";// não limita os resultados exibidos
        js += "        source: " + createJSBloodhoundOpbject();
        js += "     }";
        js += " );";
        js += " $('#" + container.getMarkupId() + " .typeahead').on('typeahead:selected', function(event, selection, dataset) {  ";
        js += "     $('#" + valueField.getMarkupId(true) + "').val(selection.key);";
        js += "     $('#" + valueField.getMarkupId(true) + "').trigger('change');";
        js += " });";
        js += " SingularTypeahead.configure('" + container.getMarkupId() + "','" + valueField.getMarkupId() + "');";
        return js;
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

        final Map<String, String> map = newLinkedHashMap();
        final SInstance instance = model.getObject();
        final Provider<Serializable, SInstance> provider = instance.asAtrProvider().getProvider();
        final ProviderContext<SInstance> providerContext = new ProviderContext<>();

        providerContext.setInstance(instance);
        providerContext.setQuery(StringUtils.EMPTY);

        if (provider != null) {
            for (Object o : provider.load(providerContext)) {
                final String key = String.valueOf(instance.asAtrProvider().getIdFunction().apply(o));
                final String display = instance.asAtrProvider().getDisplayFunction().apply((Serializable) o);
                map.put(key, display);
                cache.put(key, new TypeaheadCache((Serializable) o, display));
            }
        }

        return map;
    }

    private SInstance instance() {
        return IMInstanciaAwareModel.optionalCast(model).map(IMInstanciaAwareModel::getMInstancia).orElse(null);
    }

    private PackageResourceReference resourceRef(String resourceName) {
        return new PackageResourceReference(getClass(), resourceName);
    }
    
    @Override
    protected void onInitialize() {
        super.onInitialize();
        valueField.add($b.attr("style", "display:none;"));
    }

    public TextField<String> getValueField() {
        return valueField;
    }
}

/**
 * Behaviour that implements responses compatible with the Bloodhound fetch library.
 * https://github.com/twitter/typeahead.js/blob/master/doc/bloodhound.md
 *
 * @author Fabricio Buzeto
 */
class BloodhoundDataBehavior extends AbstractDefaultAjaxBehavior {

    private IModel<? extends SInstance> model;
    private IModel<String>              filterModel;
    private Map<String, TypeaheadCache> cache;

    public BloodhoundDataBehavior(IModel<? extends SInstance> model, Map<String, TypeaheadCache> cache) {
        this.model = model;
        this.filterModel = Model.of("");
        this.cache = cache;
    }

    @Override
    public boolean getStatelessHint(Component component) {
        return false;
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
        filterModel.setObject(filter);
        Map<String, String>                     map      = newLinkedHashMap();
        final SInstance                         instance = model.getObject();
        final Provider<Serializable, SInstance> provider = instance.asAtrProvider().getProvider();
        if (provider != null) {
            for (Serializable s : provider.load(ProviderContext.of(instance, filter))) {
                String key     = String.valueOf(instance.asAtrProvider().getIdFunction().apply(s));
                String display = instance.asAtrProvider().getDisplayFunction().apply(s);
                map.put(key, display);
                cache.put(key, new TypeaheadCache(s, display));
            }
        }
        return map;
    }

    protected RequestCycle requestCycle() {
        return getComponent().getRequestCycle();
    }

    public IModel<String> getFilterModel() {
        return filterModel;
    }

}

class TypeaheadCache implements Serializable {

    private final Serializable trueValue;
    private final String       display;

    TypeaheadCache(Serializable trueValue, String display) {
        this.trueValue = trueValue;
        this.display = display;
    }

    public Serializable getTrueValue() {
        return trueValue;
    }

    public String getDisplay() {
        return display;
    }
}