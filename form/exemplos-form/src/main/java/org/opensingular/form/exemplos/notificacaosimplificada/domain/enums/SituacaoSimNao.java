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

public enum SituacaoSimNao {

    SIM("Sim", "S"),
    NAO("Não", "N"),
    NAO_SE_APLICA("NÃO SE APLICA", "X");

    private String descricao;
    private String codigo;


    private SituacaoSimNao(String descricao, String codigo) {
        this.codigo = codigo;
        this.descricao = descricao;
    }


    public String getDescricao() {
        return descricao;
    }


    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }


    public String getCodigo() {
        return codigo;
    }


    public void setCodigo(String codigo) {
        this.codigo = codigo;
    }

    public static SituacaoSimNao valueOfEnum(String codigo) {
        SituacaoSimNao status[] = SituacaoSimNao.values();

        for (SituacaoSimNao st : status) {
            if (codigo != null && codigo.equals(st.getCodigo())) {
                return st;
            }
        }
        return null;
    }

    public static SituacaoSimNao fromBoolean(Boolean bool) {
        if (bool != null && bool.booleanValue()) {
            return SIM;
        } else {
            return NAO;
        }
    }

    public Boolean toBoolean() {
        if (this == SIM) {
            return Boolean.TRUE;
        } else {
            return Boolean.FALSE;
        }
    }

}
