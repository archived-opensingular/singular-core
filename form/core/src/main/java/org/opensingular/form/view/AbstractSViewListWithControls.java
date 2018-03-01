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

package org.opensingular.form.view;

import org.opensingular.form.SIList;
import org.opensingular.form.SInstance;
import org.opensingular.lib.commons.lambda.IFunction;

import java.util.Optional;

public class AbstractSViewListWithControls<SELF extends AbstractSViewList> extends AbstractSViewList {

    private IFunction<SIList, Boolean> newEnabled = list -> Boolean.TRUE;
    private IFunction<SInstance, Boolean> deleteEnabled = si -> Boolean.TRUE;
    private boolean insertEnabled = false;
    private int initialLines;
    private String label;


    public final boolean isNewEnabled(SIList list) {
        return newEnabled.apply(list);
    }

    public final boolean isDeleteEnabled(SInstance instance) {
        return deleteEnabled.apply(instance);
    }

    public final boolean isInsertEnabled() {
        return insertEnabled;
    }

    public int getInitialNumberOfLines() {
        return initialLines;
    }

    public final SELF enableNew() {
        return setNewEnabled(true);
    }

    public final SELF enableDelete() {
        return setDeleteEnabled(true);
    }

    public final SELF enabledInsert() {
        return setInsertEnabled(true);
    }

    public final SELF disableNew() {
        return setNewEnabled(false);
    }

    public final SELF disableInsert() {
        return setInsertEnabled(false);
    }

    public final SELF disableDelete() {
        return setDeleteEnabled(false);
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

    public final SELF setDeleteEnabled(boolean deleteEnabled) {
        this.deleteEnabled = list -> deleteEnabled;
        return (SELF) this;
    }

    public final SELF setDeleteEnabled(IFunction<SInstance, Boolean> deleteEnabled) {
        this.deleteEnabled = deleteEnabled;
        return (SELF) this;
    }

    public final SELF setInsertEnabled(boolean insertEnabled) {
        this.insertEnabled = insertEnabled;
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
