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
import org.apache.wicket.Page;
import org.apache.wicket.protocol.http.WebApplication;
import org.apache.wicket.util.tester.WicketTester;
import org.opensingular.internal.lib.commons.test.SingularTestUtil;

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
public class SingularSimpleWicketTester extends WicketTester {

    public SingularSimpleWicketTester() {
        setUp();
    }



    @Deprecated
    public SingularSimpleWicketTester(boolean turnOnSerializationCheck) {
        setUp(turnOnSerializationCheck);
    }

    public SingularSimpleWicketTester(Class<? extends Page> homePage) {
        super(homePage);
        setUp();
    }

    public SingularSimpleWicketTester(WebApplication application) {
        super(application);
        setUp();
    }

    @Deprecated
    public SingularSimpleWicketTester(boolean turnOnSerializationCheck, WebApplication application) {
        super(application);
        setUp(turnOnSerializationCheck);
    }

    public SingularSimpleWicketTester(WebApplication application, String path) {
        super(application, path);
        setUp();
    }

    public SingularSimpleWicketTester(WebApplication application, ServletContext servletCtx) {
        super(application, servletCtx);
        setUp();
    }

    public SingularSimpleWicketTester(WebApplication application, boolean init) {
        super(application, init);
        setUp();
    }

    public SingularSimpleWicketTester(WebApplication application, ServletContext servletCtx, boolean init) {
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
    public final AssertionsSimpleWComponent getAssertionsPage() {
        checkIfStartPageCalled();
        return new AssertionsSimpleWComponent(getLastRenderedPage());
    }

    /** Criar um objeto de assertivas para o form da última página executada (assume que o ID é 'form'). */
    public final AssertionsSimpleWComponent getAssertionsForm() {
        return getAssertionsForPath("form");
    }

    /** Criar um objeto de assertivas para o componente indicado no path da última página executada. */
    public final AssertionsSimpleWComponent getAssertionsForPath(String path) {
        checkIfStartPageCalled();
        return new AssertionsSimpleWComponent(getComponentFromLastRenderedPage(path));
    }

    /**
     * Cria um objeto de assertivas para o sub componente na hierarquia que for encontrado com o id informado ou dispara
     * exception senão encontrar o componente.
     */
    public final AssertionsSimpleWComponent getAssertionsForSubComp(String id) {
        checkIfStartPageCalled();
        return new AssertionsSimpleWComponent(getLastRenderedPage()).getSubComponentWithId(id);
    }

    private void checkIfStartPageCalled() {
        if (getLastRenderedPage() == null) {
            throw new IllegalStateException("deve ser antes chamado o método SingularDummyFormPageTester.startDummyPage()");
        }
    }

    /** Exibe a página gerada no browser do computador atual para verificação peso desenvolvedor. */
    public void showHtmlContentOnDesktopForUserAndWaitOpening() {
        SingularTestUtil.showHtmlContentOnDesktopForUserAndWaitOpening(getLastResponseAsString());
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
}
