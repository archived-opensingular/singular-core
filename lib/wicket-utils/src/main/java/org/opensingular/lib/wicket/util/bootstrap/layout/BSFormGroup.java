/*
 * Copyright (c) 2016, Singular and/or its affiliates. All rights reserved.
 * Singular PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package org.opensingular.lib.wicket.util.bootstrap.layout;

import static org.opensingular.lib.wicket.util.util.WicketUtils.$b;
import static org.opensingular.lib.wicket.util.util.WicketUtils.$m;

import java.io.Serializable;

import org.apache.wicket.Component;

public class BSFormGroup extends BSContainer<BSFormGroup> {

    private IBSGridCol.BSGridSize defaultGridSize;

    public BSFormGroup(String id, IBSGridCol.BSGridSize defaultGridSize) {
        super(id);
        setDefaultGridSize(defaultGridSize);
        setCssClass("form-group");
        add($b.classAppender("can-have-error"));
    }

    public IBSGridCol.BSGridSize getDefaultGridSize() {
        return defaultGridSize;
    }

    public BSFormGroup setDefaultGridSize(IBSGridCol.BSGridSize defaultGridSize) {
        this.defaultGridSize = defaultGridSize;
        return this;
    }

    public BSFormGroup appendSubgroup() {
        return appendComponent(id -> newSubgroup());
    }

    public BSFormGroup newSubgroup() {
        return newComponent(id -> newSubgroup(id).setCssClass(null));
    }

    public BSFormGroup appendSubgroupLabelControlsFeedback(
        int labelColspan, Component labelFor, Serializable labelValueOrModel,
        int controlsColspan, IBSComponentFactory<BSControls> factory) {

        return appendSubgroupLabelControls(
            labelColspan, labelFor, labelValueOrModel,
            controlsColspan, factory);
    }

    public BSFormGroup appendSubgroupLabelControls(
        int labelColspan, Component labelFor, Serializable labelValueOrModel,
        int controlsColspan, IBSComponentFactory<BSControls> factory) {

        return newSubgroup()
            .appendLabel(labelColspan, labelFor, labelValueOrModel)
            .appendControls(controlsColspan, factory);
    }

    public BSFormGroup appendLabel(int colspan, Serializable valueOrModel) {
        return appendLabel(colspan, null, valueOrModel);
    }

    public BSFormGroup appendLabel(int colspan, Component labelFor, Serializable valueOrModel) {
        BSLabel label = newComponent(this::newLabel);
        getDefaultGridSize().col(label, colspan)
            .setTargetComponent(labelFor)
            .setDefaultModel($m.wrapValue(valueOrModel));
        return this;
    }

    public BSFormGroup appendControls(int colspan, IBSComponentFactory<BSControls> factory) {
        BSControls controls = newComponent(factory);
        getDefaultGridSize().col(controls, colspan);
        return this;
    }

    public BSControls newControls(int colspan) {
        return newControls(colspan, BSControls::new);
    }

    public BSControls newControls(int colspan, IBSComponentFactory<BSControls> factory) {
        BSControls controls = newComponent(factory);
        return getDefaultGridSize().col(controls, colspan);
    }

    protected BSLabel newLabel(String id) {
        return new BSLabel(id);
    }

    protected BSControls newControls(String id) {
        return new BSControls(id);
    }

    protected BSFormGroup newSubgroup(String id) {
        return new BSFormGroup(id, getDefaultGridSize());
    }
}
