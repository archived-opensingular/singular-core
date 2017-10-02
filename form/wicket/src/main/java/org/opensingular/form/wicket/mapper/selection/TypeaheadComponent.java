/*
 * Copyright (C) 2016 Singular Studios (a.k.a Atom Tecnologia) - www.opensingular.com
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.opensingular.form.wicket.mapper.selection;

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
import org.opensingular.form.SInstance;
import org.opensingular.form.SingularFormException;
import org.opensingular.form.converter.SInstanceConverter;
import org.opensingular.form.provider.Provider;
import org.opensingular.form.provider.ProviderContext;
import org.opensingular.form.util.transformer.Value;
import org.opensingular.form.view.SViewAutoComplete;
import org.opensingular.form.wicket.model.AbstractSInstanceAwareModel;
import org.opensingular.form.wicket.model.ISInstanceAwareModel;
import org.opensingular.form.wicket.util.WicketFormProcessing;
import org.opensingular.lib.commons.lambda.IFunction;
import org.opensingular.lib.wicket.util.template.SingularTemplate;

import javax.annotation.Nonnull;
import java.io.Serializable;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Stream;

import static com.google.common.collect.Maps.newLinkedHashMap;
import static org.opensingular.form.wicket.mapper.selection.TypeaheadComponent.generateResultOptions;
import static org.opensingular.lib.wicket.util.util.WicketUtils.$b;


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

    public final CssReferenceHeaderItem CSS_REFERENCE = CssReferenceHeaderItem.forReference(new PackageResourceReference(TypeaheadComponent.class, "TypeaheadComponent.css") {
        @Override
        public List<HeaderItem> getDependencies() {
            if (getPage() instanceof SingularTemplate) {
                return ((SingularTemplate) getPage()).getStyles();
            } else {
                return Collections.emptyList();
            }
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
        container = buildContainer();
        add(container);
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
        labelField = makeLabelField();
        c.add(labelField);
        valueField = makeValueField();
        c.add(valueField);
        dynamicFetcher = new BloodhoundDataBehavior(model, cache);
        add(dynamicFetcher);
        return c;
    }

    @Nonnull
    private TextField<String> makeValueField() {
        return new TextField<>("value_field", makeValueModel());
    }

    @Nonnull
    private AbstractSInstanceAwareModel<String> makeValueModel() {
        return new AbstractSInstanceAwareModel<String>() {

            private String lastId;
            private Serializable lastValue;

            @Override
            public SInstance getSInstance() {
                return ISInstanceAwareModel.optionalSInstance(model).orElse(null);
            }

            @Override
            public String getObject() {
                if (instance().isEmptyOfData()) {
                    return null;
                }
                if (!Value.dehydrate(instance()).equals(lastValue)) {
                    lastValue = Value.dehydrate(instance());
                    final IFunction<Object, Object>                   idFunction = instance().asAtrProvider().getIdFunction();
                    final SInstanceConverter<Serializable, SInstance> converter  = instance().asAtrProvider().getConverter();
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
                    getRequestCycle().setMetaData(WicketFormProcessing.MDK_SKIP_VALIDATION_ON_REQUEST, Boolean.TRUE);
                    getSInstance().clearInstance();
                } else {
                    setVallIfNullorClear(key, getSInstance());
                }
            }
        };
    }

    protected void setVallIfNullorClear(String key, SInstance instance) {
        final Serializable val = getValueFromChace(key).map(TypeaheadCache::getTrueValue).orElse(getValueFromProvider(key).orElse(null));
        if (val != null) {
            instance().asAtrProvider().getConverter().fillInstance(instance, val);
        } else {
            instance.clearInstance();
        }
    }

    @Nonnull
    private TextField<String> makeLabelField() {
        return new TextField<>("label_field", new Model<String>() {

            private String lastDisplay;
            private Serializable lastValue;

            @Override
            public String getObject() {
                if (instance().isEmptyOfData()) {
                    return null;
                } else if (!Value.dehydrate(instance()).equals(lastValue)) {
                    lastValue = Value.dehydrate(instance());
                    SInstanceConverter<Serializable, SInstance> converter = instance().asAtrProvider().getConverter();
                    if (converter != null) {
                        Serializable converted = converter.toObject(instance());
                        if (converted != null) {
                            lastDisplay = instance().asAtrProvider().getDisplayFunction().apply(converted);
                        }
                    }
                }
                return lastDisplay;
            }
        });
    }

    private Optional<TypeaheadCache> getValueFromChace(String key) {
        return Optional.ofNullable(cache.get(key));
    }

    private Optional<Serializable> getValueFromProvider(String key) {

        final Stream<Serializable>              stream;
        final Provider<Serializable, SInstance> provider        = instance().asAtrProvider().getProvider();
        final ProviderContext<SInstance>        providerContext = new ProviderContext<>();

        providerContext.setInstance(instance());

        if (dynamicFetcher != null) {
            providerContext.setQuery(dynamicFetcher.getFilterModel().getObject());
        } else {
            providerContext.setQuery(StringUtils.EMPTY);
        }

        if (provider != null) {
            stream = provider.load(providerContext).stream();
        } else {
            throw new SingularFormException("Nenhum provider foi informado", instance());
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
        js += " $('#" + labelField.getMarkupId() + "').val('" + ObjectUtils.defaultIfNull(Optional.ofNullable(labelField.getModel()).map((x) -> x.getObject()).orElse(null), "") + "');";
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
        js += "     $('#" + valueField.getMarkupId(true) + "').trigger('blur');";
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

        final Map<String, String>               map             = newLinkedHashMap();
        final SInstance                         instance        = model.getObject();
        final Provider<Serializable, SInstance> provider        = instance.asAtrProvider().getProvider();
        final ProviderContext<SInstance>        providerContext = new ProviderContext<>();

        providerContext.setInstance(instance);
        providerContext.setQuery(StringUtils.EMPTY);

        if (provider != null) {
            for (Object o : provider.load(providerContext)) {
                final String key     = String.valueOf(instance.asAtrProvider().getIdFunction().apply(o));
                final String display = instance.asAtrProvider().getDisplayFunction().apply((Serializable) o);
                map.put(key, display);
                cache.put(key, new TypeaheadCache((Serializable) o, display));
            }
        }

        return map;
    }

    private SInstance instance() {
        return ISInstanceAwareModel.optionalSInstance(model).orElse(null);
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
                new TextRequestHandler("application/json", StandardCharsets.UTF_8.name(), generateResultOptions(values(filterValue()))));
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