package org.opensingular.singular.exemplos.notificacaosimplificada.domain.enums;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlEnumValue;

@XmlEnum
public enum TipoAmostra {

    @XmlEnumValue("F")
    FIXA('F', "Fixa"),

    @XmlEnumValue("S")
    SEQUENCIAL('S', "Sequencial");

    private Character codigo;
    private String    descricao;

    private TipoAmostra(Character codigo, String descricao) {
        this.codigo = codigo;
        this.descricao = descricao;
    }

    public Character getCodigo() {
        return codigo;
    }

    public String getDescricao() {
        return descricao;
    }

    public static TipoAmostra valueOfEnum(Character codigo) {

        TipoAmostra status[] = TipoAmostra.values();

        for (TipoAmostra st : status) {
            if (codigo != null && st.getCodigo().charValue() == codigo.charValue()) {
                return st;
            }
        }
        return null;
    }
}
