/*
 * Copyright (c) 2016, Mirante and/or its affiliates. All rights reserved.
 * Mirante PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package org.opensingular.singular.util.wicket.bootstrap.layout;

import static org.opensingular.singular.util.wicket.util.WicketUtils.$b;
import static org.opensingular.singular.util.wicket.util.WicketUtils.$m;

import java.io.Serializable;

import org.apache.wicket.Component;

import org.opensingular.singular.util.wicket.bootstrap.layout.IBSGridCol.BSGridSize;

public class BSFormGroup extends BSContainer<BSFormGroup> {

    private BSGridSize defaultGridSize;

    public BSFormGroup(String id, BSGridSize defaultGridSize) {
        super(id);
        setDefaultGridSize(defaultGridSize);
        setCssClass("form-group");
        add($b.classAppender("can-have-error"));
    }

    public BSGridSize getDefaultGridSize() {
        return defaultGridSize;
    }
    public BSFormGroup setDefaultGridSize(BSGridSize defaultGridSize) {
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
