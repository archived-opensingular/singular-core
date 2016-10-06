package org.opensingular.form.exemplos.notificacaosimplificada.domain.enums;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlEnumValue;

@XmlEnum
public enum TipoMascaramento {

    @XmlEnumValue("1")
    ABERTO('A', "Aberto"),

    @XmlEnumValue("2")
    CEGO('C', "Cego"),

    @XmlEnumValue("3")
    DUPLO_CEGO('D', "Duplo Cego"),

    @XmlEnumValue("3")
    TRIPLO_CEGO('T', "Triplo Cego");

    private Character codigo;
    private String    descricao;

    private TipoMascaramento(Character codigo, String descricao) {
        this.codigo = codigo;
        this.descricao = descricao;
    }

    public Character getCodigo() {
        return codigo;
    }

    public String getDescricao() {
        return descricao;
    }

    public static TipoMascaramento valueOf(Character codigo) {

        TipoMascaramento status[] = TipoMascaramento.values();

        for (TipoMascaramento st : status) {
            if (codigo != null && st.getCodigo().charValue() == codigo.charValue()) {
                return st;
            }
        }
        return null;
    }
}
