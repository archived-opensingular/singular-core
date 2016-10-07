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

package org.opensingular.flow.core.view;

import java.io.Serializable;

public interface WebRef extends Serializable {

    public String getNome();

    public String getNomeCurto();

    public Lnk getPath();

    public String getPathIcone();

    public String getPathIconePequeno();

    public String getConfirmacao();

    public boolean isPossuiDireitoAcesso();

    public boolean isJs();

    public String getJs();

    public boolean isAbrirEmNovaJanela();

    /**
     * Informa se o link é válido para o conjunto de dados informado no momento da geração do link.
     */
    public boolean isSeAplicaAoContexto();

    public ModalViewDef getModalViewDef();

    public WebRef addParam(String nome, Object valor);

    public String gerarHtml(String urlApp);
}
