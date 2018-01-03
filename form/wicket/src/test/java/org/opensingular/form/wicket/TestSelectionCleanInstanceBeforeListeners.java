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

package org.opensingular.form.wicket;

import com.google.common.collect.Lists;
import org.apache.commons.collections4.MapUtils;
import org.apache.wicket.Component;
import org.apache.wicket.MarkupContainer;
import org.apache.wicket.markup.html.form.FormComponent;
import org.apache.wicket.util.tester.FormTester;
import org.apache.wicket.util.visit.IVisit;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.opensingular.form.SType;
import org.opensingular.form.type.core.SIString;
import org.opensingular.form.type.core.STypeString;
import org.opensingular.form.type.country.brazil.STypeUF;
import org.opensingular.form.wicket.helpers.SingularFormDummyPageTester;

import java.io.Serializable;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import static org.opensingular.form.wicket.AjaxUpdateListenersFactory.*;

public class TestSelectionCleanInstanceBeforeListeners implements Serializable {


    private transient SingularFormDummyPageTester tester;
    private transient STypeUF                     uf;
    private transient STypeString                 pais;
    private transient STypeString                 ecapital;
    private transient String              PAIS_SIMPLE_PROVIDER           = "PAIS_SIMPLE_PROVIDER";
    private transient String              ECAPITAL_UPDATE_LISTENER       = "ECAPITAL_UPDATE_LISTENER";
    private transient Map<String, String> EXECUTION_ORDER_AND_PAIS_VALUE = new HashMap<>();

    @Before
    public void testClear() throws Exception {
        EXECUTION_ORDER_AND_PAIS_VALUE.clear();
        tester = new SingularFormDummyPageTester();
        tester.getDummyPage().setTypeBuilder(root -> {
            uf = (STypeUF) root.addField("uf", STypeUF.class);
            uf.asAtr().label("UF");

            pais = root.addFieldString("pais");
            pais.asAtr().dependsOn(uf).label("Capital do país:");
            pais.selection().selfIdAndDisplay().simpleProvider(siString -> {
                EXECUTION_ORDER_AND_PAIS_VALUE.put(PAIS_SIMPLE_PROVIDER, siString.findNearest(uf.sigla).map(SIString::getValue).orElse(null));
                if (siString.findNearest(uf.sigla).map(SIString::getValue).map("DF"::equalsIgnoreCase).orElse(false)) {
                    return Lists.newArrayList("Brasil");
                }
                return Collections.emptyList();
            });

            ecapital = root.addFieldString("ecapital");
            ecapital
                    .withUpdateListener(si -> {
                        EXECUTION_ORDER_AND_PAIS_VALUE.put(ECAPITAL_UPDATE_LISTENER, si.findNearest(uf.sigla).map(SIString::getValue).orElse(null));
                        if (si.findNearest(pais).map(SIString::getValue).map("Brasil"::equals).orElse(false)) {
                            si.setValue("Brasil está selecionado!");
                        } else {
                            si.clearInstance();
                        }
                    })
                    .asAtr()
                    .label("É capital?")
                    .dependsOn(pais, uf);
        });
        tester.startDummyPage();
    }

    @Test
    public void testChangeUfShouldClearPaisBeforeEcapitalUpdateListener() throws Exception {
        EXECUTION_ORDER_AND_PAIS_VALUE.clear();
        setValueOnFieldOneAndCallAjaxValidate("DF", uf);
        printExecutionOrder();
        Assert.assertEquals(null, EXECUTION_ORDER_AND_PAIS_VALUE.get(ECAPITAL_UPDATE_LISTENER));
    }

    private void setValueOnFieldOneAndCallAjaxValidate(String value, SType<?> field) {
        FormTester       formTester = tester.newFormTester();
        FormComponent<?> component  = findFormComponentForType(field);
        formTester.setValue(component, value);
        callAjaxProcessEvent(component);
    }

    private void printExecutionOrder() {
        MapUtils.verbosePrint(System.out, "Execution order and Pais value", EXECUTION_ORDER_AND_PAIS_VALUE);
    }

    private FormComponent<?> findFormComponentForType(SType<?> field) {
        FormComponent formComponent = null;
        Component     component     = tester.getAssertionsForm().getSubComponentWithType(field).getTarget();
        if (component instanceof FormComponent<?>) {
            formComponent = (FormComponent<?>) component;
        } else if (component instanceof MarkupContainer) {
            MarkupContainer cotainer = (MarkupContainer) component;
            formComponent = cotainer.visitChildren(FormComponent.class, this::visit);
        }
        return formComponent;
    }

    private <S extends Component, R> void visit(S s, IVisit<R> riVisit) {
        if (s instanceof FormComponent) {
            riVisit.stop((R) s);
        }
    }


    private void callAjaxProcessEvent(Component fieldOne) {
        tester.executeAjaxEvent(fieldOne, SINGULAR_PROCESS_EVENT);
    }

}
