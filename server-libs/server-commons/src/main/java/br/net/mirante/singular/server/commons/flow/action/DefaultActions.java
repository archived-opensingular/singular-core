/*
 * Copyright (c) 2016, Mirante and/or its affiliates. All rights reserved.
 * Mirante PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package br.net.mirante.singular.server.commons.flow.action;

import static br.net.mirante.singular.server.commons.util.ServerActionConstants.*;

import br.net.mirante.singular.server.commons.service.dto.ItemAction;
import br.net.mirante.singular.server.commons.service.dto.ItemActionConfirmation;
import br.net.mirante.singular.server.commons.service.dto.ItemActionType;
import br.net.mirante.singular.util.wicket.resource.Icone;

public class DefaultActions {

    public static final ItemActionConfirmation CONFIRMATION_DELETE = new ItemActionConfirmation("Excluir o rascunho", "Confirma a exclusão?", "Cancelar", "Remover");

    public static final ItemAction EDIT = new ItemAction(ACTION_EDIT, "Alterar", Icone.PENCIL, ItemActionType.POPUP);
    public static final ItemAction DELETE = new ItemAction(ACTION_DELETE, "Excluir", Icone.MINUS, ItemActionType.ENDPOINT, CONFIRMATION_DELETE);
    public static final ItemAction VIEW = new ItemAction(ACTION_VIEW, "Visualizar", Icone.EYE, ItemActionType.POPUP);
    public static final ItemAction ANALYSE = new ItemAction(ACTION_ANALYSE, "Analisar", Icone.PENCIL, ItemActionType.POPUP);

}
