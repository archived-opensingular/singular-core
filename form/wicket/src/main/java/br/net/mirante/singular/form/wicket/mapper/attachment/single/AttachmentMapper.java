/*
 * Copyright (c) 2016, Mirante and/or its affiliates. All rights reserved.
 * Mirante PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package br.net.mirante.singular.form.wicket.mapper.attachment.single;

import br.net.mirante.singular.form.SInstance;
import br.net.mirante.singular.form.SingularFormException;
import br.net.mirante.singular.form.type.core.attachment.SIAttachment;
import br.net.mirante.singular.form.wicket.IAjaxUpdateListener;
import br.net.mirante.singular.form.wicket.enums.ViewMode;
import br.net.mirante.singular.form.wicket.mapper.ControlsFieldComponentAbstractMapper;
import br.net.mirante.singular.form.wicket.mapper.attachment.DownloadLink;
import br.net.mirante.singular.form.wicket.mapper.attachment.DownloadSupportedBehavior;
import br.net.mirante.singular.util.wicket.bootstrap.layout.BSWellBorder;
import br.net.mirante.singular.util.wicket.bootstrap.layout.TemplatePanel;
import org.apache.wicket.Component;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;


public class AttachmentMapper extends ControlsFieldComponentAbstractMapper {

    @Override
    @SuppressWarnings("unchecked")
    public Component appendInput() {
        final FileUploadPanel container = new FileUploadPanel("container", (IModel<SIAttachment>) model, ViewMode.EDITION);
        formGroup.appendDiv(container);
        return container.getUploadField();
    }

    @Override
    public void addAjaxUpdate(Component component, IModel<SInstance> model, IAjaxUpdateListener listener) {
    }

    @Override
    public String getReadOnlyFormattedText(IModel<? extends SInstance> model) {
        throw new SingularFormException("Este metodo não deve ser acessado, para download utilizar appendReadOnlyInput");
    }

    @Override
    @SuppressWarnings("unchecked")
    public Component appendReadOnlyInput() {

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
        final DownloadLink downloadLink = new DownloadLink("downloadLink", (IModel<SIAttachment>) model, downloadSupportedBehavior);
        panel.add(downloadSupportedBehavior);
        well.add(downloadLink);
        panel.add(well);
        formGroup.appendTag("div", panel);
        return panel;
    }

}
