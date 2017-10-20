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

import static java.util.stream.Collectors.*;
import static org.opensingular.lib.wicket.util.util.Shortcuts.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.wicket.Component;
import org.apache.wicket.MarkupContainer;
import org.apache.wicket.feedback.FeedbackCollector;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.form.Form;
import org.junit.Assert;
import org.junit.Test;
import org.opensingular.lib.wicket.util.SingleFormDummyPage;
import org.opensingular.lib.wicket.util.WicketUtilTester;
import org.opensingular.lib.wicket.util.bootstrap.layout.TemplatePanel;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;

public class WicketUtilsTest {

    @Test
    public void test() {
        WicketUtilTester tester = WicketUtilTester.withDummyApplication();

        tester.startPage(new SingleFormDummyPage() {
            @Override
            protected Component newContentPanel(String contentId) {
                MarkupContainer content = new TemplatePanel(contentId, () -> ""
                    + "<div wicket:id='a'>"
                    + "  <div wicket:id='b'>"
                    + "    <div wicket:id='c'>"
                    + "    </div>"
                    + "  </div>"
                    + "</div>"
                    + "")
                        .add(new WebMarkupContainer("a")
                            .add(new WebMarkupContainer("b")
                                .add(new WebMarkupContainer("c"))));

                content.get("a").error("error msg");
                return content;
            }
        });

        SingleFormDummyPage page = (SingleFormDummyPage) tester.getLastRenderedPage();
        Component a = tester.childById("a").get();
        Component b = tester.childById("b").get();
        Component c = tester.childById("c").get();
        TemplatePanel content = (TemplatePanel) tester.childById("a").get().getParent();
        Form<?> form = (Form<?>) page.get(SingleFormDummyPage.FORM_ID);

        List<Component> list = new ArrayList<>();
        WicketUtils.appendListOfParents(list, tester.childById("c").get(), page);

        Assert.assertTrue(Lists.transform(list, it -> it.getId()).containsAll(Arrays.asList("a", "b")));

        Assert.assertTrue(a.hasErrorMessage());
        WicketUtils.clearMessagesForComponent(a);
        Assert.assertTrue(new FeedbackCollector(a).collect(it -> !it.isRendered()).isEmpty());

        Assert.assertTrue(
            WicketUtils.findChildren(page, WebMarkupContainer.class)
                .collect(toSet())
                .containsAll(Sets.newHashSet(a, b, c)));

        Assert.assertSame(content,
            WicketUtils.findClosestParent(c, TemplatePanel.class).get());

        Assert.assertEquals("a:b:c",
            WicketUtils.findContainerRelativePath(content, "c").get());

        Assert.assertEquals(content,
            WicketUtils.findFirstChild(page, TemplatePanel.class).get());

        Assert.assertEquals(c,
            WicketUtils.findFirstChild(page, WebMarkupContainer.class, it -> it.getId().equals("c")).get());

        Assert.assertEquals(
            SingleFormDummyPage.PARENT_PATH + "a:b:c",
            WicketUtils.findPageRelativePath(content, "c").get());

        Assert.assertEquals(
            Arrays.asList(b, a, content, form, page),
            WicketUtils.listParents(c));

        Assert.assertEquals(
            Arrays.asList(b, a, content, form),
            WicketUtils.listParents(c, form));

        Assert.assertEquals(
            new ArrayList<>(),
            Arrays.asList($m.ofValue(), new ArrayList<>()).stream()
                .filter(it -> !WicketUtils.nullOrEmpty(it))
                .collect(toCollection(ArrayList::new)));
    }

}
