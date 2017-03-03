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
import org.apache.wicket.util.tester.FormTester;

import javax.servlet.ServletContext;

/**
 * Versão especializada do {@link SingularWicketTester} que automaticamente cria uma página Dummy para testar formuláro
 * de {@link org.opensingular.form.SType}.
 *
 * @author Daniel Bordin on 15/02/2017.
 */
public final class SingularDummyFormPageTester extends SingularWicketTester {

    private DummyPage page = new DummyPage();

    public SingularDummyFormPageTester() {}

    public SingularDummyFormPageTester(Class<? extends Page> homePage) {
        super(homePage);
    }

    public SingularDummyFormPageTester(WebApplication application) {
        super(application);
    }

    public SingularDummyFormPageTester(WebApplication application, String path) {
        super(application, path);
    }

    public SingularDummyFormPageTester(WebApplication application, ServletContext servletCtx) {
        super(application, servletCtx);
    }

    public SingularDummyFormPageTester(WebApplication application, boolean init) {
        super(application, init);
    }

    public SingularDummyFormPageTester(WebApplication application, ServletContext servletCtx, boolean init) {
        super(application, servletCtx, init);
    }

    /** Retorna a página padrão de test de componente ou Form. */
    public DummyPage getDummyPage() {
        return page;
    }

    /** Executa no wicket a página de teste padrão que contem o SType ou Form a ser testado. */
    public final SingularDummyFormPageTester startDummyPage() {
        if (page.getTypeBuilder() == null) {
            throw new RuntimeException("page.getTypeBuilder() está null (configuare para o teste)");
        }
        startPage(page);
        return this;
    }

    public FormTester newFormTester(){
        return newFormTester("form");
    }
}
