package br.net.mirante.singular.form.wicket.test.base;

import java.util.function.Predicate;
import java.util.stream.Stream;

import br.net.mirante.singular.form.mform.SIComposite;
import br.net.mirante.singular.form.mform.document.RefType;
import br.net.mirante.singular.form.mform.document.SDocumentFactory;
import br.net.mirante.singular.form.wicket.helpers.TestFinders;
import org.apache.commons.lang3.StringUtils;
import org.apache.wicket.Component;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.FormComponent;
import org.apache.wicket.util.tester.FormTester;
import org.apache.wicket.util.tester.WicketTester;
import org.junit.Before;

import br.net.mirante.singular.form.mform.SType;
import br.net.mirante.singular.form.mform.STypeComposite;

import static br.net.mirante.singular.form.wicket.helpers.TestFinders.*;


public abstract class AbstractSingularFormTest {

    protected MockPage page;
    protected WicketTester tester;
    protected FormTester form;

    protected abstract void populateMockType(STypeComposite<?> mockType);

    @Before
    public void setUp() {
        tester = new WicketTester();
        tester.startPage(page = new MockPage() {
            @Override
            protected void populateType(STypeComposite<?> mockType) {
                populateMockType(mockType);
            }
        });
        form = tester.newFormTester("form");
    }

    protected String getFormRelativePath(FormComponent<?> c) {
        return c.getPath().replace(c.getForm().getRootForm().getPath() + ":", StringUtils.EMPTY);
    }

    protected Stream<FormComponent> findFormComponentsByType(SType type) {
        return findFormComponentsByType(form.getForm(), type);
    }

    protected static Stream<FormComponent> findFormComponentsByType(Form form, SType type){
        return TestFinders.findFormComponentsByType(form, type);
    }

    protected static <T extends Component> Stream<T> findOnForm(Class<T> classOfQuery, Form form, Predicate<T> predicate) {
        return  TestFinders.findOnForm(classOfQuery, form, predicate);
    }

    protected static String formField(FormTester form, String leafName) {
        return "form:" + findId(form.getForm(), leafName).get();
    }

    protected SIComposite createInstance(final SType x) {
        SDocumentFactory factory = page.mockFormConfig.getDocumentFactory();
        return (SIComposite) factory.createInstance(new RefType() {
            @Override
            protected SType<?> retrieve() {
                return x;
            }
        });
    }
}
