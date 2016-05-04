package br.net.mirante.singular.form.wicket.mapper.selection;

import br.net.mirante.singular.form.mform.STypeComposite;
import br.net.mirante.singular.form.mform.basic.view.SViewAutoComplete;
import br.net.mirante.singular.form.mform.core.STypeString;
import br.net.mirante.singular.form.mform.provider.TextQueryProvider;
import br.net.mirante.singular.form.wicket.helpers.SingularFormBaseTest;
import org.apache.wicket.Component;
import org.apache.wicket.request.Url;
import org.hamcrest.Matchers;
import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.Assert;
import org.junit.Test;
import org.skyscreamer.jsonassert.JSONAssert;

import java.util.ArrayList;
import java.util.List;

import static org.fest.assertions.api.Assertions.assertThat;
import static org.junit.Assert.assertEquals;

public class BloodhoundDataBehaviorTest extends SingularFormBaseTest {

    STypeString string;

    private void executeBloodhoundDataBehavior() {
        executeBloodhoundDataBehavior(null);
    }

    private void executeBloodhoundDataBehavior(String query) {
        final Component                    typeaheadComponent      =  findFirstFormComponentsByType(page.getForm(), string).getParent().getParent();
        final List<BloodhoundDataBehavior> bloodhoundDataBehaviors = typeaheadComponent.getBehaviors(BloodhoundDataBehavior.class);
        Assert.assertThat("O componente possui mais de um ou nenhum BloodhoundDataBehavior", bloodhoundDataBehaviors, Matchers.hasSize(1));
        String url = String.valueOf(bloodhoundDataBehaviors.get(0).getCallbackUrl());
        if (query != null) {
            url += "&filter=" + query;
        }
        tester.executeAjaxUrl(Url.parse(url));
    }

    @Test
    public void setsEncoding() {
        executeBloodhoundDataBehavior();
        assertThat(tester.getLastResponse().getContentType()).contains("application/json");
        assertThat(tester.getLastResponse().getContentType()).contains("charset=utf-8");
    }

    @Test
    public void returnOptions() {
        executeBloodhoundDataBehavior();
        JSONArray expected = new JSONArray();
        expected.put(createValue("@gmail.com", "@gmail.com"));
        expected.put(createValue("@hotmail.com", "@hotmail.com"));
        expected.put(createValue("@yahoo.com", "@yahoo.com"));
        JSONAssert.assertEquals(expected, new JSONArray(tester.getLastResponseAsString()), false);
    }

    @Test
    public void applyFilterToOptions() {
        executeBloodhoundDataBehavior("bruce");
        JSONArray expected = new JSONArray();
        expected.put(createValue("1", "bruce@gmail.com"));
        expected.put(createValue("2", "bruce@hotmail.com"));
        expected.put(createValue("3", "bruce@yahoo.com"));
        final JSONArray array = new JSONArray(tester.getLastResponseAsString());
        assertEquals(3, array.length());
        assertEquals("bruce@gmail.com", ((JSONObject)array.get(0)).get("value"));
        assertEquals("bruce@hotmail.com", ((JSONObject)array.get(1)).get("value"));
        assertEquals("bruce@yahoo.com", ((JSONObject)array.get(2)).get("value"));
    }

    private JSONObject createValue(String key, String v) {
        JSONObject value = new JSONObject();
        value.accumulate("key", key);
        value.accumulate("value", v);
        return value;
    }

    @Override
    protected void buildBaseType(STypeComposite<?> baseType) {
        string = baseType.addFieldString("string");
        string.selectionOf(String.class).selfIdAndDisplay().filteredProvider(createProvider());
        string.withView(new SViewAutoComplete(SViewAutoComplete.Mode.DYNAMIC));
    }

    private TextQueryProvider createProvider() {
        return (TextQueryProvider) (instance, filter) -> {
            if (filter == null) filter = "";
            final List<String> emails = new ArrayList<>();
            emails.add(filter + "@gmail.com");
            emails.add(filter + "@hotmail.com");
            emails.add(filter + "@yahoo.com");
            return emails;
        };
    }
}
