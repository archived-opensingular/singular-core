/*
 * Copyright (c) 2016, Mirante and/or its affiliates. All rights reserved.
 * Mirante PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
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
