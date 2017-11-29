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

package org.opensingular.internal.lib.wicket.test;

import org.apache.commons.io.IOUtils;
import org.apache.wicket.Component;
import org.apache.wicket.Page;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.protocol.http.WebApplication;
import org.apache.wicket.util.tester.WicketTester;
import org.opensingular.internal.lib.commons.test.SingularTestUtil;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.servlet.ServletContext;
import java.io.StringReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

/**
 * Um WicketTester com configuração básicas facilitar a criação de testes com Wicket.
 *
 * @author Daniel C. Bordin on 23/07/2017.
 */
public abstract class AbstractWicketTester<COMPONENT_ASSERTIONS extends AbstractAssertionsForWicket> extends WicketTester {

    public AbstractWicketTester() {
        setUp();
    }

    @Deprecated
    protected AbstractWicketTester(boolean turnOnSerializationCheck) {
        setUp(turnOnSerializationCheck);
    }

    public AbstractWicketTester(Class<? extends Page> homePage) {
        super(homePage);
        setUp();
    }

    public AbstractWicketTester(WebApplication application) {
        super(application);
        setUp();
    }

    @Deprecated
    protected AbstractWicketTester(boolean turnOnSerializationCheck, WebApplication application) {
        super(application);
        setUp(turnOnSerializationCheck);
    }

    public AbstractWicketTester(WebApplication application, String path) {
        super(application, path);
        setUp();
    }

    public AbstractWicketTester(WebApplication application, ServletContext servletCtx) {
        super(application, servletCtx);
        setUp();
    }

    public AbstractWicketTester(WebApplication application, boolean init) {
        super(application, init);
        setUp();
    }

    public AbstractWicketTester(WebApplication application, ServletContext servletCtx, boolean init) {
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

    @Nonnull
    public abstract COMPONENT_ASSERTIONS toComponentAssertions(@Nullable Component component);

    /** Criar um objeto de assertivas para a última página executada. */
    public final COMPONENT_ASSERTIONS getAssertionsPage() {
        checkIfStartPageCalled();
        return toComponentAssertions(getLastRenderedPage());
    }

    /** Criar um objeto de assertivas para o form da última página executada (assume que o ID é 'form'). */
    public final COMPONENT_ASSERTIONS getAssertionsForm() {
        return getAssertionsForPath("form");
    }

    /** Criar um objeto de assertivas para o componente indicado no path da última página executada. */
    public final COMPONENT_ASSERTIONS getAssertionsForPath(String path) {
        checkIfStartPageCalled();
        return toComponentAssertions(getComponentFromLastRenderedPage(path));
    }

    /**
     * Cria um objeto de assertivas para o sub componente na hierarquia que for encontrado com o id informado ou dispara
     * exception senão encontrar o componente.
     */
    public final COMPONENT_ASSERTIONS getAssertionsForSubComp(String id) {
        checkIfStartPageCalled();
        return (COMPONENT_ASSERTIONS) toComponentAssertions(getLastRenderedPage()).getSubComponentWithId(id);
    }

    protected final void checkIfStartPageCalled() {
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

    /**
     * Opens the html content on the programmer desktop with the associated application of the operating system
     * (default browser) e waits 5 seconds until resume the flow of execution. Useful to visually inspect the content
     * just created by a test.
     */
    public void showLastResponseOnDesktopForUserAndWaitOpening() {
        SingularTestUtil.showHtmlContentOnDesktopForUserAndWaitOpening(getLastResponseAsString());
    }

    public final void checkToastrSuccessMessage(String expectedMessage) {

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

}
