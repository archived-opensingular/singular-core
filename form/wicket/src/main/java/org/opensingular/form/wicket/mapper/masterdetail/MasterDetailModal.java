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

package org.opensingular.form.wicket.mapper.masterdetail;

import static org.opensingular.lib.wicket.util.util.Shortcuts.*;

import org.apache.wicket.Component;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.model.IModel;

import org.opensingular.form.wicket.WicketBuildContext;
import org.opensingular.form.wicket.component.BFModalWindow;
import org.opensingular.form.wicket.enums.ViewMode;
import org.opensingular.form.wicket.mapper.SingularEventsHandlers;
import org.opensingular.form.wicket.model.SInstanceListItemModel;
import org.opensingular.form.wicket.util.FormStateUtil;
import org.opensingular.form.wicket.util.WicketFormProcessing;
import org.opensingular.lib.commons.lambda.IConsumer;
import org.opensingular.form.SIList;
import org.opensingular.form.SInstance;
import org.opensingular.form.view.SViewListByMasterDetail;
import org.opensingular.form.wicket.UIBuilderWicket;
import org.opensingular.lib.wicket.util.ajax.ActionAjaxButton;
import org.opensingular.lib.wicket.util.ajax.ActionAjaxLink;
import org.opensingular.lib.wicket.util.bootstrap.layout.BSContainer;
import org.opensingular.lib.wicket.util.modal.BSModalBorder;
import org.opensingular.lib.wicket.util.scripts.Scripts;

class MasterDetailModal extends BFModalWindow {

    protected final IModel<String>         listaLabel;
    protected final WicketBuildContext           ctx;
    protected final UIBuilderWicket              wicketBuilder;
    protected final Component                    table;
    protected final ViewMode                     viewMode;
    protected       IModel<SInstance>            currentInstance;
    protected       IConsumer<AjaxRequestTarget> closeCallback;
    protected       SViewListByMasterDetail      view;
    protected       BSContainer<?>               containerExterno;
    protected       FormStateUtil.FormState      formState;
    protected       IModel<String>               actionLabel;
    protected       ActionAjaxButton             addButton;
    private         IConsumer<AjaxRequestTarget> onHideCallback;

    MasterDetailModal(String id,
                      IModel<SIList<SInstance>> model,
                      IModel<String> listaLabel,
                      WicketBuildContext ctx,
                      ViewMode viewMode,
                      SViewListByMasterDetail view,
                      BSContainer<?> containerExterno) {
        super(id, model, true, false);

        this.wicketBuilder = ctx.getUiBuilderWicket();
        this.listaLabel = listaLabel;
        this.ctx = ctx;
        this.table = ctx.getContainer();
        this.viewMode = viewMode;
        this.view = view;
        this.containerExterno = containerExterno;

        setSize(BSModalBorder.Size.valueOf(view.getModalSize()));

        actionLabel = $m.ofValue("");
        this.addButton(BSModalBorder.ButtonStyle.EMPTY, actionLabel, addButton = new ActionAjaxButton("btn") {
            @Override
            protected void onAction(AjaxRequestTarget target, Form<?> form) {
                target.add(table);
                MasterDetailModal.this.hide(target);
            }

            @Override
            protected void onInitialize() {
                super.onInitialize();
                add(new SingularEventsHandlers(SingularEventsHandlers.FUNCTION.ADD_MOUSEDOWN_HANDLERS));
            }
        });

        if (viewMode.isEdition()) {
            this.addLink(BSModalBorder.ButtonStyle.CANCEl, $m.ofValue("Cancelar"), new ActionAjaxLink<Void>("btn-cancelar") {
                @Override
                protected void onAction(AjaxRequestTarget target) {
                    if (closeCallback != null) {
                        closeCallback.accept(target);
                    }
                    rollbackState();
                    target.add(table);
                    MasterDetailModal.this.hide(target);
                }

                @Override
                protected void onInitialize() {
                    super.onInitialize();
                    add(new SingularEventsHandlers(SingularEventsHandlers.FUNCTION.ADD_MOUSEDOWN_HANDLERS));
                }
            });
        }

    }

    private void saveState() {
        formState = FormStateUtil.keepState(currentInstance.getObject());
    }

    private void rollbackState() {
        try {
            if (formState != null && currentInstance.getObject() != null) {
                FormStateUtil.restoreState(currentInstance.getObject(), formState);
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    void showNew(AjaxRequestTarget target) {
        SIList<SInstance> list = getModelObject();
        closeCallback = this::revert;
        currentInstance = new SInstanceListItemModel<>(getModel(), list.indexOf(list.addNew()));
        actionLabel.setObject(view.getNewActionLabel());
        MasterDetailModal.this.configureNewContent(actionLabel.getObject(), target);
    }

    void showExisting(AjaxRequestTarget target, IModel<SInstance> forEdit, WicketBuildContext ctx) {
        closeCallback = null;
        currentInstance = forEdit;
        String prefix;
        if (ctx.getViewMode().isEdition()) {
            prefix = view.getEditActionLabel();
            actionLabel.setObject(prefix);
        } else {
            prefix = "";
            actionLabel.setObject("Fechar");
        }
        saveState();
        configureNewContent(prefix, target);
    }

    private void revert(AjaxRequestTarget target) {
        SIList<SInstance> list = getModelObject();
        list.remove(list.size() - 1);
    }

    private void configureNewContent(String prefix, AjaxRequestTarget target) {

        setTitleText($m.ofValue((prefix + " " + listaLabel.getObject()).trim()));

        final BSContainer<?> modalBody = new BSContainer<>("bogoMips");
        ViewMode viewModeModal = viewMode;

        setBody(modalBody);

        if (!view.isEditEnabled()) {
            viewModeModal = ViewMode.READ_ONLY;
        }

        final WicketBuildContext context = ctx.createChild(modalBody, containerExterno, true, currentInstance);

        wicketBuilder.build(context, viewModeModal);

        WicketFormProcessing.onFormPrepare(modalBody, currentInstance, false);

        context.initContainerBehavior();

        target.add(ctx.getExternalContainer().setOutputMarkupId(true));
        target.add(containerExterno.setOutputMarkupId(true));

        show(target);
    }

    @Override
    public void show(AjaxRequestTarget target) {
        super.show(target);
        target.appendJavaScript(Scripts.multipleModalBackDrop());
    }
    @Override
    public void hide(AjaxRequestTarget target) {
        super.hide(target);
        if (onHideCallback != null)
            onHideCallback.accept(target);
    }


    @SuppressWarnings("unchecked")
    public IModel<SIList<SInstance>> getModel() {
        return (IModel<SIList<SInstance>>) super.getDefaultModel();
    }
    @SuppressWarnings("unchecked")
    public SIList<SInstance> getModelObject() {
        return (SIList<SInstance>) super.getDefaultModelObject();
    }
    public MasterDetailModal setOnHideCallback(IConsumer<AjaxRequestTarget> onHideCallback) {
        this.onHideCallback = onHideCallback;
        return this;
    }

    @Override
    public boolean isWithAutoFocus() {
        return viewMode == null || viewMode.isEdition();
    }

}