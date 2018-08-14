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

package org.opensingular.form.view.list;

import org.opensingular.form.SIList;
import org.opensingular.form.SInstance;
import org.opensingular.form.view.AbstractSViewList;
import org.opensingular.lib.commons.lambda.IFunction;
import org.opensingular.lib.commons.lambda.IPredicate;
import org.opensingular.lib.commons.ui.Icon;

import java.util.Optional;

public class AbstractSViewListWithControls<SELF extends AbstractSViewList> extends AbstractSViewList {

    private IFunction<SIList, Boolean> newEnabled = list -> Boolean.TRUE;
    private int initialLines;
    private String label;
    private ButtonsConfig buttonsConfig = new ButtonsConfig();

    public ButtonsConfig getButtonsConfig() {
        return buttonsConfig;
    }

    public SELF configureEditButton(String hint, IPredicate<SInstance> visibleFor, Icon icon) {
        getButtonsConfig().setEditButton(new ButtonAction(visibleFor, hint, icon));
        return (SELF) this;
    }
    public SELF configureEditButton(IPredicate<SInstance> visibleFor) {
        getButtonsConfig().setEditButton(new ButtonAction(visibleFor, "Editar", null));
        return (SELF) this;
    }

    public SELF configureDeleteButton(String hint, IPredicate<SInstance> visibleFor, Icon icon) {
        getButtonsConfig().setDeleteButton(new ButtonAction(visibleFor, hint, icon));
        return (SELF) this;
    }

    public SELF configureDeleteButton(IPredicate<SInstance> visibleFor) {
        getButtonsConfig().setDeleteButton(new ButtonAction(visibleFor, "Remover", null));
        return (SELF) this;
    }


    public boolean isInsertEnabled() {
        return true;
        //TODO VERIFICAR ISSO AQUI.
    }


    public final boolean isNewEnabled(SIList list) {
        return newEnabled.apply(list);
    }


    public int getInitialNumberOfLines() {
        return initialLines;
    }

    public final SELF enableNew() {
        return setNewEnabled(true);
    }

    public final SELF disableNew() {
        return setNewEnabled(false);
    }

    /**
     * Configure the number of empty lines that must be added to SIList
     * @param numberOfLines
     * @return
     */
    public final SELF setInitialNumberOfLines(int numberOfLines) {
        this.initialLines = numberOfLines;
        return (SELF) this;
    }

    public final SELF setNewEnabled(boolean newEnabled) {
        this.newEnabled = list -> newEnabled;
        return (SELF) this;
    }

    public final SELF setNewEnabled(IFunction<SIList, Boolean> enabledFunction) {
        this.newEnabled = enabledFunction;
        return (SELF) this;
    }

    public final SELF label(String label) {
        this.label = label;
        return (SELF) this;
    }

    public Optional<String> label() {
        return Optional.ofNullable(this.label);
    }

}
