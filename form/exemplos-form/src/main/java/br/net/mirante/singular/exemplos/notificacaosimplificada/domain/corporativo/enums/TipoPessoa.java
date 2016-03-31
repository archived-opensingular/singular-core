/*
 * Copyright (c) 2016, Mirante and/or its affiliates. All rights reserved.
 * Mirante PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package br.net.mirante.singular.exemplos.notificacaosimplificada.domain.corporativo.enums;

public enum TipoPessoa {


    JURIDICA("J", "Jurídica"),
    FISICA("F", "Física");

    public static final String CLASS_NAME = "br.net.mirante.singular.exemplos.notificacaosimplificada.domain.corporativo.enums.TipoPessoa";

    private String cod;
    private String descricao;

    TipoPessoa(String cod, String descricao) {
        this.cod = cod;
        this.descricao = descricao;
    }

    public static TipoPessoa valueOfEnum(String cod) {
        for (TipoPessoa tipo : TipoPessoa.values()) {
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
