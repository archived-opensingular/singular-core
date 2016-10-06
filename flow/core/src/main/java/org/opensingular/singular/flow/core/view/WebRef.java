/*
 * Copyright (c) 2016, Mirante and/or its affiliates. All rights reserved.
 * Mirante PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package org.opensingular.singular.flow.core.view;

import java.io.Serializable;

public interface WebRef extends Serializable {

    public String getNome();

    public String getNomeCurto();

    public Lnk getPath();

    public String getPathIcone();

    public String getPathIconePequeno();

    public String getConfirmacao();

    public boolean isPossuiDireitoAcesso();

    public boolean isJs();

    public String getJs();

    public boolean isAbrirEmNovaJanela();

    /**
     * Informa se o link é válido para o conjunto de dados informado no momento da geração do link.
     */
    public boolean isSeAplicaAoContexto();

    public ModalViewDef getModalViewDef();

    public WebRef addParam(String nome, Object valor);

    public String gerarHtml(String urlApp);
}
