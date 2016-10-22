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

package org.opensingular.form.wicket.mapper.attachment.list;

import org.apache.wicket.ajax.AjaxEventBehavior;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.opensingular.form.SIList;
import org.opensingular.form.type.core.attachment.SIAttachment;
import org.opensingular.form.wicket.WicketBuildContext;
import org.opensingular.form.wicket.mapper.AbstractListaMapper;
import org.apache.wicket.model.IModel;

public class AttachmentListMapper extends AbstractListaMapper {

    public final static String MULTIPLE_HIDDEN_UPLOAD_FIELD_ID = "uploadField";

    @Override
    public void buildView(WicketBuildContext ctx) {
        final FileListUploadPanel comp = new FileListUploadPanel("up-list", (IModel<SIList<SIAttachment>>) ctx.getModel(), ctx);
        ctx.getContainer().appendTag("div", comp);
        final WicketBuildContext.OnFieldUpdatedListener listener = new WicketBuildContext.OnFieldUpdatedListener();
        comp.add(new AjaxEventBehavior(SINGULAR_PROCESS_EVENT) {
            @Override
            protected void onEvent(AjaxRequestTarget target) {
                listener.onProcess(comp, target, ctx.getModel());
            }
        });
    }

}