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

package org.opensingular.form.wicket.mapper.attachment;

import static org.opensingular.lib.wicket.util.util.Shortcuts.$b;
import static org.opensingular.lib.wicket.util.util.WicketUtils.$m;

import org.apache.wicket.core.util.string.JavaScriptUtils;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.model.IModel;

import org.opensingular.form.type.core.attachment.SIAttachment;
import org.opensingular.lib.wicket.util.model.IReadOnlyModel;
import org.opensingular.lib.wicket.util.util.WicketUtils;

/**
 * Classe de link para utilização em conjunto dom {@link DownloadSupportedBehavior}
 * para disponibilizar links de download de um único uso.
 */
public class DownloadLink extends Link<Void> {

    private IModel<SIAttachment>      model;
    private DownloadSupportedBehavior downloadSupportedBehaviour;

    public DownloadLink(String id, IModel<SIAttachment> model, DownloadSupportedBehavior downloadSupportedBehaviour) {
        super(id);
        this.model = model;
        this.downloadSupportedBehaviour = downloadSupportedBehaviour;

    }

    @Override
    protected void onInitialize() {
        super.onInitialize();
        this.add($b.attr("onclick",
            (IReadOnlyModel<String>) () -> "DownloadSupportedBehavior.ajaxDownload(" +
                jsStringOrNull(downloadSupportedBehaviour.getUrl()) + "," +
                jsStringOrNull(model.getObject().getFileId()) + "," +
                jsStringOrNull(model.getObject().getFileName()) +
                ");" +
                "return false;"));
        configureBody();
        add(WicketUtils.$b.attr("title", $m.ofValue(model.getObject().getFileName())));
        add($b.attr("target", "_blank"));
    }

    public void configureBody() {
        this.setBody($m.property(model, "fileName"));
    }

    private static String jsStringOrNull(String s) {
        return (s == null) ? "null" : "'" + JavaScriptUtils.escapeQuotes(s) + "'";
    }

    @Override
    protected void onConfigure() {
        super.onConfigure();
        setEnabled(isFileAssigned());
    }

    protected boolean isFileAssigned() {
        return (model.getObject() != null) && (model.getObject().getFileId() != null);
    }

    @Override
    public void onClick() {}

}
