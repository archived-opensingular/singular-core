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
import org.apache.wicket.model.IModel;
import org.apache.wicket.request.cycle.RequestCycle;
import org.opensingular.form.SIList;
import org.opensingular.form.SInstance;
import org.opensingular.form.decorator.action.ISInstanceActionCapable;
import org.opensingular.form.decorator.action.ISInstanceActionsProvider;
import org.opensingular.form.type.core.attachment.SIAttachment;
import org.opensingular.form.view.FileEventListener;
import org.opensingular.form.view.SViewAttachmentList;
import org.opensingular.form.wicket.WicketBuildContext;
import org.opensingular.form.wicket.mapper.AbstractListMapper;
import org.opensingular.form.wicket.mapper.decorator.SInstanceActionsPanel;
import org.opensingular.form.wicket.mapper.decorator.SInstanceActionsProviders;
import org.opensingular.form.wicket.model.ReadOnlyCurrentInstanceModel;
import org.opensingular.lib.commons.lambda.IFunction;
import org.opensingular.lib.wicket.util.bootstrap.layout.BSContainer;

import java.util.Arrays;
import java.util.List;

import static org.opensingular.form.wicket.AjaxUpdateListenersFactory.SINGULAR_PROCESS_EVENT;

public class AttachmentListMapper extends AbstractListMapper implements ISInstanceActionCapable {

    private SInstanceActionsProviders instanceActionsProviders = new SInstanceActionsProviders(this);
    private WicketBuildContext ctx;

    public final static String MULTIPLE_HIDDEN_UPLOAD_FIELD_ID = "up-list";

    @Override
    public void buildView(WicketBuildContext ctx) {
        this.ctx = ctx;
        final FileUploadListPanel comp = new FileUploadListPanel(MULTIPLE_HIDDEN_UPLOAD_FIELD_ID, (IModel<SIList<SIAttachment>>) ctx.getModel(), ctx, this::addSInstanceActions);
        registerListeners(ctx, comp);
        ctx.getContainer().appendTag("div", comp);
        final WicketBuildContext.OnFieldUpdatedListener listener = new WicketBuildContext.OnFieldUpdatedListener();
        comp.add(new AjaxEventBehavior(SINGULAR_PROCESS_EVENT) {
            @Override
            protected void onEvent(AjaxRequestTarget target) {
                listener.onProcess(comp, target, ctx.getModel());
            }
        });
    }

    private void registerListeners(WicketBuildContext ctx, FileUploadListPanel container) {
        if (ctx.getView() instanceof SViewAttachmentList) {
            SViewAttachmentList viewAttachment = (SViewAttachmentList) ctx.getView();
            for (FileEventListener uploadListener : viewAttachment.getFileUploadedListeners()) {
                container.registerFileUploadedListener(uploadListener);
            }
            for (FileEventListener removedListener : viewAttachment.getFileRemovedListeners()) {
                container.registerFileRemovedListener(removedListener);
            }
        }
    }

    private void addSInstanceActions(BSContainer<?> container) {
        final IModel<SIList<SInstance>> model = new ReadOnlyCurrentInstanceModel<>(ctx);
        IFunction<AjaxRequestTarget, List<?>> internalContextListProvider = target -> Arrays.asList(
                this,
                RequestCycle.get().find(AjaxRequestTarget.class),
                model,
                model.getObject(),
                ctx,
                ctx.getContainer());

        SInstanceActionsPanel.addPrimarySecondaryPanelsTo(
                container,
                this.instanceActionsProviders,
                model,
                false,
                internalContextListProvider, ctx.getActionClassifier());
    }

    @Override
    public void addSInstanceActionsProvider(int sortPosition, ISInstanceActionsProvider provider) {
        this.instanceActionsProviders.addSInstanceActionsProvider(sortPosition, provider);
    }
}