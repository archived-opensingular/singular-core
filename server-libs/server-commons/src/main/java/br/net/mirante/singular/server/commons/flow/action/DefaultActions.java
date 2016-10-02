/*
 * Copyright (c) 2016, Mirante and/or its affiliates. All rights reserved.
 * Mirante PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package br.net.mirante.singular.server.commons.flow.action;

import br.net.mirante.singular.server.commons.flow.rest.ActionDefinition;
import br.net.mirante.singular.server.commons.service.dto.ItemAction;
import br.net.mirante.singular.server.commons.service.dto.ItemActionConfirmation;
import br.net.mirante.singular.server.commons.service.dto.ItemActionType;
import br.net.mirante.singular.util.wicket.resource.Icone;

public class DefaultActions {


    public static final ActionDefinition ACTION_RELOCATE = new ActionDefinition("atribuir");
    public static final ActionDefinition ACTION_RELOCATE_TO_OTHER = new ActionDefinition("redistribuir");
    public static final ActionDefinition ACTION_EDIT     = new ActionDefinition("alterar");
    public static final ActionDefinition ACTION_DELETE   = new ActionDefinition("excluir");
    public static final ActionDefinition ACTION_VIEW     = new ActionDefinition("visualizar");
    public static final ActionDefinition ACTION_ANALYSE  = new ActionDefinition("analisar");

    public static final ItemActionConfirmation CONFIRMATION_DELETE   = new ItemActionConfirmation("Excluir o rascunho", "Confirma a exclusão?", "Cancelar", "Remover");
    public static final ItemActionConfirmation CONFIRMATION_RELOCATE = new ItemActionConfirmation("Redistribuir", "Escolha:", "Cancelar", "Atribuir");

    public static final ItemAction EDIT    = new ItemAction(ACTION_EDIT.getName(), "Alterar", Icone.PENCIL, ItemActionType.POPUP);
    public static final ItemAction DELETE  = new ItemAction(ACTION_DELETE.getName(), "Excluir", Icone.MINUS, ItemActionType.ENDPOINT, CONFIRMATION_DELETE);
    public static final ItemAction VIEW    = new ItemAction(ACTION_VIEW.getName(), "Visualizar", Icone.EYE, ItemActionType.POPUP);
    public static final ItemAction ANALYSE = new ItemAction(ACTION_ANALYSE.getName(), "Analisar", Icone.PENCIL, ItemActionType.POPUP);
    public static final ItemAction RELOCATE_TO_OTHER    = new ItemAction(ACTION_RELOCATE_TO_OTHER.getName(), "Redistribuir", Icone.PENCIL, ItemActionType.ENDPOINT, CONFIRMATION_RELOCATE);

}
