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

package org.opensingular.form.exemplos.notificacaosimplificada.domain.corporativo.enums;

public enum TipoPessoaNS {


    JURIDICA("J", "Jurídica"),
    FISICA("F", "Física");

    public static final String CLASS_NAME = "org.opensingular.form.exemplos.notificacaosimplificada.domain.corporativo.enums.TipoPessoaNS";

    private String cod;
    private String descricao;

    TipoPessoaNS(String cod, String descricao) {
        this.cod = cod;
        this.descricao = descricao;
    }

    public static TipoPessoaNS valueOfEnum(String cod) {
        for (TipoPessoaNS tipo : TipoPessoaNS.values()) {
            if (cod.equals(tipo.getCod())) {
                return tipo;
            }
        }
        return null;
    }

    public String getCod() {
        return cod;
    }

    public String getDescricao() {
        return descricao;
    }
}
