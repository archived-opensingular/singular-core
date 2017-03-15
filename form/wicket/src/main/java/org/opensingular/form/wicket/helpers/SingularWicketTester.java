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

import org.apache.wicket.Page;
import org.apache.wicket.protocol.http.WebApplication;
import org.apache.wicket.util.tester.WicketTester;
import org.opensingular.internal.form.wicket.util.WicketSerializationDebugUtil;

import javax.servlet.ServletContext;
import java.nio.charset.StandardCharsets;

/**
 * Um WicketTester com configuração básicas do Singular para facilitar a criação de testes.
 *
 * @author Daniel Bordin on 12/02/2017.
 */
public class SingularWicketTester extends WicketTester {

    public SingularWicketTester() {
        setup();
    }

    @Deprecated
    public SingularWicketTester(boolean turnOnSerializationCheck) {
        setup(turnOnSerializationCheck);
    }

    public SingularWicketTester(Class<? extends Page> homePage) {
        super(homePage);
        setup();
    }

    public SingularWicketTester(WebApplication application) {
        super(application);
        setup();
    }

    @Deprecated
    public SingularWicketTester(boolean turnOnSerializationCheck, WebApplication application) {
        super(application);
        setup(turnOnSerializationCheck);
    }

    public SingularWicketTester(WebApplication application, String path) {
        super(application, path);
        setup();
    }

    public SingularWicketTester(WebApplication application, ServletContext servletCtx) {
        super(application, servletCtx);
        setup();
    }

    public SingularWicketTester(WebApplication application, boolean init) {
        super(application, init);
        setup();
    }

    public SingularWicketTester(WebApplication application, ServletContext servletCtx, boolean init) {
        super(application, servletCtx, init);
        setup();
    }

    private void setup() {
        setup(true);
    }

    private void setup(boolean turnOnSerializationCheck) {
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
            throw new RuntimeException("deve ser antes chamado o método SingularDummyFormPageTester.startDummyPage()");
        }
    }
}
