package br.net.mirante.singular.form.wicket.helpers;

import br.net.mirante.singular.form.SIComposite;
import br.net.mirante.singular.form.SInstance;
import br.net.mirante.singular.form.SType;
import br.net.mirante.singular.form.STypeComposite;
import br.net.mirante.singular.form.SingularFormException;
import br.net.mirante.singular.form.document.RefType;
import br.net.mirante.singular.form.document.SDocumentFactory;
import br.net.mirante.singular.form.wicket.model.IMInstanciaAwareModel;

import org.apache.commons.lang3.StringUtils;
import org.apache.wicket.Component;
import org.apache.wicket.markup.html.form.AbstractChoice;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.FormComponent;
import org.apache.wicket.util.tester.FormTester;
import org.apache.wicket.util.tester.WicketTester;
import org.junit.Before;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Stream;

import static br.net.mirante.singular.form.wicket.helpers.TestFinders.findId;

public abstract class SingularFormBaseTest {

    protected DummyPage    page;
    protected WicketTester tester;
    protected FormTester   form;

    protected abstract void buildBaseType(STypeComposite<?> baseType);

    protected void populateInstance(SIComposite instance) {}

    @Before
    public void setUp() {
        tester = new WicketTester();
        tester.getApplication().getMarkupSettings().setDefaultMarkupEncoding("utf-8");
        page = new DummyPage();
        page.setTypeBuilder(this::buildBaseType);
        page.setInstanceCreator(this::createAndPopulateInstance);
        tester.startPage(page);
        form = tester.newFormTester("form");
    }

    protected SIComposite baseInstance() {
        return page.getCurrentInstance();
    }

    protected String getFormRelativePath(FormComponent<?> c) {
        return c.getPath().replace(c.getForm().getRootForm().getPath() + ":", StringUtils.EMPTY);
    }

    @SuppressWarnings("unchecked")
    protected <I extends SInstance> Stream<IMInstanciaAwareModel<I>> findModelsByType(SType<I> type) {
        return findFormComponentsByType(type)
            .map(it -> it.getModel())
            .filter(it -> it instanceof IMInstanciaAwareModel)
            .map(it -> (IMInstanciaAwareModel<I>) it);
    }

    protected Stream<FormComponent> findFormComponentsByType(SType type) {
        return findFormComponentsByType(form.getForm(), type);
    }

    protected static Stream<FormComponent> findFormComponentsByType(Form form, SType type) {
        return TestFinders.findFormComponentsByType(form, type);
    }

    protected static FormComponent findFirstFormComponentsByType(Form form, SType type) {
        return TestFinders.findFormComponentsByType(form, type).findFirst().orElseThrow(() -> new SingularFormException("Não foi possivel encontrar"));
    }

    protected static <T extends Component> Stream<T> findOnForm(Class<T> classOfQuery, Form form, Predicate<T> predicate) {
        return TestFinders.findOnForm(classOfQuery, form, predicate);
    }

    protected static String formField(FormTester form, String leafName) {
        return "form:" + findId(form.getForm(), leafName).get();
    }

    protected SIComposite createInstance(final SType x) {
        SDocumentFactory factory = page.mockFormConfig.getDocumentFactory();
        RefType refType = new RefType() {
            protected SType<?> retrieve() {
                return x;
            }
        };
        return (SIComposite) factory.createInstance(refType);
    }

    protected SIComposite createAndPopulateInstance(final SType x) {
        SIComposite instance = createInstance(x);
        populateInstance(instance);
        return instance;
    }

    protected void ajaxClick(Component target) {
        tester.executeAjaxEvent(target, "click");
    }

    public List<String> getkeysFromSelection(AbstractChoice choice) {
        final List<String> list = new ArrayList<>();
        for (Object c : choice.getChoices()) {
            list.add(choice.getChoiceRenderer().getIdValue(c, choice.getChoices().indexOf(c)));
        }
        return list;
    }

    public List<String> getDisplaysFromSelection(AbstractChoice choice) {
        final List<String> list = new ArrayList<>();
        for (Object c : choice.getChoices()) {
            list.add(String.valueOf(choice.getChoiceRenderer().getDisplayValue(c)));
        }
        return list;
    }
}
