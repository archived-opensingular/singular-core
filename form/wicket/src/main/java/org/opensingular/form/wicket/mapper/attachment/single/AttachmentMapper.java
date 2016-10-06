/*
 * Copyright (c) 2016, Mirante and/or its affiliates. All rights reserved.
 * Mirante PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package org.opensingular.form.wicket.mapper.attachment.single;

import org.apache.wicket.Component;
import org.apache.wicket.model.IModel;

import org.opensingular.form.SInstance;
import org.opensingular.form.SingularFormException;
import org.opensingular.form.type.core.attachment.SIAttachment;
import org.opensingular.form.wicket.WicketBuildContext;
import org.opensingular.form.wicket.enums.ViewMode;
import org.opensingular.form.wicket.mapper.AbstractControlsFieldComponentMapper;
import org.opensingular.form.wicket.mapper.attachment.DownloadLink;
import org.opensingular.form.wicket.mapper.attachment.DownloadSupportedBehavior;
import org.opensingular.form.wicket.IAjaxUpdateListener;
import org.opensingular.lib.wicket.util.bootstrap.layout.BSControls;
import org.opensingular.lib.wicket.util.bootstrap.layout.BSWellBorder;
import org.opensingular.lib.wicket.util.bootstrap.layout.TemplatePanel;

public class AttachmentMapper extends AbstractControlsFieldComponentMapper {

    @Override
    @SuppressWarnings("unchecked")
    public Component appendInput(WicketBuildContext ctx, BSControls formGroup, IModel<String> labelModel) {
        final IModel<? extends SInstance> model = ctx.getModel();
        final FileUploadPanel container = new FileUploadPanel("container", (IModel<SIAttachment>) model, ViewMode.EDIT);
        formGroup.appendDiv(container);
        return container.getUploadField();
    }

    @Override
    public void addAjaxUpdate(Component component, IModel<SInstance> model, IAjaxUpdateListener listener) {}

    @Override
    public String getReadOnlyFormattedText(IModel<? extends SInstance> model) {
        throw new SingularFormException("Este metodo não deve ser acessado, para download utilizar appendReadOnlyInput");
    }

    @Override
    @SuppressWarnings("unchecked")
    public Component appendReadOnlyInput(WicketBuildContext ctx, BSControls formGroup, IModel<String> labelModel) {
        final IModel<? extends SInstance> model = ctx.getModel();

        String markup = "";
        markup += " <div wicket:id='well'> ";
        markup += "     <div stype='min-height: 20px; white-space: pre-wrap; word-wrap: break-word;'> ";
        markup += "         <i class='fa fa-file'></i> ";
        markup += "         <a wicket:id='downloadLink'></a> ";
        markup += "     </div> ";
        markup += " </div> ";

        final BSWellBorder well = BSWellBorder.small("well");
        final TemplatePanel panel = new TemplatePanel("_readOnlyAttachment", markup);
        final DownloadSupportedBehavior downloadSupportedBehavior = new DownloadSupportedBehavior(model);
        final DownloadLink              downloadLink = new DownloadLink("downloadLink", (IModel<SIAttachment>) model, downloadSupportedBehavior);
        panel.add(downloadSupportedBehavior);
        well.add(downloadLink);
        panel.add(well);
        formGroup.appendTag("div", panel);
        return panel;
    }

}
