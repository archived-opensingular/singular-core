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

package org.opensingular.form.exemplos.notificacaosimplificada.domain.enums;

public enum TipoAcao {

    INCLUIR('I', "Incluir", "Incluído"),
    ALTERAR('A', "Alterar", "Alterado"),
    EXCLUIR('E', "Excluir", "Excluído");

    public static final String ENUM_CLASS_NAME = "org.opensingular.form.exemplos.notificacaosimplificada.domain.enums.TipoAcao";

    private Character codigo;
    private String    descricao;
    private String    descricaoGerundio;

    private TipoAcao(Character codigo, String descricao, String descricaoGerundio) {
        this.codigo = codigo;
        this.descricao = descricao;
        this.descricaoGerundio = descricaoGerundio;
    }

    public Character getCodigo() {
        return codigo;
    }

    public String getDescricao() {
        return descricao;
    }

    public String getDescricaoGerundio() {
        return descricaoGerundio;
    }

    public static TipoAcao valueOfEnum(Character codigo) {

        TipoAcao tipos[] = TipoAcao.values();

        for (TipoAcao tipo : tipos) {
            if (codigo != null && tipo.getCodigo().equals(codigo)) {
                return tipo;
            }
        }
        return null;
    }

}
