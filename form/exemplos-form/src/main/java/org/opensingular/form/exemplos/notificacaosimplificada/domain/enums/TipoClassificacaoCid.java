package org.opensingular.form.exemplos.notificacaosimplificada.domain.enums;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlEnumValue;
import javax.xml.bind.annotation.XmlType;

@XmlEnum
@XmlType(namespace = "http://www.anvisa.gov.br/reg-med/schema/domains")
public enum TipoClassificacaoCid {

    @XmlEnumValue("E")
    ETIOLOGIA('E', "Etiologia"),

    @XmlEnumValue("M")
    MANIFESTACAO('M', "Manifestação");

    public static final String ENUM_CLASS_NAME = "org.opensingular.singular.exemplos.notificacaosimplificada.domain.enums.TipoClassificacaoCid";

    private Character codigo;
    private String    descricao;

    private TipoClassificacaoCid(Character codigo, String descricao) {
        this.codigo = codigo;
        this.descricao = descricao;
    }

    public Character getCodigo() {
        return codigo;
    }

    public String getDescricao() {
        return descricao;
    }

    public static TipoClassificacaoCid valueOfEnum(Character codigo) {

        TipoClassificacaoCid status[] = TipoClassificacaoCid.values();

        for (TipoClassificacaoCid st : status) {
            if (codigo != null && st.getCodigo().charValue() == codigo.charValue()) {
                return st;
            }
        }
        return null;
    }
}
