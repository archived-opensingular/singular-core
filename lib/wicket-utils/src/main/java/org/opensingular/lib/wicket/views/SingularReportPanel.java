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
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.link.DownloadLink;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.opensingular.lib.commons.lambda.ISupplier;
import org.opensingular.lib.commons.views.ViewGenerator;
import org.opensingular.lib.commons.views.ViewOutputFormat;
import org.opensingular.lib.commons.views.ViewOutputFormatExportable;
import org.opensingular.lib.commons.views.ViewsUtil;
import org.opensingular.lib.wicket.util.modal.BSModalBorder;

import java.io.File;

public class SingularReportPanel extends Panel {
    private final ISupplier<ViewGenerator> viewGeneratorSupplier;
    private final BSModalBorder filterModalBorder;

    private IModel<String> titleModel = new Model<>();

    public SingularReportPanel(String id, ISupplier<ViewGenerator> viewGeneratorSupplier, BSModalBorder filterModalBorder) {
        super(id);
        this.viewGeneratorSupplier = viewGeneratorSupplier;
        this.filterModalBorder = filterModalBorder;
        addTitle();
        addTable();
        addExportExcelLink();
        addSearch();
    }

    private void addSearch() {
        AjaxButton ajaxButton = new AjaxButton("search") {
            @Override
            protected void onSubmit(AjaxRequestTarget target, Form<?> form) {
                super.onSubmit(target, form);
                if (filterModalBorder != null) {
                    filterModalBorder.show(target);
                }
            }
        };
        add(ajaxButton);
        ajaxButton.setVisible(filterModalBorder != null);
    }

    private void addTable() {
        add(new WicketViewWrapperForViewOutputHtml("table", viewGeneratorSupplier));
    }

    private void addTitle() {
        add(new Label("title", titleModel));
    }

    public SingularReportPanel setTitle(String title) {
        titleModel.setObject(title);
        return this;
    }

    public void addExportExcelLink() {
        DownloadLink downloadLink = new DownloadLink("excel", new Model<File>() {
            @Override
            public File getObject() {
                    ViewOutputFormatExportable format = ViewOutputFormat.EXCEL;
                    return ViewsUtil.exportToTempFile(viewGeneratorSupplier.get(), format);
            }
        }, "report.xlsx");
        downloadLink.setDeleteAfterDownload(true);
        add(downloadLink);
    }
}
