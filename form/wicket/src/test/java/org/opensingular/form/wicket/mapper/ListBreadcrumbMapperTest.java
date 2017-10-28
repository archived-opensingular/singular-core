/*
 * Copyright (C) 2016 Singular Studios (a.k.a Atom Tecnologia) - www.opensingular.com
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
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

package org.opensingular.form.wicket.mapper;

import org.junit.Test;
import org.opensingular.form.SIComposite;
import org.opensingular.form.STypeComposite;
import org.opensingular.form.STypeList;
import org.opensingular.form.type.core.STypeString;
import org.opensingular.form.view.SViewBreadcrumb;
import org.opensingular.form.wicket.helpers.AssertionsWComponent;
import org.opensingular.form.wicket.helpers.SingularDummyFormPageTester;

/**
 * @author Daniel C. Bordin on 27/03/2017.
 */
public class ListBreadcrumbMapperTest {

    @Test
    public void testEditRendering() {
        SingularDummyFormPageTester ctx = new SingularDummyFormPageTester();
        ctx.getDummyPage().setTypeBuilder(ListBreadcrumbMapperTest::createSimpleForm);
        ctx.getDummyPage().setAsEditView();
        ctx.startDummyPage();

        AssertionsWComponent cmpExpProf = ctx.getAssertionsPage().getSubComponentWithTypeNameSimple(
                "experienciasProfissionais").isNotNull();
        ctx.getAssertionsInstance().isList("experienciasProfissionais", 0);

        //add a item in the breadCrum
        ctx.clickLink(cmpExpProf.getSubComponentWithId("_add").getTarget());
        ctx.getAssertionsInstance().isList("experienciasProfissionais", 1);

        //Cancel sub tela
        ctx.executeAjaxEvent(ctx.getAssertionsForSubComp("cancelButton").getTarget(), "onclick");
        ctx.getAssertionsInstance().isList("experienciasProfissionais", 0);

        //add a item in the breadCrum
        ctx.clickLink(ctx.getAssertionsForSubComp("_add").getTarget());
        ctx.getAssertionsInstance().isList("experienciasProfissionais", 1);

        //Submit sub tela
        ctx.executeAjaxEvent(ctx.getAssertionsForSubComp("okButton").getTarget(), "onclick");
        ctx.getAssertionsInstance().isList("experienciasProfissionais", 1);
    }

    private static void createSimpleForm(STypeComposite testForm) {

        testForm.addFieldString("nome", true).asAtr().label("Nome");

        STypeList<STypeComposite<SIComposite>, SIComposite> experiencias = testForm.addFieldListOfComposite(
                "experienciasProfissionais", "experiencia");
        STypeComposite<?> experiencia = experiencias.getElementsType();
        STypeString empresa = experiencia.addFieldString("empresa", true);
        STypeString cargo = experiencia.addFieldString("cargo", true);

        experiencias.withView(SViewBreadcrumb::new).asAtr().label("Experiências profissionais");
        empresa.asAtr().label("Empresa").asAtrBootstrap().colPreference(8);
        cargo.asAtr().label("Cargo");

    }
}
