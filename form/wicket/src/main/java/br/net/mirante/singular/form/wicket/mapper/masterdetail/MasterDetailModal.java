package br.net.mirante.singular.form.wicket.mapper.masterdetail;

import static br.net.mirante.singular.form.wicket.mapper.SingularEventsHandlers.FUNCTION.*;
import static br.net.mirante.singular.util.wicket.util.Shortcuts.*;

import org.apache.wicket.Component;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.model.IModel;

import br.net.mirante.singular.commons.lambda.IConsumer;
import org.opensingular.singular.form.SIList;
import org.opensingular.singular.form.SInstance;
import org.opensingular.singular.form.view.SViewListByMasterDetail;
import br.net.mirante.singular.form.wicket.UIBuilderWicket;
import br.net.mirante.singular.form.wicket.WicketBuildContext;
import br.net.mirante.singular.form.wicket.component.BFModalWindow;
import br.net.mirante.singular.form.wicket.enums.ViewMode;
import br.net.mirante.singular.form.wicket.mapper.SingularEventsHandlers;
import br.net.mirante.singular.form.wicket.model.SInstanceListItemModel;
import br.net.mirante.singular.form.wicket.util.FormStateUtil;
import br.net.mirante.singular.form.wicket.util.WicketFormProcessing;
import br.net.mirante.singular.util.wicket.ajax.ActionAjaxButton;
import br.net.mirante.singular.util.wicket.ajax.ActionAjaxLink;
import br.net.mirante.singular.util.wicket.bootstrap.layout.BSContainer;
import br.net.mirante.singular.util.wicket.modal.BSModalBorder;

class MasterDetailModal extends BFModalWindow {

    protected final IModel<String>         listaLabel;
    protected final WicketBuildContext     ctx;
    protected final UIBuilderWicket        wicketBuilder;
    protected final Component              table;
    protected final ViewMode               viewMode;
    protected IModel<SInstance>            currentInstance;
    protected IConsumer<AjaxRequestTarget> closeCallback;
    protected SViewListByMasterDetail      view;
    protected BSContainer<?>               containerExterno;
    protected FormStateUtil.FormState      formState;
    protected IModel<String>               actionLabel;
    protected ActionAjaxButton             addButton;
    private IConsumer<AjaxRequestTarget>   onHideCallback;

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
                add(new SingularEventsHandlers(ADD_MOUSEDOWN_HANDLERS));
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
                    add(new SingularEventsHandlers(ADD_MOUSEDOWN_HANDLERS));
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
        target.appendJavaScript(getConfigureBackdropScript());
    }
    @Override
    public void hide(AjaxRequestTarget target) {
        super.hide(target);
        if (onHideCallback != null)
            onHideCallback.accept(target);
    }

    private String getConfigureBackdropScript() {
        String js = "";
        js += " (function (zindex){ ";
        js += "     $('.modal-backdrop').each(function(index) { ";
        js += "         var zIndex = $(this).css('z-index'); ";
        js += "         $(this).css('z-index', zindex-1+index); ";
        js += "     }); ";
        js += "     $('.modal').each(function(index) { ";
        js += "         var zIndex = $(this).css('z-index'); ";
        js += "         $(this).css('z-index', zindex+index); ";
        js += "     }); ";
        js += " })(10050); ";
        return js;
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
}
