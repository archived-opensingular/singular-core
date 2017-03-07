/*
 * Copyright (C) 2016 Singular Studios (a.k.a Atom Tecnologia) - www.opensingular.com
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
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

package org.opensingular.internal.form.wicket.util;

import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.util.tester.WicketTester;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.opensingular.form.SingularFormException;
import org.opensingular.form.wicket.helpers.SingularWicketTester;

import java.util.function.Supplier;

/**
 * Teste a classe de verificação automatica de serialização das páginas
 * @author Daniel Bordin on 11/02/2017.
 */
public class TestWicketSerializationDebugUtil {

    protected WicketTester tester;

    @Before
    public void setUp() {
        tester = new SingularWicketTester();
    }

    @Test
    public void testeWrongSerializationPage() {
        tester.startPage(WrongSerializationPage.class);
        tester.assertRenderedPage(WrongSerializationPage.class);
        Assert.assertTrue(WicketSerializationDebugUtil.getLastVerificationResult(tester.getApplication()).contains("result size="));
        Assert.assertFalse(WicketSerializationDebugUtil.getLastVerificationResult(tester.getApplication()).contains("EXCEPTION"));
        try {
            tester.clickLink("reload");
            Assert.fail("Era esperada uma exception de serialização");
        } catch (SingularFormException e) {
            Assert.assertTrue(e.getMessage().contains("Erro serializando"));
        }
        Assert.assertTrue(WicketSerializationDebugUtil.getLastVerificationResult(tester.getApplication()).contains("result size=EXCEPTION"));
    }

    public static class WrongSerializationPage extends WebPage {

        public Object o = 1;

        public WrongSerializationPage() {

            add(new Form("form").add(new Label("content", "ola")));
            add(new Link("reload") {
                @Override
                public void onClick() {
                    o = new Supplier<String>() {
                        @Override
                        public String get() {
                            return null;
                        }
                    };
                }
            });
        }
    }
}
