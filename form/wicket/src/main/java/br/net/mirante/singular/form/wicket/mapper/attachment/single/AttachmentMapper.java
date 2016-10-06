/*
 * Copyright (c) 2016, Mirante and/or its affiliates. All rights reserved.
 * Mirante PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package br.net.mirante.singular.form.wicket.mapper.attachment.single;

import org.apache.wicket.Component;
import org.apache.wicket.model.IModel;

import br.net.mirante.singular.form.SInstance;
import br.net.mirante.singular.form.SingularFormException;
import br.net.mirante.singular.form.type.core.attachment.SIAttachment;
import br.net.mirante.singular.form.wicket.IAjaxUpdateListener;
import br.net.mirante.singular.form.wicket.WicketBuildContext;
import br.net.mirante.singular.form.wicket.enums.ViewMode;
import br.net.mirante.singular.form.wicket.mapper.AbstractControlsFieldComponentMapper;
import br.net.mirante.singular.form.wicket.mapper.attachment.DownloadLink;
import br.net.mirante.singular.form.wicket.mapper.attachment.DownloadSupportedBehavior;
import br.net.mirante.singular.util.wicket.bootstrap.layout.BSControls;
import br.net.mirante.singular.util.wicket.bootstrap.layout.BSWellBorder;
import br.net.mirante.singular.util.wicket.bootstrap.layout.TemplatePanel;

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

        String markup = ""
            + "\n<div wicket:id='well'>"
            + "\n  <div stype='min-height: 20px; white-space: pre-wrap; word-wrap: break-word;'>"
            + "\n    <i class='fa fa-file'></i>"
            + "\n    <a wicket:id='downloadLink'></a>"
            + "\n  </div>"
            + "\n</div>"
            + "\n";

        final BSWellBorder well = BSWellBorder.small("well");
        final TemplatePanel panel = new TemplatePanel("_readOnlyAttachment", markup);
        final DownloadSupportedBehavior downloadSupportedBehavior = new DownloadSupportedBehavior(model);
        final DownloadLink downloadLink = new DownloadLink("downloadLink", (IModel<SIAttachment>) model, downloadSupportedBehavior);
        panel.add(downloadSupportedBehavior);
        well.add(downloadLink);
        panel.add(well);
        formGroup.appendTag("div", panel);
        return panel;
    }

}
