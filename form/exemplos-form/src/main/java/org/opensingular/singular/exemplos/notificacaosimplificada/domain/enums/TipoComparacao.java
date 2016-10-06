package org.opensingular.singular.exemplos.notificacaosimplificada.domain.enums;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlEnumValue;

@XmlEnum
public enum TipoComparacao {

    @XmlEnumValue("N")
    EQUIVALENCIA('N', "Equivalencia"),

    @XmlEnumValue("D")
    SUPERIORIDADE('D', "Superioridade"),

    @XmlEnumValue("A")
    NAO_INFERIORIDADE('A', "Nao Inferioridade");

    private Character codigo;
    private String    descricao;

    private TipoComparacao(Character codigo, String descricao) {
        this.codigo = codigo;
        this.descricao = descricao;
    }

    public Character getCodigo() {
        return codigo;
    }

    public String getDescricao() {
        return descricao;
    }

    public static TipoComparacao valueOf(Character codigo) {

        TipoComparacao status[] = TipoComparacao.values();

        for (TipoComparacao st : status) {
            if (codigo != null && st.getCodigo().charValue() == codigo.charValue()) {
                return st;
            }
        }
        return null;
    }
}
