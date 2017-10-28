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

package org.opensingular.form.wicket.helpers;

import org.apache.commons.io.IOUtils;
import org.apache.wicket.Page;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.protocol.http.WebApplication;
import org.apache.wicket.util.tester.WicketTester;
import org.opensingular.form.SType;
import org.opensingular.form.helpers.AssertionsSInstance;
import org.opensingular.internal.lib.commons.test.SingularTestUtil;
import org.opensingular.internal.lib.wicket.test.WicketSerializationDebugUtil;

import javax.servlet.ServletContext;
import java.io.StringReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

/**
 * Um WicketTester com configuração básicas do Singular para facilitar a criação de testes.
 *
 * @author Daniel Bordin on 12/02/2017.
 */
public class SingularWicketTester extends WicketTester {

    public SingularWicketTester() {
        setUp();
    }

    @Deprecated
    public SingularWicketTester(boolean turnOnSerializationCheck) {
        setUp(turnOnSerializationCheck);
    }

    public SingularWicketTester(Class<? extends Page> homePage) {
        super(homePage);
        setUp();
    }

    public SingularWicketTester(WebApplication application) {
        super(application);
        setUp();
    }

    @Deprecated
    public SingularWicketTester(boolean turnOnSerializationCheck, WebApplication application) {
        super(application);
        setUp(turnOnSerializationCheck);
    }

    public SingularWicketTester(WebApplication application, String path) {
        super(application, path);
        setUp();
    }

    public SingularWicketTester(WebApplication application, ServletContext servletCtx) {
        super(application, servletCtx);
        setUp();
    }

    public SingularWicketTester(WebApplication application, boolean init) {
        super(application, init);
        setUp();
    }

    public SingularWicketTester(WebApplication application, ServletContext servletCtx, boolean init) {
        super(application, servletCtx, init);
        setUp();
    }

    private void setUp() {
        setUp(true);
    }

    private void setUp(boolean turnOnSerializationCheck) {
        getApplication().getMarkupSettings().setDefaultMarkupEncoding(StandardCharsets.UTF_8.name());
        if (turnOnSerializationCheck) {
            WicketSerializationDebugUtil.configurePageSerializationDebug(getApplication(), getClass());
        }
    }

    /** Criar um objeto de assertivas para a última página executada. */
    public final AssertionsWComponent getAssertionsPage() {
        checkIfStartPageCalled();
        return new AssertionsWComponent(getLastRenderedPage());
    }

    public final AssertionsSInstance getAssertionsInstance() {
        checkIfStartPageCalled();
        return getAssertionsPage().getSubComponentWithSInstance().assertSInstance();
    }

    /** Criar um objeto de assertivas para o form da última página executada (assume que o ID é 'form'). */
    public final AssertionsWComponent getAssertionsForm() {
        return getAssertionsForPath("form");
    }

    /** Criar um objeto de assertivas para o componente indicado no path da última página executada. */
    public final AssertionsWComponent getAssertionsForPath(String path) {
        checkIfStartPageCalled();
        return new AssertionsWComponent(getComponentFromLastRenderedPage(path));
    }

    /**
     * Cria um objeto de assertivas para o sub componente na hierarquia que for encontrado com o id informado ou dispara
     * exception senão encontrar o componente.
     */
    public final AssertionsWComponent getAssertionsForSubComp(String id) {
        checkIfStartPageCalled();
        return AssertionsWComponentBase.createAssertionForSubComponent(getLastRenderedPage(),
                c -> c.getId().equals(id));
    }

    private void checkIfStartPageCalled() {
        if (getLastRenderedPage() == null) {
            throw new IllegalStateException("deve ser antes chamado o método SingularDummyFormPageTester.startDummyPage()");
        }
    }

    public SingularFormTester newSingularFormTester(String path) {
        return newSingularFormTester(path, true);
    }

    public SingularFormTester newSingularFormTester(String path, boolean fillBlankString) {
        return new SingularFormTester(path, (Form<?>)getComponentFromLastRenderedPage(path), this, fillBlankString);
    }

    public <T extends SType<?>> STypeTester<T> newSingularSTypeTester(Class<? extends T> sypeClass){
        return new STypeTester<>(this, sypeClass);
    }

    public void checkToastrSuccessMessage(String expectedMessage) {

        List<String> foundMessages = new ArrayList<>();

        IOUtils.lineIterator(new StringReader(getLastResponse().getDocument()))
            .forEachRemaining(
                line -> {
                    if (line.startsWith("toastr.success")) {
                        String message = extractMessage(line);
                        foundMessages.add(message);
                    }
                }
        );

        if (!foundMessages.contains(expectedMessage)) {
            throw new AssertionError(String.format("Não foi possível encontrar a mensagem '%s', mensagens encontradas: '%s'", expectedMessage, foundMessages));
        }

    }

    private String extractMessage(String line) {
        return line.replace("toastr.success('', '", "")
                .replace("');", "").trim();
    }

    /**
     * Opens the html content on the programmer desktop with the associated application of the operating system
     * (default browser) e waits 5 seconds until resume the flow of execution. Useful to visually inspect the content
     * just created by a test.
     */
    public void showLastResponseOnDesktopForUserAndWaitOpening() {
        SingularTestUtil.showHtmlContentOnDesktopForUserAndWaitOpening(getLastResponseAsString());
    }
}
