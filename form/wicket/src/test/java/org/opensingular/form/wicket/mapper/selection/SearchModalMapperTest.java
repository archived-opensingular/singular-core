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

import org.apache.commons.lang3.StringUtils;
import org.apache.wicket.Component;
import org.apache.wicket.markup.html.form.Button;
import org.apache.wicket.markup.html.form.TextField;
import org.junit.Test;
import org.opensingular.form.SInstance;
import org.opensingular.form.STypeComposite;
import org.opensingular.form.provider.Config;
import org.opensingular.form.provider.FilteredProvider;
import org.opensingular.form.provider.ProviderContext;
import org.opensingular.form.type.core.STypeString;
import org.opensingular.form.view.SViewSearchModal;
import org.opensingular.form.wicket.helpers.AssertionsWComponentList;
import org.opensingular.form.wicket.helpers.SingularDummyFormPageTester;
import org.opensingular.form.wicket.mapper.search.SearchModalPanel;
import org.opensingular.lib.wicket.util.ajax.ActionAjaxLink;

import java.util.Arrays;
import java.util.List;

import static org.opensingular.form.wicket.AjaxUpdateListenersFactory.SINGULAR_PROCESS_EVENT;


public class SearchModalMapperTest {

    private static STypeString mandatoryField;
    private static STypeString dependentField;

    private static void buildBaseType(STypeComposite<?> baseType) {
        mandatoryField = baseType.addFieldString("mandatoryField", true);

        mandatoryField.withView(new SViewSearchModal());
        mandatoryField.asAtrProvider().filteredProvider(new FilteredProvider<String>() {
            @Override
            public void configureProvider(Config cfg) {
                cfg.getFilter().addFieldString("search");
                cfg.result().addColumn("String");
            }

            @Override
            public List<String> load(ProviderContext<SInstance> context) {
                return Arrays.asList("1", "2");
            }
        });
        dependentField = baseType.addFieldString("dependentField");
        dependentField.asAtr().dependsOn(mandatoryField);
        dependentField.asAtr()
                .visible(ins -> StringUtils.isNotEmpty(ins.findNearestValue(mandatoryField, String.class).orElse(null)));

    }

    @Test
    public void testIfChooseValueInModelUpdatesDependentComponent() {
        SingularDummyFormPageTester tester = new SingularDummyFormPageTester();
        tester.getDummyPage().setTypeBuilder(SearchModalMapperTest::buildBaseType);
        tester.startDummyPage();

        Component mandatoryFieldComp = tester.getAssertionsForm().getSubComponents(TextField.class)
                .element(0).asTextField().getTarget();
        Component dependentFieldComp = tester.getAssertionsForm().getSubComponents(TextField.class)
                .element(1).asTextField().getTarget();

        tester.assertInvisible(dependentFieldComp.getPageRelativePath());

        Button openModalButton = tester.getAssertionsForm()
                .getSubComponentWithId(SearchModalPanel.MODAL_TRIGGER_ID).getTarget(Button.class);
        tester.executeAjaxEvent(openModalButton, "click");

        AssertionsWComponentList links = tester.getAssertionsForm().getSubComponents(ActionAjaxLink.class);
        tester.executeAjaxEvent(links.element(0).getTarget(), "click");

        tester.getAssertionsForm().getSubComponentWithType(mandatoryField).assertSInstance().isValueEquals("1");

        tester.executeAjaxEvent(mandatoryFieldComp, SINGULAR_PROCESS_EVENT);

        tester.assertVisible(dependentFieldComp.getPageRelativePath());
    }
}