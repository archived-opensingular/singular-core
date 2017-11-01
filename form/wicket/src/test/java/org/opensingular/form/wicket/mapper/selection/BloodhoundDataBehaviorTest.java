/*
 *
 *  * Copyright (C) 2016 Singular Studios (a.k.a Atom Tecnologia) - www.opensingular.com
 *  *
 *  * Licensed under the Apache License, Version 2.0 (the "License");
 *  *  you may not use this file except in compliance with the License.
 *  * You may obtain a copy of the License at
 *  *
 *  * http://www.apache.org/licenses/LICENSE-2.0
 *  *
 *  * Unless required by applicable law or agreed to in writing, software
 *  * distributed under the License is distributed on an "AS IS" BASIS,
 *  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  * See the License for the specific language governing permissions and
 *  * limitations under the License.
 *
 */

package org.opensingular.form.wicket.mapper.selection;

import org.apache.wicket.Component;
import org.apache.wicket.request.Url;
import org.hamcrest.Matchers;
import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.opensingular.form.STypeComposite;
import org.opensingular.form.provider.TextQueryProvider;
import org.opensingular.form.type.core.STypeString;
import org.opensingular.form.view.SViewAutoComplete;
import org.opensingular.form.wicket.helpers.SingularDummyFormPageTester;
import org.skyscreamer.jsonassert.JSONAssert;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertEquals;

public class BloodhoundDataBehaviorTest {

    private SingularDummyFormPageTester tester;
    private static STypeString string;

    @Before
    public void setUp(){
        tester = new SingularDummyFormPageTester();
        tester.getDummyPage().setTypeBuilder(BloodhoundDataBehaviorTest::buildBaseType);
        tester.startDummyPage();
    }

    private void executeBloodhoundDataBehavior() {
        executeBloodhoundDataBehavior(null);
    }

    private void executeBloodhoundDataBehavior(String query) {
        final Component typeaheadComponent = tester.getAssertionsForm().getSubComponents(TypeaheadComponent.class).element(0).getTarget();
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
        assertThat(tester.getLastResponse().getContentType()).contains("charset="+ StandardCharsets.UTF_8.name());
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

    private static void buildBaseType(STypeComposite<?> baseType) {
        string = baseType.addFieldString("string");
        string.selectionOf(String.class).selfIdAndDisplay().filteredProvider(createProvider());
        string.withView(new SViewAutoComplete(SViewAutoComplete.Mode.DYNAMIC));
    }

    private static TextQueryProvider createProvider() {
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
