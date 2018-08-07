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

package org.opensingular.internal.lib.wicket.test;

import org.apache.wicket.Component;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.util.file.File;
import org.apache.wicket.util.tester.FormTester;

public class SingularFormTester extends FormTester {

    private AbstractWicketTester<?> tester;

    protected SingularFormTester(String path, Form<?> workingForm, AbstractWicketTester<?> wicketTester, boolean fillBlankString) {
        super(path, workingForm, wicketTester, fillBlankString);
        this.tester = wicketTester;
    }

    public FormTester select(String formComponentId, int index) {
        String formRelativeComponentId = getFormRelativeComponentId(formComponentId);
        return super.select(formRelativeComponentId, index);
    }

    @Override
    public String getTextComponentValue(String id) {
        String formRelativeComponentId = getFormRelativeComponentId(id);
        return super.getTextComponentValue(formRelativeComponentId);
    }

    @Override
    public FormTester selectMultiple(String formComponentId, int[] indexes) {
        String formRelativeComponentId = getFormRelativeComponentId(formComponentId);
        return super.selectMultiple(formRelativeComponentId, indexes);
    }

    @Override
    public FormTester selectMultiple(String formComponentId, int[] indexes, boolean replace) {
        String formRelativeComponentId = getFormRelativeComponentId(formComponentId);
        return super.selectMultiple(formRelativeComponentId, indexes, replace);
    }

    @Override
    public FormTester setValue(String formComponentId, String value) {
        String formRelativeComponentId = getFormRelativeComponentId(formComponentId);
        return super.setValue(formRelativeComponentId, value);
    }

    @Override
    public FormTester setValue(String checkBoxId, boolean value) {
        String formRelativeComponentId = getFormRelativeComponentId(checkBoxId);
        return super.setValue(formRelativeComponentId, value);
    }

    @Override
    public FormTester setFile(String formComponentId, File file, String contentType) {
        String formRelativeComponentId = getFormRelativeComponentId(formComponentId);
        return super.setFile(formRelativeComponentId, file, contentType);
    }

    @Override
    public FormTester submit(String buttonComponentId) {
        String formRelativeComponentId = getFormRelativeComponentId(buttonComponentId);
        return super.submit(formRelativeComponentId);
    }

    private String getFormRelativeComponentId(String formComponentId) {
        AbstractAssertionsForWicket<?,?,?,?> aC = tester.getAssertionsPage().getSubComponentWithId(formComponentId);
        Component formComponent = aC.getTarget(Component.class);

        String path = formComponent.getPath();
        String formPath = getForm().getPath() + ":";
        return path.replace(formPath, "");
    }


}
