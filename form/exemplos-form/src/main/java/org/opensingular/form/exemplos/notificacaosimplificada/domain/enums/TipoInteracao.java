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

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlEnumValue;

@XmlEnum
public enum TipoInteracao {

    @XmlEnumValue("M")
    MEDICAMENTO('M', "Medicamento"),

    @XmlEnumValue("N")
    EXAME_NAO_LABORIAL('N', "Exame laboratorial"),

    @XmlEnumValue("D")
    DOENCA('D', "Doença"),

    @XmlEnumValue("A")
    ALIMENTO('A', "Alimento");

    private Character codigo;
    private String    descricao;

    private TipoInteracao(Character codigo, String descricao) {
        this.codigo = codigo;
        this.descricao = descricao;
    }

    public Character getCodigo() {
        return codigo;
    }

    public String getDescricao() {
        return descricao;
    }

    public static TipoInteracao valueOf(Character codigo) {

        TipoInteracao status[] = TipoInteracao.values();

        for (TipoInteracao st : status) {
            if (codigo != null && st.getCodigo().charValue() == codigo.charValue()) {
                return st;
            }
        }
        return null;
    }
}
