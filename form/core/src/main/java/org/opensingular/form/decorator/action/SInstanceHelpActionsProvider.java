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

import org.opensingular.form.SIComposite;
import org.opensingular.form.SIList;
import org.opensingular.form.SInstance;
import org.opensingular.form.SType;
import org.opensingular.form.STypeComposite;
import org.opensingular.form.type.basic.AtrBasic;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.apache.commons.lang3.StringUtils.isBlank;
import static org.apache.commons.lang3.StringUtils.isNotBlank;

/**
 * Provider para a ação de exibição do Help do campo.
 */
public class SInstanceHelpActionsProvider implements ISInstanceActionsProvider {

    @Override
    public Iterable<SInstanceAction> getActions(ISInstanceActionCapable target, SInstance instance) {
        if ((instance.getParent() instanceof SIComposite) && (instance.getParent().getParent() instanceof SIList<?>)) {
            SIList<?> parent = (SIList<?>) instance.getParent().getParent();
            if (!this.getListFieldActions(target, parent, instance.getType().getNameSimple()).isEmpty()) {
                return Collections.emptyList();
            }
        }
        return doHelpAction(instance.asAtr().getLabel(), instance.asAtr().getHelp());
    }

    @Override
    public List<SInstanceAction> getListFieldActions(ISInstanceActionCapable target, SIList<?> instance, String field) {
        final AtrBasic atr;
        if ((instance.getElementsType() instanceof STypeComposite<?>) && isNotBlank(field)) {
            STypeComposite<?> compositeType = (STypeComposite<?>) instance.getElementsType();
            SType<?>          foundField    = compositeType.getField(field);
            if (foundField != null) {
                atr = foundField.asAtr();
            } else {
                atr = instance.getElementsType().asAtr();
            }
        } else {
            atr = instance.getElementsType().asAtr();
        }
        return doHelpAction(atr.getLabel(), atr.getHelp());
    }

    private List<SInstanceAction> doHelpAction(String title, final String helpText) {
        return (isBlank(helpText))
                ? Collections.emptyList()
                : Arrays.asList(new SInstanceAction(SInstanceAction.ActionType.NORMAL)
                .setIcon(SIcon.resolve("question"))
                .setText("Ajuda")
                .setPosition(Integer.MIN_VALUE)
                .setImportant(true)
                .setPreview(new SInstanceAction.Preview()
                        .setTitle(title)
                        .setMessage(helpText)
                        .setFormat("HTML")));
    }
}
