/*
 * Copyright (C) 2016 Singular Studios (a.k.a Atom Tecnologia) - www.opensingular.com
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.opensingular.server.commons.flow.action;

import org.opensingular.server.commons.flow.rest.ActionDefinition;
import org.opensingular.server.commons.flow.rest.DefaultServerREST;
import org.opensingular.server.commons.service.dto.ItemAction;
import org.opensingular.server.commons.service.dto.ItemActionConfirmation;
import org.opensingular.server.commons.service.dto.ItemActionType;
import org.opensingular.lib.wicket.util.resource.Icone;

public class DefaultActions {

    public static final ActionDefinition ACTION_EDIT     = new ActionDefinition("alterar");
    public static final ActionDefinition ACTION_DELETE   = new ActionDefinition("excluir");
    public static final ActionDefinition ACTION_VIEW     = new ActionDefinition("visualizar");
    public static final ActionDefinition ACTION_ANALYSE  = new ActionDefinition("analisar");
    public static final ActionDefinition ACTION_ASSIGN   = new ActionDefinition("atribuir");
    public static final ActionDefinition ACTION_RELOCATE = new ActionDefinition("realocar");

    public static final ItemActionConfirmation CONFIRMATION_DELETE   = new ItemActionConfirmation("Excluir o rascunho", "Confirma a exclusão?", "Cancelar", "Remover", null);
    public static final ItemActionConfirmation CONFIRMATION_RELOCATE = new ItemActionConfirmation("Realocar", "Escolha:", "Cancelar", "Realocar", DefaultServerREST.USERS);

    public static final ItemAction EDIT     = new ItemAction(ACTION_EDIT.getName(), "Alterar", Icone.PENCIL, ItemActionType.POPUP);
    public static final ItemAction DELETE   = new ItemAction(ACTION_DELETE.getName(), "Excluir", Icone.MINUS, ItemActionType.ENDPOINT, CONFIRMATION_DELETE);
    public static final ItemAction VIEW     = new ItemAction(ACTION_VIEW.getName(), "Visualizar", Icone.EYE, ItemActionType.POPUP);
    public static final ItemAction ANALYSE  = new ItemAction(ACTION_ANALYSE.getName(), "Analisar", Icone.PENCIL, ItemActionType.POPUP);
    public static final ItemAction ASSIGN   = new ItemAction(ACTION_ASSIGN.getName(), "Atribuir", Icone.ARROW_DOWN, ItemActionType.ENDPOINT);
    public static final ItemAction RELOCATE = new ItemAction(ACTION_RELOCATE.getName(), "Realocar", Icone.SHARE_SQUARE, ItemActionType.ENDPOINT, CONFIRMATION_RELOCATE);

}
