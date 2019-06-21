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

package org.opensingular.form.wicket.mapper.composite;

import de.alpharogroup.wicket.js.addon.toastr.ToastrType;
import org.apache.wicket.Component;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.model.IModel;
import org.opensingular.form.SIComposite;
import org.opensingular.form.SIList;
import org.opensingular.form.SInstance;
import org.opensingular.form.view.SViewCompositeModal;
import org.opensingular.form.wicket.WicketBuildContext;
import org.opensingular.form.wicket.component.BFModalWindow;
import org.opensingular.form.wicket.enums.ViewMode;
import org.opensingular.form.wicket.util.FormStateUtil;
import org.opensingular.form.wicket.util.WicketFormProcessing;
import org.opensingular.lib.commons.base.SingularException;
import org.opensingular.lib.commons.lambda.IConsumer;
import org.opensingular.lib.commons.lambda.ISupplier;
import org.opensingular.lib.wicket.util.ajax.ActionAjaxButton;
import org.opensingular.lib.wicket.util.ajax.ActionAjaxLink;
import org.opensingular.lib.wicket.util.bootstrap.layout.BSContainer;
import org.opensingular.lib.wicket.util.modal.BSModalBorder;
import org.opensingular.lib.wicket.util.scripts.Scripts;
import org.opensingular.lib.wicket.util.toastr.ToastrHelper;

import javax.annotation.Nullable;

import static org.opensingular.lib.wicket.util.util.Shortcuts.$b;
import static org.opensingular.lib.wicket.util.util.Shortcuts.$m;

public class CompositeModal extends BFModalWindow {

    protected final IModel<String>                 listLabel;
    protected final WicketBuildContext             ctx;
    protected final Component                      table;
    protected final ViewMode                       viewMode;
    protected final ISupplier<SViewCompositeModal> viewSupplier;

    protected IModel<SIComposite>          currentInstance;
    protected IConsumer<AjaxRequestTarget> closeCallback;
    protected BSContainer<?>               containerExterno;
    protected FormStateUtil.FormState      formState;
    protected IModel<String>               actionLabel;
    public    ActionAjaxButton             addButton;
    private   IConsumer<AjaxRequestTarget> onHideCallback;

    public CompositeModal(String id,
                          IModel<? extends SInstance> model,
                          IModel<String> listLabel,
                          WicketBuildContext ctx,
                          ViewMode viewMode,
                          BSContainer<?> containerExterno) {
        super(id, model, true, false);

        this.listLabel = listLabel;
        this.ctx = ctx;
        this.table = ctx.getContainer();
        this.viewMode = viewMode;
        this.containerExterno = containerExterno;
        this.viewSupplier = ctx.getViewSupplier(SViewCompositeModal.class);

        setSize(BSModalBorder.Size.valueOf(getView().getModalSize()));

        actionLabel = $m.ofValue("");
        addButton = new ActionAjaxButton("btn") {
            @Override
            protected void onAction(AjaxRequestTarget target, Form<?> form) {
                boolean mustHide        = true;
                boolean mustProcessForm = viewMode.isEdition();
                if (mustProcessForm && getView().isEnforceValidationOnAdd()) {
                    boolean invalid = WicketFormProcessing.validateErrors(CompositeModal.this.getBodyContainer(), target, currentInstance.getObject(), false);
                    mustHide = !invalid;
                    mustProcessForm = !invalid;
                    if (invalid && getView().getEnforcedValidationMessage() != null) {
                        new ToastrHelper(CompositeModal.this.getBodyContainer()).addToastrMessage(ToastrType.ERROR, getView().getEnforcedValidationMessage());
                    }
                }
                if (mustProcessForm) {
                    WicketFormProcessing.processDependentTypes(this.getPage(), target, model.getObject());
                    if (getView().isValidateAllLineOnConfirmAndCancel()) {
                        WicketFormProcessing.onFormSubmit((WebMarkupContainer) table, target, model, true);
                    } else {
                        WicketFormProcessing.onFormSubmit((WebMarkupContainer) table, target, currentInstance, true);
                    }
                }
                if (mustHide) {
                    CompositeModal.this.hide(target);
                    target.add(table);
                }
            }
        };

        addButton.add($b.visibleIf(() -> currentInstance.getObject().asAtr().isEnabled()));
        this.addButton(BSModalBorder.ButtonStyle.EMPTY, actionLabel, addButton);

        if (viewMode.isEdition()) {
            this.addLink(BSModalBorder.ButtonStyle.CANCEL, $m.ofValue("Cancelar"), new ActionAjaxLink<Void>("btn-cancelar") {
                @Override
                protected void onAction(AjaxRequestTarget target) {
                    rollbackTheInstance(target);
                    if (getView().isValidateAllLineOnConfirmAndCancel()) {
                        WicketFormProcessing.validateErrors(this.getParent(), target, model.getObject(), false);
                    }
                }
            });
        }

        getModalBorder().setCloseIconCallback(this::rollbackTheInstance);

    }

    public SViewCompositeModal getView() {
        return viewSupplier.get();
    }

    /**
     * Method responsible for remove the new Instance, or rollback to the old Instance.
     * It is used by cancel and  close button.
     *
     * @param target The target to close the modal.
     */
    private void rollbackTheInstance(AjaxRequestTarget target) {
        if (closeCallback != null) {
            closeCallback.accept(target);
        }
        rollbackState();
        target.add(table);
        CompositeModal.this.hide(target);
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
            throw SingularException.rethrow(e.getMessage(), e);
        }
    }

    /**
     * Method for show the modal.
     *
     * @param target   The ajaxTarget.
     * @param forEdit  The instance of the modal.
     * @param ctx      The context.
     * @param viewMode The viewMode, this is useful for force READ_ONLY case.
     */
    void showExisting(AjaxRequestTarget target, IModel<SIComposite> forEdit, WicketBuildContext ctx, @Nullable ViewMode viewMode) {
        closeCallback = null;
        currentInstance = forEdit;
        String prefix;
        if (ctx.getViewMode().isEdition()) {
            prefix = getView().getEditActionLabel();
            actionLabel.setObject(prefix);
        } else {
            prefix = "";
            actionLabel.setObject("Fechar");
        }
        saveState();
        configureNewContent(prefix, target, viewMode);
    }

    private void revert() {
        SIList<SInstance> list = getModelObject();
        list.remove(list.size() - 1);
    }

    /**
     * @param prefix
     * @param target
     * @param viewModeReadOnly The viewMode, this is useful for force READ_ONLY case.
     *                         If it's null, it will use a rule of view to get the viewMode.
     */
    private void configureNewContent(String prefix, AjaxRequestTarget target, @Nullable ViewMode viewModeReadOnly) {

        setTitleText($m.get(() -> (prefix + " " + listLabel.getObject()).trim()));

        BSContainer<?> modalBody     = new BSContainer<>("bogoMips");
        ViewMode       viewModeModal = viewMode;

        setBody(modalBody);

        boolean isEnabled = !getView().isEditEnabled() || ViewMode.READ_ONLY == viewModeReadOnly;
        if (isEnabled) {
            viewModeModal = ViewMode.READ_ONLY;
        }

        WicketBuildContext context = buildModalContent(modalBody, viewModeModal);

        WicketFormProcessing.onFormPrepare(modalBody, currentInstance, false);

        context.initContainerBehavior();

        target.add(ctx.getExternalContainer().setOutputMarkupId(true));
        target.add(containerExterno.setOutputMarkupId(true));
    }

    protected WicketBuildContext buildModalContent(BSContainer<?> modalBody, ViewMode viewModeModal) {
        WicketBuildContext context = ctx.createChild(modalBody, containerExterno, currentInstance);

        context.build(viewModeModal);
        return context;
    }


    @Override
    public void show(AjaxRequestTarget target) {
        showExisting(target, getModel(), ctx, viewMode);
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
    public IModel<SIComposite> getModel() {
        return (IModel<SIComposite>) super.getDefaultModel();
    }

    @SuppressWarnings("unchecked")
    public SIList<SInstance> getModelObject() {
        return (SIList<SInstance>) super.getDefaultModelObject();
    }

    public CompositeModal setOnHideCallback(IConsumer<AjaxRequestTarget> onHideCallback) {
        this.onHideCallback = onHideCallback;
        return this;
    }

    @Override
    public boolean isWithAutoFocus() {
        return viewMode == null || viewMode.isEdition();
    }

}