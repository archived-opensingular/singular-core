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
