package br.net.mirante.singular.util.wicket.bootstrap.layout;

import static br.net.mirante.singular.util.wicket.util.WicketUtils.$b;
import static br.net.mirante.singular.util.wicket.util.WicketUtils.$m;

import java.io.Serializable;

import org.apache.wicket.Component;

import br.net.mirante.singular.util.wicket.bootstrap.layout.IBSGridCol.BSGridSize;

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
        int labelColspan, String labelFor, Serializable labelValueOrModel,
        int controlsColspan, IBSComponentFactory<BSControls> factory) {

        return appendSubgroupLabelControls(
            labelColspan, labelFor, labelValueOrModel,
            controlsColspan, factory);
    }

    public BSFormGroup appendSubgroupLabelControlsFeedback(
        int labelColspan, Component labelFor, Serializable labelValueOrModel,
        int controlsColspan, IBSComponentFactory<BSControls> factory) {

        return appendSubgroupLabelControls(
            labelColspan, labelFor, labelValueOrModel,
            controlsColspan, factory);
    }

    public BSFormGroup appendSubgroupLabelControls(
        int labelColspan, String labelFor, Serializable labelValueOrModel,
        int controlsColspan, IBSComponentFactory<BSControls> factory) {

        return newSubgroup()
            .appendLabel(labelColspan, labelFor, labelValueOrModel)
            .appendControls(controlsColspan, factory);
    }

    public BSFormGroup appendSubgroupLabelControls(
        int labelColspan, Component labelFor, Serializable labelValueOrModel,
        int controlsColspan, IBSComponentFactory<BSControls> factory) {

        return newSubgroup()
            .appendLabel(labelColspan, labelFor, labelValueOrModel)
            .appendControls(controlsColspan, factory);
    }

    public BSFormGroup appendLabel(int colspan, Serializable valueOrModel) {
        return appendLabel(colspan, (String) null, valueOrModel);
    }

    public BSFormGroup appendLabel(int colspan, Component labelFor, Serializable valueOrModel) {
        return appendLabel(colspan, labelFor.getId(), valueOrModel);
    }

    public BSFormGroup appendLabel(int colspan, String labelFor, Serializable valueOrModel) {
        BSLabel label = newComponent(this::newLabel);
        getDefaultGridSize().col(label, colspan)
            .setContainer(this)
            .setTargetComponentIds(labelFor)
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
