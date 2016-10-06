package org.opensingular.singular.showcase.view.page.prototype;

import org.opensingular.form.SDictionary;
import org.opensingular.form.SIComposite;
import org.opensingular.form.SIList;
import org.opensingular.form.SType;
import org.opensingular.form.context.SFormConfig;
import org.opensingular.form.document.RefType;
import org.opensingular.form.document.SDocumentFactory;
import org.opensingular.form.internal.xml.MElement;
import org.opensingular.form.io.MformPersistenciaXML;
import org.opensingular.singular.form.showcase.view.page.prototype.PrototypeContent;
import org.opensingular.singular.form.showcase.view.page.prototype.PrototypePage;
import org.opensingular.singular.form.showcase.view.page.prototype.SPackagePrototype;
import org.opensingular.singular.showcase.SpringWicketTester;
import org.opensingular.singular.form.showcase.dao.form.Prototype;
import org.opensingular.singular.form.showcase.view.template.Content;
import org.apache.wicket.markup.html.link.AbstractLink;
import org.apache.wicket.util.string.StringValue;
import org.apache.wicket.util.tester.FormTester;
import org.apache.wicket.util.tester.WicketTester;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.experimental.runners.Enclosed;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import javax.inject.Inject;
import javax.inject.Named;

import static org.opensingular.singular.form.wicket.helpers.TestFinders.findOnForm;
import static org.fest.assertions.api.Assertions.assertThat;

@Ignore("Problems with maven environment")
@RunWith(Enclosed.class)
public class PrototypePageTest {

    @RunWith(SpringJUnit4ClassRunner.class)
    @ContextConfiguration(locations = {"/applicationContext.xml"})
    public static class Base {
        @Inject
        private SpringWicketTester springWicketTester;
        @Inject @Named("formConfigWithDatabase")
        private SFormConfig<String> singularFormConfig;
        private WicketTester        driver;
        private SDictionary         dictionary;
        private SIComposite         currentInstance;
        private FormTester          form;

        @Before
        public void setup() {
            driver = springWicketTester.wt();
            dictionary = SDictionary.create();
            dictionary.loadPackage(SPackagePrototype.class);
            createInstance();
        }

        private void createInstance() {
            SDocumentFactory documentFactory = singularFormConfig.getDocumentFactory();
            currentInstance = (SIComposite) documentFactory.createInstance(new RefType() {
                protected SType<?> retrieve() {
                    return dictionary.getType(SPackagePrototype.META_FORM_COMPLETE);
                }
            });
        }

        @Test
        public void rendersPrototypeOnScreen() {

            SIList campo = (SIList) currentInstance.getField(SPackagePrototype.CHILDREN);
            assertThat(campo).isNotNull();

            SIComposite field = (SIComposite) campo.addNew();

            field.getField(SPackagePrototype.NAME).setValue("Abacate");

            startPage();

            driver.assertContains("Abacate");
        }

        private AbstractLink findMasterDetailLink() {
            return findOnForm(AbstractLink.class, form.getForm(), (b) -> true)
                    .findFirst()
                    .orElseThrow(() -> new RuntimeException("Não foi possivel encontrar o botão de adicionar do mestre detalhe"));
        }

        private void startPage() {
            driver.startPage(new PrototypePage() {
                @Override
                protected Content getContent(String id) {
                    return createDummyContent(id);
                }

                @Override
                protected void onInitialize() {
                    super.onInitialize();
                }

                private PrototypeContent createDummyContent(final String id) {
                    return new PrototypeContent(id, StringValue.valueOf("")) {
                        @Override
                        protected void loadOrBuildModel() {
                            this.prototype = new Prototype();
                            MElement xml = MformPersistenciaXML.toXML(currentInstance);
                            if (xml != null) {
                                this.prototype.setXml(xml.toStringExato());
                            }
                        }
                    };
                }

            });
        }
    }

}
