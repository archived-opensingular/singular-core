/*
 * Copyright (C) 2016 Singular Studios (a.k.a Atom Tecnologia) - www.opensingular.com
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
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

package org.opensingular.server.commons.flow;

import org.opensingular.flow.core.view.Lnk;
import org.opensingular.flow.core.view.ModalViewDef;
import org.opensingular.flow.core.view.WebRef;
import org.apache.commons.lang3.NotImplementedException;
import org.apache.wicket.markup.html.WebPage;

public class SingularWebRef implements WebRef {

    private Class<? extends WebPage> page;

    public SingularWebRef(Class<? extends WebPage> page) {
        this.page = page;
    }

    public Class<? extends WebPage> getPageClass() {
        return page;
    }

    @Deprecated
    @Override
    public String getNome() {
        throw new NotImplementedException("Método não implementado, não é necessário para o singular.");
    }

    @Deprecated
    @Override
    public String getNomeCurto() {
        throw new NotImplementedException("Método não implementado, não é necessário para o singular.");
    }

    @Deprecated
    @Override
    public Lnk getPath() {
        throw new NotImplementedException("Método não implementado, não é necessário para o singular.");
    }

    @Deprecated
    @Override
    public String getPathIcone() {
        throw new NotImplementedException("Método não implementado, não é necessário para o singular.");
    }

    @Deprecated
    @Override
    public String getPathIconePequeno() {
        throw new NotImplementedException("Método não implementado, não é necessário para o singular.");
    }

    @Deprecated
    @Override
    public String getConfirmacao() {
        throw new NotImplementedException("Método não implementado, não é necessário para o singular.");
    }

    @Deprecated
    @Override
    public boolean isPossuiDireitoAcesso() {
        throw new NotImplementedException("Método não implementado, não é necessário para o singular.");
    }

    @Deprecated
    @Override
    public boolean isJs() {
        throw new NotImplementedException("Método não implementado, não é necessário para o singular.");
    }

    @Deprecated
    @Override
    public String getJs() {
        throw new NotImplementedException("Método não implementado, não é necessário para o singular.");
    }

    @Deprecated
    @Override
    public boolean isAbrirEmNovaJanela() {
        throw new NotImplementedException("Método não implementado, não é necessário para o singular.");
    }

    @Deprecated
    @Override
    public boolean isSeAplicaAoContexto() {
        throw new NotImplementedException("Método não implementado, não é necessário para o singular.");
    }

    @Deprecated
    @Override
    public ModalViewDef getModalViewDef() {
        throw new NotImplementedException("Método não implementado, não é necessário para o singular.");
    }

    @Deprecated
    @Override
    public WebRef addParam(String nome, Object valor) {
        throw new NotImplementedException("Método não implementado, não é necessário para o singular.");
    }

    @Deprecated
    @Override
    public String gerarHtml(String urlApp) {
        throw new NotImplementedException("Método não implementado, não é necessário para o singular.");
    }
}
