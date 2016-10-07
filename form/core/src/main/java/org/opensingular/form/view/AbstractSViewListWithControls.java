/*
 * Copyright (c) 2016, Singular and/or its affiliates. All rights reserved.
 * Singular PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package org.opensingular.form.view;

import java.util.Optional;

public class AbstractSViewListWithControls<SELF extends AbstractSViewList> extends AbstractSViewList {

    private boolean newEnabled = true;
    private boolean insertEnabled = false;
    private boolean deleteEnabled = true;
    private String label;

    public final boolean isNewEnabled() {
        return newEnabled;
    }

    public final boolean isDeleteEnabled() {
        return deleteEnabled;
    }

    public final boolean isInsertEnabled() {
        return insertEnabled;
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

    public final SELF disableDelete() {
        return setDeleteEnabled(false);
    }

    public final SELF disableInsert() {
        return setInsertEnabled(false);
    }

    public final SELF setNewEnabled(boolean newEnabled) {
        this.newEnabled = newEnabled;
        return (SELF) this;
    }

    public final SELF setDeleteEnabled(boolean deleteEnabled) {
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
