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

package org.opensingular.form.studio;

import org.apache.wicket.Component;
import org.apache.wicket.ajax.AbstractDefaultAjaxBehavior;
import org.apache.wicket.request.Url;
import org.apache.wicket.util.tester.FormTester;
import org.apache.wicket.util.tester.WicketTestCase;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.opensingular.form.*;
import org.opensingular.form.wicket.enums.ViewMode;
import org.opensingular.form.wicket.panel.SingularFormPanel;
import org.opensingular.lib.wicket.util.datatable.BSDataTable;

import javax.annotation.Nonnull;
import java.util.List;

public class SingularStudioSimpleCRUDPanelTest extends WicketTestCase {

    private SingularStudioSimpleCRUDTestPage singularStudioSimpleCRUDTestPage;

    @Before
    public void setUp() throws Exception {
        singularStudioSimpleCRUDTestPage = new SingularStudioSimpleCRUDTestPage();
    }

    @Test
    public void testCreateNew() throws Exception {
        tester.startPage(singularStudioSimpleCRUDTestPage);
        addNew();
        assertThat(singularStudioSimpleCRUDTestPage.springFormPersistenceInMemory.loadAll().size(), Matchers.is(1));
    }

    @Test
    public void testCancelCreateNew() throws Exception {
        tester.startPage(singularStudioSimpleCRUDTestPage);
        tester.clickLink("form:crud:container:content:create");
        tester.clickLink("form:crud:container:content:form:cancel");
        tester.assertComponent("form:crud:container:content:table", BSDataTable.class);
    }

    @Test
    public void testRendering() throws Exception {
        tester.startPage(singularStudioSimpleCRUDTestPage);
        tester.assertRenderedPage(SingularStudioSimpleCRUDTestPage.class);
    }

    @Test
    public void testDelete() throws Exception {
        tester.startPage(singularStudioSimpleCRUDTestPage);
        addNew();
        tester.clickLink("form:crud:container:content:table:body:rows:1:cells:1:cell:actions:2:link");
        List<AbstractDefaultAjaxBehavior> behaviors = singularStudioSimpleCRUDTestPage.singularStudioSimpleCRUDPanel.getBehaviors(AbstractDefaultAjaxBehavior.class);
        assertTrue(behaviors.size() == 1);
        tester.executeAjaxUrl(Url.parse(behaviors.get(0).getCallbackUrl()));
        assertThat(singularStudioSimpleCRUDTestPage.springFormPersistenceInMemory.loadAll().size(), Matchers.is(0));
    }

    @Test
    public void testEdit() throws Exception {
        tester.startPage(singularStudioSimpleCRUDTestPage);
        addNew();
        tester.clickLink("form:crud:container:content:table:body:rows:1:cells:1:cell:actions:1:link");
        tester.assertComponent("form:crud:container:content:form:content", SingularFormPanel.class);
        SingularFormPanel singularFormPanel = (SingularFormPanel) tester.getComponentFromLastRenderedPage("form:crud:container:content:form:content");
        Assert.assertEquals(ViewMode.EDIT, singularFormPanel.getViewMode());
    }

    @Test
    public void testView() throws Exception {
        tester.startPage(singularStudioSimpleCRUDTestPage);
        addNew();
        tester.clickLink("form:crud:container:content:table:body:rows:1:cells:1:cell:actions:3:link");
        tester.assertComponent("form:crud:container:content:form:content", SingularFormPanel.class);
        SingularFormPanel singularFormPanel = (SingularFormPanel) tester.getComponentFromLastRenderedPage("form:crud:container:content:form:content");
        Assert.assertEquals(ViewMode.READ_ONLY, singularFormPanel.getViewMode());
    }

    private void addNew() {
        FormTester formTester = tester.newFormTester("form");
        tester.clickLink("form:crud:container:content:create");
        formTester.submit("crud:container:content:form:save");
    }

    @SInfoType(name = "SimpleCrudType", spackage = SimpleCrudPackage.class)
    public static class SimpleCrudType extends STypeComposite<SIComposite> {
        @Override
        protected void onLoadType(@Nonnull TypeBuilder tb) {

        }
    }

    @SInfoPackage(name = "foo.bar")
    public static class SimpleCrudPackage extends SPackage {
    }


}