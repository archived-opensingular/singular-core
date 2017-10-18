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

package org.opensingular.lib.wicket.util.util;

import static org.opensingular.lib.wicket.util.util.Shortcuts.*;

import org.apache.wicket.Component;
import org.apache.wicket.MetaDataKey;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.junit.Assert;
import org.junit.Test;
import org.opensingular.lib.wicket.util.SingleFormDummyPage;
import org.opensingular.lib.wicket.util.WicketUtilTester;
import org.opensingular.lib.wicket.util.bootstrap.layout.TemplatePanel;

public class IBehaviorsMixinTest {

    private static final MetaDataKey<Boolean> METADATA_KEY = new MetaDataKey<Boolean>() {};

    @Test
    public void test() {
        WicketUtilTester tester = WicketUtilTester.withDummyApplication();

        tester.startPage(new SingleFormDummyPage() {
            @Override
            protected Component newContentPanel(String contentId) {
                Component alwaysInvisible = new WebMarkupContainer("alwaysInvisible").setVisible(false);
                return new TemplatePanel(contentId, () -> ""
                    + "<div wicket:id='attr'></div>"
                    + "<div wicket:id='clazz'></div>"
                    + "<div wicket:id='enabled'></div>"
                    + "<div wicket:id='disabled'></div>"
                    + "<div wicket:id='visible'></div>"
                    + "<div wicket:id='invisible'></div>"
                    + "<div wicket:id='clickAlert'></div>"
                    + "<div wicket:id='onComponentTag'></div>"
                    + "<div wicket:id='onConfigure'></div>"
                    + "<div wicket:id='bodyOnly' class='bodyOnly_ignored'><span class='bodyOnly_rendered'></span></div>"
                    + "<div wicket:id='visibleIf_model'></div>"
                    + "<div wicket:id='visibleIf_supplier'></div>"
                    + "<div wicket:id='visibleIfAlso'></div>"
                    + "<div wicket:id='alwaysInvisible'></div>"
                    + "<div wicket:id='visibleIfModelObject'></div>"
                    + "")
                        .add(new WebMarkupContainer("attr")
                            .add($b.attr("attr1", "attr1Value"))
                            .add($b.attrAppender("attr1", "attr1Extra", " "))
                            .add($b.attr("attr2", "attr2Value", $m.ofValue(false)))
                            .add($b.attrAppender("attr2", "attr2Extra", " ", $m.ofValue(false)))
                            .add($b.attr("attr3", "attr3Value", $m.ofValue(true)))
                            .add($b.attrAppender("attr3", "attr3Extra", " ", $m.ofValue(true)))
                            .add($b.attr("attr4", "attr4Value attr4Extra attr4Exact"))
                            .add($b.attrRemover("attr4", "Value", true))
                            .add($b.attrRemover("attr4", "Extra", false))
                            .add($b.attrRemover("attr4", "attr4Exact", true)))

                        .add(new WebMarkupContainer("clazz")
                            .add($b.classAppender("clazz1"))
                            .add($b.classAppender("clazz2", $m.ofValue(true)))
                            .add($b.classAppender("clazz3", $m.ofValue(false))))

                        .add(new WebMarkupContainer("enabled")
                            .add($b.enabledIf($m.ofValue(true))))
                        .add(new WebMarkupContainer("disabled")
                            .add($b.enabledIf($m.ofValue(false))))

                        .add(new WebMarkupContainer("visible")
                            .add($b.notVisibleIf(() -> false)))
                        .add(new WebMarkupContainer("invisible")
                            .add($b.notVisibleIf(() -> true)))

                        .add(new WebMarkupContainer("clickAlert")
                            .add($b.on("click", c -> "alert(1234)")))

                        .add(new WebMarkupContainer("onComponentTag")
                            .add($b.onComponentTag((c, t) -> t.put("onComponentTagAttr", "onComponentTagValue"))))

                        .add(new WebMarkupContainer("onConfigure")
                            .add($b.onConfigure(c -> c.setMetaData(METADATA_KEY, true))))

                        .add(new WebMarkupContainer("bodyOnly")
                            .add($b.renderBodyOnly($m.ofValue(true))))

                        .add(new WebMarkupContainer("visibleIf_model")
                            .add($b.visibleIf($m.ofValue(false))))
                        .add(new WebMarkupContainer("visibleIf_supplier")
                            .add($b.visibleIf(() -> false)))
                        .add(new WebMarkupContainer("visibleIfAlso")
                            .add($b.visibleIfAlso(alwaysInvisible)))
                        .add(alwaysInvisible)
                        .add(new WebMarkupContainer("visibleIfModelObject", $m.ofValue("abc"))
                            .add($b.visibleIfModelObject((String it) -> it.contains("x"))))

                ;
            }
        });

        tester.assertContains("attr1=\"attr1Value attr1Extra\"");
        tester.assertContainsNot("attr2=\"");
        tester.assertContains("attr3=\"attr3Value attr3Extra\"");
        tester.assertContains("attr4=\"attr4Value attr4 \"");

        tester.assertContains("class=\"clazz1 clazz2\"");

        Assert.assertTrue(tester.childById("enabled").get().isEnabled());
        Assert.assertFalse(tester.childById("disabled").get().isEnabled());

        Assert.assertTrue(tester.childById("visible").get().isVisible());
        Assert.assertFalse(tester.childById("invisible").get().isVisible());

        tester.assertContains("alert\\(1234\\)");

        tester.assertContains("onComponentTagAttr=\"onComponentTagValue\"");

        Assert.assertTrue(tester.childById("onConfigure").get().getMetaData(METADATA_KEY));

        tester.assertContainsNot("class='bodyOnly_ignored'");
        tester.assertContains("class='bodyOnly_rendered'");
        
        Assert.assertFalse(tester.childById("visibleIf_model").get().isVisible());
        Assert.assertFalse(tester.childById("visibleIf_supplier").get().isVisible());
        Assert.assertFalse(tester.childById("visibleIfAlso").get().isVisible());
        Assert.assertFalse(tester.childById("visibleIfModelObject").get().isVisible());
    }

}
