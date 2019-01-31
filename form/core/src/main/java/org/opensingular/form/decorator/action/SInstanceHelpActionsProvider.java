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

package org.opensingular.form.decorator.action;

import static org.apache.commons.lang3.StringUtils.*;

import java.util.Arrays;
import java.util.Collections;

import org.opensingular.form.SIList;
import org.opensingular.form.SInstance;
import org.opensingular.form.STypeComposite;

/**
 * Provider para a ação de exibição do Help do campo.
 */
public class SInstanceHelpActionsProvider implements ISInstanceActionsProvider {

    @Override
    public Iterable<SInstanceAction> getActions(ISInstanceActionCapable target, SInstance instance) {
        return doHelpAction(instance.asAtr().getHelp());
    }

    @Override
    public Iterable<SInstanceAction> getListFieldActions(ISInstanceActionCapable target, SIList<?> instance, String field) {
        if ((instance.getElementsType() instanceof STypeComposite<?>) && isNotBlank(field)) {
            STypeComposite<?> compositeType = (STypeComposite<?>) instance.getElementsType();
            return doHelpAction(compositeType.getField(field).asAtr().getHelp());
        }
        return doHelpAction(instance.getElementsType().asAtr().getHelp());
    }

    private Iterable<SInstanceAction> doHelpAction(final String helpText) {
        return (isBlank(helpText))
            ? Collections.emptyList()
            : Arrays.asList(new SInstanceAction(SInstanceAction.ActionType.NORMAL)
                .setIcon(SIcon.resolve("question"))
                .setText("Ajuda")
                .setPosition(Integer.MIN_VALUE)
                .setPreview(new SInstanceAction.Preview()
                    .setMessage(helpText)
                    .setFormat("HTML")));
    }
}
