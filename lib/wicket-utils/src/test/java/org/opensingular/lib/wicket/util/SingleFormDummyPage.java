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

package org.opensingular.lib.wicket.util;

import java.util.Optional;

import org.apache.wicket.Component;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.util.tester.FormTester;
import org.apache.wicket.util.tester.WicketTester;
import org.opensingular.lib.commons.lambda.IPredicate;
import org.opensingular.lib.wicket.util.util.WicketUtils;

public class SingleFormDummyPage extends WebPage {

    public static final String CONTENT_ID  = "content";
    public static final String FORM_ID     = "form";
    public static final String PARENT_PATH = FORM_ID + ":" + CONTENT_ID + ":";

    public SingleFormDummyPage() {
        add(newForm(FORM_ID)
            .add(newContentPanel(CONTENT_ID)));
    }

    protected Form<Object> newForm(String formId) {
        return new Form<>(formId);
    }

    protected Component newContentPanel(String contentId) {
        return new WebMarkupContainer(contentId);
    }

    public Form<?> topForm() {
        return (Form<?>) get(FORM_ID);
    }
    public WebMarkupContainer contentPanel() {
        return (WebMarkupContainer) get(FORM_ID).get(CONTENT_ID);
    }

    public <C extends Component> Optional<C> findChild(Class<C> componentClass, IPredicate<C> filter) {
        return WicketUtils.findFirstChild(contentPanel(), componentClass, filter);
    }
    public Optional<Component> childById(String id) {
        return findChild(Component.class, it -> id.equals(it.getId()));
    }
    public <C extends Component> Optional<C> childById(Class<C> componentClass, String id) {
        return findChild(componentClass, it -> id.equals(it.getId()));
    }
    public Form<?> topFormInContent() {
        return findChild(Form.class, it -> true).map(it -> (Form<?>) it).get();
    }
    public FormTester newFormTesterForTopForm(WicketTester tester) {
        return tester.newFormTester(topForm().getPageRelativePath());
    }
    public FormTester newFormTesterForTopFormInContent(WicketTester tester) {
        return tester.newFormTester(topFormInContent().getPageRelativePath());
    }
}