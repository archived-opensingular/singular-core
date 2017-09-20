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

import org.apache.wicket.Component;
import org.apache.wicket.behavior.Behavior;
import org.apache.wicket.markup.ComponentTag;
import org.apache.wicket.markup.MarkupStream;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.model.IModel;

import org.apache.wicket.model.Model;
import org.opensingular.form.type.core.attachment.SIAttachment;
import org.opensingular.lib.wicket.util.util.WicketUtils;

import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Classe de link para utilização em conjunto dom {@link DownloadSupportedBehavior}
 * para disponibilizar links de download de um único uso.
 */
public class DownloadLink extends WebMarkupContainer {


    private static final String FILE_REGEX_PATTERN = ".*\\.(.*)";
    private static final Set<String> SUPPORTED_EXTENSIONS = new LinkedHashSet<>(Arrays.asList("pdf", "jpg", "gif", "png"));

    private IModel<SIAttachment> model;
    private IModel<Boolean> openInNewTabIfIsBrowserFriendly;
    private DownloadSupportedBehavior downloadSupportedBehaviour;

    public DownloadLink(String id, IModel<SIAttachment> model, DownloadSupportedBehavior downloadSupportedBehaviour) {
        this(id, model, downloadSupportedBehaviour, Model.of(Boolean.TRUE));
    }

    public DownloadLink(String id, IModel<SIAttachment> model, DownloadSupportedBehavior downloadSupportedBehaviour
            , IModel<Boolean> openInNewTabIfIsBrowserFriendly) {
        super(id);
        this.model = model;
        this.downloadSupportedBehaviour = downloadSupportedBehaviour;
        this.openInNewTabIfIsBrowserFriendly = openInNewTabIfIsBrowserFriendly;
    }

    @Override
    protected void onInitialize() {
        super.onInitialize();
        add(new Behavior() {
            @Override
            public void onComponentTag(Component component, ComponentTag tag) {
                super.onComponentTag(component, tag);
                if (!model.getObject().isEmptyOfData()) {
                    tag.getAttributes().put("href", downloadSupportedBehaviour.getDownloadURL(model.getObject().getFileId(), model.getObject().getFileName()));
                }
            }
        });
    }

    @Override
    public void onComponentTagBody(MarkupStream markupStream, ComponentTag openTag) {
        super.onComponentTagBody(markupStream, openTag);
        String fileName = model.getObject().getFileName();
        if (fileName != null) {
            getResponse().write(fileName);
            openTag.getAttributes().put("title", fileName);
        }
    }

    @Override
    protected void onConfigure() {
        super.onConfigure();
        add(WicketUtils.$b.attr("title", $m.ofValue(model.getObject().getFileName())));
        if (openInNewTabIfIsBrowserFriendly.getObject()
                && isContentTypeBrowserFriendly(model.getObject().getFileName())) {
            add($b.attr("target", "_blank"));
        }
        setEnabled(isFileAssigned());
    }

    protected boolean isFileAssigned() {
        return (model.getObject() != null) && (model.getObject().getFileId() != null);
    }

    private boolean isContentTypeBrowserFriendly(String filename) {
        return filename != null && isContentTypeBrowserFriendly(Pattern.compile(FILE_REGEX_PATTERN).matcher(filename));
    }

    private boolean isContentTypeBrowserFriendly(Matcher matcher) {
        return matcher.matches() && SUPPORTED_EXTENSIONS.contains(matcher.group(1));
    }

}
