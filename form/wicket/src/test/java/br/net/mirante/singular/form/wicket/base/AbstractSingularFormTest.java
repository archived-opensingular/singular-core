package br.net.mirante.singular.form.wicket.base;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Stream;

import org.apache.commons.lang3.StringUtils;
import org.apache.wicket.Component;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.FormComponent;
import org.apache.wicket.util.tester.FormTester;
import org.apache.wicket.util.tester.WicketTester;
import org.apache.wicket.util.visit.IVisit;
import org.apache.wicket.util.visit.IVisitor;
import org.junit.Before;
import org.junit.Test;

import br.net.mirante.singular.form.mform.SType;
import br.net.mirante.singular.form.mform.STypeComposite;
import br.net.mirante.singular.form.wicket.model.IMInstanciaAwareModel;
import junit.framework.Assert;


public abstract class AbstractSingularFormTest {

    protected MockPage mockPage;
    protected WicketTester wicketTester;
    protected FormTester formTester;

    protected abstract void populateMockType(STypeComposite<?> mockType);

    @Before
    public void setUp() {
        wicketTester = new WicketTester();
        wicketTester.startPage(mockPage = new MockPage() {
            @Override
            protected void populateType(STypeComposite<?> mockType) {
                populateMockType(mockType);
            }
        });
        formTester = wicketTester.newFormTester("form");
    }

    @Test
    public void asserRendering() {
        wicketTester.assertRenderedPage(MockPage.class);
        Assert.assertNotNull(formTester);
    }

    protected String getFormRelativePath(FormComponent<?> c) {
        return c.getPath().replace(c.getForm().getRootForm().getPath() + ":", StringUtils.EMPTY);
    }

    protected <T extends Component> Stream<T> findOnForm(Class<T> classOfQuery, Form form, Predicate<T> predicate) {
        final List<T> found = new ArrayList<>();
        form.visitChildren(classOfQuery, new IVisitor<T, Object>() {
            @Override
            public void component(T t, IVisit<Object> visit) {
                if (predicate.test(t)) {
                    found.add(t);
                }
            }
        });
        return found.stream();
    }

    protected Stream<FormComponent> findFormComponentsByType(Form form, SType type) {
        return findOnForm(FormComponent.class, form, fc -> IMInstanciaAwareModel
                .optionalCast(fc.getDefaultModel())
                .map((ins) -> type.equals(ins.getMInstancia().getType()))
                .orElse(false));
    }


}
