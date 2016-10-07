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

public enum SituacaoNecessitaAnaliseComplementar {

    SIM('S', "Sim"),
    NAO('N', "Não");

    public static final String ENUM_CLASS_NAME = "org.opensingular.form.exemplos.notificacaosimplificada.domain.enums.SituacaoNecessitaAnaliseComplementar";

    private Character codigo;
    private String    descricao;

    private SituacaoNecessitaAnaliseComplementar(Character codigo,
                                                 String descricao) {
        this.codigo = codigo;
        this.descricao = descricao;
    }

    public Character getCodigo() {
        return codigo;
    }

    public String getDescricao() {
        return descricao;
    }

    public static SituacaoNecessitaAnaliseComplementar valueOfEnum(Character codigo) {

        SituacaoNecessitaAnaliseComplementar tipos[] = SituacaoNecessitaAnaliseComplementar.values();

        for (SituacaoNecessitaAnaliseComplementar tipo : tipos) {
            if (codigo != null && tipo.getCodigo().equals(codigo)) {
                return tipo;
            }
        }
        return null;
    }
}
