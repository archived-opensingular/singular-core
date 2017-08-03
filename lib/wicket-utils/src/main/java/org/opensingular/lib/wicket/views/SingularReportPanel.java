/*
 * Copyright (C) 2016 Singular Studios (a.k.a Atom Tecnologia) - www.opensingular.com
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
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

package org.opensingular.lib.wicket.views;

import org.apache.commons.lang3.text.WordUtils;
import org.apache.wicket.Component;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.form.AjaxButton;
import org.apache.wicket.behavior.Behavior;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Button;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.link.DownloadLink;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.markup.repeater.RepeatingView;
import org.apache.wicket.model.Model;
import org.apache.wicket.util.time.Duration;
import org.jetbrains.annotations.NotNull;
import org.opensingular.lib.commons.extension.SingularExtensionUtil;
import org.opensingular.lib.commons.lambda.ISupplier;
import org.opensingular.lib.commons.report.ReportMetadata;
import org.opensingular.lib.commons.report.SingularReport;
import org.opensingular.lib.commons.util.FormatUtil;
import org.opensingular.lib.commons.views.*;
import org.opensingular.lib.wicket.views.plugin.ButtonReportPlugin;

import java.io.File;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static org.opensingular.lib.wicket.util.util.Shortcuts.$b;
import static org.opensingular.lib.wicket.util.util.Shortcuts.$m;

public abstract class SingularReportPanel<R extends ReportMetadata<T>, T> extends Panel {
    private final ISupplier<SingularReport<R, T>> singularReportSupplier;
    private final List<ButtonReportPlugin> buttonReportPlugins;

    private Form<Void> form;
    private RepeatingView pluginContainerView;
    private WicketViewWrapperForViewOutputHtml table;
    private Button exportButton;
    private ListView<ViewOutputFormat> formats;

    public SingularReportPanel(String id, ISupplier<SingularReport<R, T>> singularReportSupplier) {
        super(id);
        this.singularReportSupplier = singularReportSupplier;
        this.buttonReportPlugins = SingularExtensionUtil.get().findExtensionByClass(ButtonReportPlugin.class);
    }

    @Override
    protected void onInitialize() {
        super.onInitialize();
        addForm();
        addTitle();
        addTable();
        addPluginContainerView();
        addExportButton();
        addExportButtons();
        addPluginButtons();
        buttonReportPlugins.forEach(b -> {
            b.init(singularReportSupplier.get());
            b.onBuild(SingularReportPanel.this);
        });
    }

    private void addPluginContainerView() {
        pluginContainerView = new RepeatingView("plugin-container-view");
        form.add(pluginContainerView);
    }

    private void addForm() {
        form = new Form<>("form");
        add(form);
    }

    private void addExportButton() {
        exportButton = new Button("export");
        form.add(exportButton);
    }

    private void addPluginButtons() {
        form.add(new ButtonReportListView());
    }

    private void addTable() {
        table = new WicketViewWrapperForViewOutputHtml("table", this::makeViewGenerator);
        form.add(table);
    }


    private ViewGenerator makeViewGenerator() {
        R reportMetadata = makeReportMetadata();
        buttonReportPlugins.forEach(b -> b.updateReportMetatada(reportMetadata));
        return getSingularReport().map(r -> r.makeViewGenerator(reportMetadata)).orElse(null);
    }

    protected abstract R makeReportMetadata();

    private void addTitle() {
        form.add(new Label("title", getSingularReport().map(SingularReport::getReportName).orElse("")));
    }

    private List<ViewOutputFormat> exportFormatList() {
        final ViewGenerator vg = makeViewGenerator();
        if (vg instanceof ViewMultiGenerator) {
            final List<ViewOutputFormat> enabledFormats = getSingularReport()
                    .map(SingularReport::getEnabledExportFormats).orElse(Collections.emptyList());
            return ((ViewMultiGenerator) vg).getDirectSupportedFormats().stream()
                    .filter(enabledFormats::contains).collect(Collectors.toList());
        }
        return Collections.emptyList();
    }

    public void addExportButtons() {
        formats = new ListView<ViewOutputFormat>("export-list-item", $m.get(this::exportFormatList)) {
            @Override
            protected void populateItem(ListItem<ViewOutputFormat> item) {
                if (item.getModelObject() instanceof ViewOutputFormatExportable) {
                    addDownloadLinkToItem(item);
                } else {
                    item.setVisible(false);
                }
            }
        };
        formats.add(new Behavior() {
            @Override
            public void onConfigure(Component component) {
                super.onConfigure(component);
                formats.setVisible(component.isEnabledInHierarchy());
            }
        });
        form.add(new WebMarkupContainer("export-list").add(formats));
    }

    private void addDownloadLinkToItem(ListItem<ViewOutputFormat> item) {
        DownloadLink downloadLink = new DownloadLink("export-link", new Model<File>() {
            @Override
            public File getObject() {
                return ViewsUtil.exportToTempFile(makeViewGenerator(), (ViewOutputFormatExportable) item.getModelObject());
            }
        }, generateExportFileName(item));
        downloadLink.setDeleteAfterDownload(true);
        downloadLink.setCacheDuration(Duration.NONE);
        downloadLink.add(new Label("export-label", WordUtils.capitalize(item.getModelObject().getName().toLowerCase())));
        item.add(downloadLink);
    }

    @NotNull
    private String generateExportFileName(ListItem<ViewOutputFormat> item) {
        return singularReportSupplier.get().getReportName()
                + " "
                + FormatUtil.dateToDefaultTimestampString(new Date())
                + "."
                + ((ViewOutputFormatExportable) item.getModelObject()).getFileExtension();
    }

    private class ButtonReportListView extends ListView<ButtonReportPlugin> {
        public ButtonReportListView() {
            super("plugin-buttons", buttonReportPlugins);
        }

        @Override
        protected void populateItem(ListItem<ButtonReportPlugin> item) {
            final ButtonReportPlugin buttonReportPlugin = item.getModelObject();
            AjaxButton pluginButton = new AjaxButton("plugin-button") {
                @Override
                protected void onSubmit(AjaxRequestTarget target, Form<?> form) {
                    super.onSubmit(target, form);
                    buttonReportPlugin.onAction(target, makeViewGenerator());
                }
            };
            addButtonIcon(buttonReportPlugin, pluginButton);
            addButtonLabel(buttonReportPlugin, pluginButton);
            item.add(pluginButton);
            item.setRenderBodyOnly(true);
        }

        private void addButtonLabel(ButtonReportPlugin buttonReportPlugin, AjaxButton pluginButton) {
            pluginButton.add(new Label("button-label", buttonReportPlugin.getName()).setRenderBodyOnly(true));
        }

        private void addButtonIcon(ButtonReportPlugin buttonReportPlugin, AjaxButton pluginButton) {
            String icoCss = "";
            if (buttonReportPlugin.getIcon() != null) {
                icoCss = buttonReportPlugin.getIcon().getCssClass();
            }
            pluginButton.add(new WebMarkupContainer("button-icon").add($b.classAppender(icoCss)));
        }

    }

    private Optional<SingularReport<R, T>> getSingularReport() {
        return Optional.ofNullable(singularReportSupplier.get());
    }

    public List<ButtonReportPlugin> getButtonReportPlugins() {
        return buttonReportPlugins;
    }

    public Form<Void> getForm() {
        return form;
    }

    public RepeatingView getPluginContainerView() {
        return pluginContainerView;
    }

    public WicketViewWrapperForViewOutputHtml getTable() {
        return table;
    }

    public Button getExportButton() {
        return exportButton;
    }
}