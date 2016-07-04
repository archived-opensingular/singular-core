/*
 * Copyright (c) 2016, Mirante and/or its affiliates. All rights reserved.
 * Mirante PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package br.net.mirante.singular.server.commons.flow.action;

import static br.net.mirante.singular.server.commons.util.ServerActionConstants.*;

import br.net.mirante.singular.server.commons.service.dto.ItemAction;
import br.net.mirante.singular.server.commons.service.dto.ItemActionType;
import br.net.mirante.singular.util.wicket.resource.Icone;

public class DefaultActions {

    public static final ItemAction ALTERAR = new ItemAction(ACAO_ALTERAR, "Alterar", Icone.PENCIL, ItemActionType.POPUP);
    public static final ItemAction EXCLUIR = new ItemAction(ACAO_EXCLUIR, "Excluir", Icone.MINUS, ItemActionType.ENDPOINT);
    public static final ItemAction VISUALIZAR = new ItemAction(ACAO_VISUALIZAR, "Visualizar", Icone.EYE, ItemActionType.POPUP);
    public static final ItemAction CUMPRIR_EXIGENCIA = new ItemAction(ACAO_EXIGENCIA, "Exigência", Icone.PENCIL, ItemActionType.POPUP);
}
