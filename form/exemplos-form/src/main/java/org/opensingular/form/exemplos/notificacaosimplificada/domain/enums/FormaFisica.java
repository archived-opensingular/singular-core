package org.opensingular.form.exemplos.notificacaosimplificada.domain.enums;

import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;


@XmlRootElement(name = "forma-fisica", namespace = "http://www.anvisa.gov.br/reg-med/schema/domains")
@XmlType(name = "forma-fisica", namespace = "http://www.anvisa.gov.br/reg-med/schema/domains")
public enum FormaFisica {
    SOLIDO('S', "Sólido"),
    LIQUIDO('L', "Liquido"),
    GASOSO('G', "Gasoso");

    public static final String ENUM_CLASS_NAME = "org.opensingular.singular.exemplos.notificacaosimplificada.domain.enums.FormaFisica";

    private Character codigo;
    private String    descricao;


    private FormaFisica(Character codigo, String descricao) {
        this.codigo = codigo;
        this.descricao = descricao;
    }


    public Character getCodigo() {
        return codigo;
    }


    public String getDescricao() {
        return descricao;
    }

    public static FormaFisica valueOfEnum(Character codigo) {

        FormaFisica tipos[] = FormaFisica.values();

        for (FormaFisica tipo : tipos) {
            if (codigo != null && tipo.getCodigo().equals(codigo)) {
                return tipo;
            }
        }
        return null;
    }
}
