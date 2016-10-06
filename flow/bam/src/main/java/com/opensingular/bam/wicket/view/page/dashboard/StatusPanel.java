/*
 * Copyright (c) 2016, Mirante and/or its affiliates. All rights reserved.
 * Mirante PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package com.opensingular.bam.wicket.view.page.dashboard;

import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.ResourceModel;

import org.opensingular.singular.util.wicket.resource.Color;
import org.opensingular.singular.util.wicket.resource.Icone;

import static org.opensingular.singular.util.wicket.util.WicketUtils.$b;
import static org.opensingular.singular.util.wicket.util.WicketUtils.$m;

public class StatusPanel extends Panel {

    private String labelKey;
    private Integer value;
    private String unit;
    private Icone icon;
    private Color color;

    private boolean withProgress;
    private String progressLabelKey;
    private Integer progressValue;

    public StatusPanel(String id, String labelKey, Integer value) {
        super(id);
        this.labelKey = labelKey;
        this.value = value;
        this.unit = null;
        this.icon = Icone.PIE;
        this.color = Color.BLUE_SHARP;
        this.withProgress = false;
        this.progressLabelKey = "label.progress.label";
        this.progressValue = 0;
    }

    public StatusPanel setUnit(String unit) {
        this.unit = unit;
        return this;
    }

    public StatusPanel setIcon(Icone icon) {
        this.icon = icon;
        return this;
    }

    public StatusPanel setColor(Color color) {
        this.color = color;
        return this;
    }

    public StatusPanel setProgressLabelKey(String progressLabelKey) {
        this.progressLabelKey = progressLabelKey;
        this.withProgress = true;
        return this;
    }

    public StatusPanel setProgressValue(Integer progressValue) {
        this.progressValue = progressValue;
        this.withProgress = true;
        return this;
    }

    public StatusPanel withProgress(boolean withProgress) {
        this.withProgress = withProgress;
        return this;
    }

    @Override
    protected void onInitialize() {
        super.onInitialize();
        add(new Label("label", new ResourceModel(labelKey)));
        add(new Label("value", $m.property(this, "valueWithUnit")).add($b.classAppender(color.getFontCssClass())));
        add(new WebMarkupContainer("icon").add($b.classAppender(icon.getCssClass())));
        WebMarkupContainer progress = new WebMarkupContainer("progress");
        if (!withProgress) {
            progress.add($b.classAppender("without-progress-bar"));
            progress.add($b.classAppender("hide"));
        }
        WebMarkupContainer progressBar = new WebMarkupContainer("progressCSSValue");
        progressBar.add($b.classAppender(color.getCssClass()));
        progressBar.add($b.attr("style", String.format("width: %d%%;", progressValue)));
        progressBar.add(new Label("progressLabelValue", $m.ofValue(String.format("%d%%", progressValue))));
        progress.add(progressBar);
        progress.add(new Label("progressLabel", new ResourceModel(progressLabelKey)));
        progress.add(new Label("progressValue", $m.ofValue(String.format("%d%%", progressValue))));
        add(progress);
    }

    public String getValueWithUnit() {
        String pattern = value == null ? "---" : unit == null ? "%d" : "%d %s";
        return String.format(pattern, value, unit);
    }
}
