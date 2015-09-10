package br.net.mirante.singular.view.page.dashboard;

import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.ResourceModel;

import br.net.mirante.singular.util.wicket.resource.Color;
import br.net.mirante.singular.util.wicket.resource.Icone;

import static br.net.mirante.singular.util.wicket.util.WicketUtils.$b;
import static br.net.mirante.singular.util.wicket.util.WicketUtils.$m;

public class StatusPanel extends Panel {

    private String labelKey;
    private Integer value;
    private Icone icon;
    private Color color;

    private boolean withProgress;
    private String progressLabelKey;
    private Integer progressValue;

    public StatusPanel(String id, String labelKey, Integer value) {
        super(id);
        this.labelKey = labelKey;
        this.value = value;
        this.icon = Icone.PIE;
        this.color = Color.BLUE_SHARP;
        this.withProgress = false;
        this.progressLabelKey = "label.progress.label";
        this.progressValue = 0;
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
        add(new Label("value", $m.ofValue(value)).add($b.attr("class", color.getFontCssClass())));
        add(new WebMarkupContainer("icon").add($b.attr("class", icon.getCssClass())));
        WebMarkupContainer progress = new WebMarkupContainer("progress");
        if (!withProgress) {
            add($b.attr("class", "without-progress-bar"));
            progress.add($b.attrAppender("class", "hide", " "));
        }
        WebMarkupContainer progressBar = new WebMarkupContainer("progressCSSValue");
        progressBar.add($b.attrAppender("class", color.getCssClass(), " "));
        progressBar.add($b.attr("style", String.format("width: %d%%;", progressValue)));
        progressBar.add(new Label("progressLabelValue", $m.ofValue(String.format("%d%%", progressValue))));
        progress.add(progressBar);
        progress.add(new Label("progressLabel", new ResourceModel(progressLabelKey)));
        progress.add(new Label("progressValue", $m.ofValue(String.format("%d%%", progressValue))));
        add(progress);
    }
}
