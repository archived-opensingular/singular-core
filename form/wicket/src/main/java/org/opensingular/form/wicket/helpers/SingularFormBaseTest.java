/*
 * Copyright (C) 2016 Singular Studios (a.k.a Atom Tecnologia) - www.opensingular.com
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
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

package org.opensingular.form.wicket.helpers;

import org.opensingular.form.SIComposite;
import org.opensingular.form.SInstance;
import org.opensingular.form.SType;
import org.opensingular.form.STypeComposite;
import org.opensingular.form.SingularFormException;
import org.opensingular.form.document.RefType;
import org.opensingular.form.document.SDocumentFactory;
import org.opensingular.form.wicket.model.ISInstanceAwareModel;

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
    protected <I extends SInstance> Stream<ISInstanceAwareModel<I>> findModelsByType(SType<I> type) {
        return findFormComponentsByType(type)
            .map(it -> it.getModel())
            .filter(it -> it instanceof ISInstanceAwareModel)
            .map(it -> (ISInstanceAwareModel<I>) it);
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
        return "form:" + TestFinders.findId(form.getForm(), leafName).get();
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
