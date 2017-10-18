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

package org.opensingular.form.wicket.test.base;

import org.apache.wicket.Component;
import org.apache.wicket.Page;
import org.apache.wicket.model.IModel;
import org.apache.wicket.protocol.http.WebApplication;
import org.apache.wicket.util.tester.FormTester;
import org.apache.wicket.util.tester.WicketTester;
import org.fest.assertions.api.Assertions;
import org.junit.Before;
import org.junit.Test;
import org.opensingular.form.PackageBuilder;
import org.opensingular.form.SDictionary;
import org.opensingular.form.SIComposite;
import org.opensingular.form.SInstance;
import org.opensingular.form.SType;
import org.opensingular.form.curriculo.SPackageCurriculo;
import org.opensingular.form.document.RefType;
import org.opensingular.form.document.SDocumentFactory;
import org.opensingular.form.type.core.SIString;
import org.opensingular.form.type.core.STypeString;
import org.opensingular.form.wicket.WicketBuildContext;
import org.opensingular.form.wicket.component.SingularFormWicket;
import org.opensingular.form.wicket.enums.ViewMode;
import org.opensingular.form.wicket.helpers.SingularWicketTester;
import org.opensingular.form.wicket.model.SInstanceRootModel;
import org.opensingular.lib.commons.lambda.ISupplier;
import org.opensingular.lib.wicket.util.bootstrap.layout.BSContainer;
import org.opensingular.lib.wicket.util.bootstrap.layout.BSGrid;
import org.opensingular.lib.wicket.util.panel.FormPanel;

import static org.opensingular.lib.wicket.util.util.WicketUtils.findContainerRelativePath;

public class TestFormWicketBuild  {

    WicketTester tester;
    protected SDictionary dictionary;

    @Before
    public void setUpDictionary() {
        dictionary = SDictionary.create();
    }

    @Before
    public void setUp() {
        tester = new SingularWicketTester(false, new WebApplication() {
            @Override
            public Class<? extends Page> getHomePage() {
                return null;
            }
        });
    }

    protected static SInstance createIntance(ISupplier<SType<?>> typeSupplier) {
        return SDocumentFactory.empty().createInstance(RefType.of(typeSupplier));
    }

    @Test
    public void testBasic() {
        BSGrid    rootContainer = new BSGrid("teste");
        TestPanel testPanel     = buildTestPanel(rootContainer);

        SIString instancia = (SIString) createIntance(() -> {
            PackageBuilder pb = dictionary.createNewPackage("teste");
            STypeString tipoCidade = pb.createType("cidade", STypeString.class);
            tipoCidade.asAtr().label("Cidade")/*.editSize(30)*/;
            return tipoCidade;
        });

        IModel<SIString> mCidade = new SInstanceRootModel<SIString>(instancia);
        mCidade.getObject().setValue("Brasilia");
        WicketBuildContext ctx = new WicketBuildContext(rootContainer.newColInRow(), testPanel.getBodyContainer(), mCidade);
        ctx.build(ViewMode.EDIT);

        tester.startComponentInPage(testPanel);
        Assertions.assertThat(mCidade.getObject().getValue()).isEqualTo("Brasilia");

        FormTester formTester = tester.newFormTester("body-child:container:form");
        formTester.setValue(findContainerRelativePath(formTester.getForm(), "cidade").get(), "Guará");
        formTester.submit();

        Assertions.assertThat(mCidade.getObject().getValue()).isEqualTo("Guará");
    }

    @Test
    public void testCurriculo() {
        BSGrid rootContainer = new BSGrid("teste");
        TestPanel testPanel = buildTestPanel(rootContainer);

        SIComposite instancia = (SIComposite) createIntance(() -> {
            dictionary.loadPackage(SPackageCurriculo.class);
            return dictionary.getType(SPackageCurriculo.TIPO_CURRICULO);
        });

        IModel<SIComposite> mCurriculo = new SInstanceRootModel<SIComposite>(instancia);
        WicketBuildContext ctx = new WicketBuildContext(rootContainer.newColInRow(), testPanel.getBodyContainer(), mCurriculo);
//        UIBuilderWicket.buildForEdit(ctx, mCurriculo);


        tester.startComponentInPage(testPanel);
        FormTester formTester = tester.newFormTester("body-child:container:form");
        formTester.submit();
    }

    private TestPanel buildTestPanel(BSGrid rootContainer){
        SingularFormWicket<Object> form = new SingularFormWicket<>("form");

        TestPanel testPanel = new TestPanel("body-child"){
            @Override
            public Component buildContainer(String id) {
                return new FormPanel(id, form) {
                    @Override
                    protected Component newFormBody(String id) {
                        return new BSContainer<>(id).appendTag("div", rootContainer);
                    }
                };
            }
        };
        return testPanel;
    }
}
