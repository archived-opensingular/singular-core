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

package org.opensingular.form.wicket.panel;

import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.mock.MockApplication;
import org.apache.wicket.spring.injection.annot.SpringComponentInjector;
import org.apache.wicket.util.tester.WicketTester;
import org.junit.Before;
import org.junit.Test;
import org.opensingular.form.context.SFormConfig;
import org.opensingular.form.document.RefType;
import org.opensingular.form.type.util.STypeEMail;
import org.opensingular.form.wicket.enums.AnnotationMode;
import org.opensingular.form.wicket.enums.ViewMode;
import org.opensingular.form.wicket.helpers.MockFormConfig;
import org.opensingular.form.wicket.helpers.SingularWicketTester;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.support.GenericApplicationContext;

import javax.inject.Inject;
import javax.inject.Named;
import java.util.function.Supplier;

/**
 * @author Daniel Bordin on 11/02/2017.
 */
public class SingularFormPanelTest {

    private ConfigurableApplicationContext springContext;
    protected WicketTester                 tester;

    @Before
    public void setUp() {
        springContext = new GenericApplicationContext();
        springContext.getBeanFactory().registerSingleton("formConfig", new MockFormConfig());
        springContext.getBeanFactory().registerSingleton(MyTestService.class.getName(), new MyTestService());
        springContext.refresh();

        MockApplication wicketApplication = new MockApplication();

        wicketApplication.getComponentInstantiationListeners().add(
            new SpringComponentInjector(wicketApplication, springContext, true));

        tester = new SingularWicketTester(wicketApplication);
    }

    @Test
    public void testeWrongSerializationPage() {
        tester.startPage(WrongSerializationPage.class);
        tester.getLastRenderedPage();

        //tester.getApplication().getFrameworkSettings().getSerializer().serialize(tester.getLastRenderedPage());
        //tester.dumpPage();
    }

    public static class WrongSerializationPage extends WebPage {

        private SingularFormPanel   singularFormPanel;

        public Object               o;

        @Inject
        private MyTestService       myTestService;

        @Inject
        @Named("formConfig")
        private SFormConfig<String> formConfig;

        public WrongSerializationPage() {
            o = new Supplier<String>() {
                @Override
                public String get() {
                    return null;
                }
            };
            o = 1;

            myTestService.getInt();

            singularFormPanel = new SingularFormPanel("singularFormPanel");
            singularFormPanel.setInstanceCreator(() -> {
                myTestService.getInt();
                RefType refType = RefType.of(STypeEMail.class);
                return formConfig.getDocumentFactory().createInstance(refType);
            });
            singularFormPanel.setAnnotationMode(AnnotationMode.READ_ONLY);
            singularFormPanel.setViewMode(ViewMode.READ_ONLY);

            add(new Form<>("form")
                .add(singularFormPanel));
        }
    }

    //Serviço não serializável
    public static class MyTestService {

        public int getInt() {
            return 1;
        }
    }
}
