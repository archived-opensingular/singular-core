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

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.form.AjaxButton;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Button;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.link.DownloadLink;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.Model;
import org.apache.wicket.util.time.Duration;
import org.opensingular.lib.commons.lambda.ISupplier;
import org.opensingular.lib.commons.report.ReportMetadata;
import org.opensingular.lib.commons.report.SingularReport;
import org.opensingular.lib.commons.views.ViewGenerator;
import org.opensingular.lib.commons.views.ViewOutputFormat;
import org.opensingular.lib.commons.views.ViewsUtil;
import org.opensingular.lib.wicket.util.modal.BSModalBorder;

import java.io.File;
import java.util.Optional;

import static org.opensingular.lib.wicket.util.util.Shortcuts.$b;

public abstract class SingularReportPanel<R extends ReportMetadata<T>, T> extends Panel {
    private final ISupplier<SingularReport<R, T>> singularReportSupplier;

    private Form<Void> form;
    private BSModalBorder searchModal;

    public SingularReportPanel(String id, ISupplier<SingularReport<R, T>> singularReportSupplier) {
        super(id);
        this.singularReportSupplier = singularReportSupplier;
    }

    @Override
    protected void onInitialize() {
        super.onInitialize();
        addForm();
        addTitle();
        addTable();
        addSearchModal();
        addExportButton();
        addExportExcelLink();
        addSearchButton();
    }

    private void addSearchModal() {
        searchModal = new BSModalBorder("search-modal");
        form.add(searchModal);
        customizeModal(searchModal);
    }

    protected abstract void customizeModal(BSModalBorder searchModal);

    private void addForm() {
        form = new Form<>("form");
        add(form);
    }

    private void addExportButton() {
        Button exportButton = new Button("export");
        form.add(exportButton);
        exportButton.add($b.enabledIf(this::isShowReport));
    }

    private void addSearchButton() {
        AjaxButton ajaxButton = new AjaxButton("search") {
            @Override
            protected void onSubmit(AjaxRequestTarget target, Form<?> form) {
                super.onSubmit(target, form);
                searchModal.show(target);
            }
        };
        form.add(ajaxButton);
    }

    private void addTable() {
        form.add(new WicketViewWrapperForViewOutputHtml("table", this::makeViewGenerator).add($b.visibleIf(this::isShowReport)));
    }

    protected Boolean isShowReport() {
        return Boolean.TRUE;
    }

    private ViewGenerator makeViewGenerator() {
        return getSingularReport().map(r -> r.makeViewGenerator(getReportMetadata())).orElse(null);
    }

    protected abstract R getReportMetadata();

    private void addTitle() {
        form.add(new Label("title", getSingularReport().map(SingularReport::getReportName).orElse("")));
    }

    public void addExportExcelLink() {
        DownloadLink downloadLink = new DownloadLink("excel", new Model<File>() {
            @Override
            public File getObject() {
                return ViewsUtil.exportToTempFile(makeViewGenerator(), ViewOutputFormat.EXCEL);
            }
        }, "report.xlsx");
        downloadLink.setDeleteAfterDownload(true);
        downloadLink.setCacheDuration(Duration.NONE);
        downloadLink.add($b.visibleIf(this::isShowReport));
        form.add(downloadLink);
    }

    private Optional<SingularReport<R, T>> getSingularReport() {
        return Optional.ofNullable(singularReportSupplier.get());
    }

    public Form<Void> getForm() {
        return form;
    }
}
