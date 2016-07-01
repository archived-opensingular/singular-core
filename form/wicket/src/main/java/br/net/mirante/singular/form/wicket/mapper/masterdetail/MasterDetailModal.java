package br.net.mirante.singular.form.wicket.mapper.masterdetail;

import br.net.mirante.singular.commons.lambda.IConsumer;
import br.net.mirante.singular.form.SIList;
import br.net.mirante.singular.form.SInstance;
import br.net.mirante.singular.form.view.SViewListByMasterDetail;
import br.net.mirante.singular.form.wicket.UIBuilderWicket;
import br.net.mirante.singular.form.wicket.WicketBuildContext;
import br.net.mirante.singular.form.wicket.component.BFModalWindow;
import br.net.mirante.singular.form.wicket.enums.ViewMode;
import br.net.mirante.singular.form.wicket.mapper.SingularEventsHandlers;
import br.net.mirante.singular.form.wicket.model.SInstanceItemListaModel;
import br.net.mirante.singular.form.wicket.util.FormStateUtil;
import br.net.mirante.singular.form.wicket.util.WicketFormProcessing;
import br.net.mirante.singular.util.wicket.ajax.ActionAjaxButton;
import br.net.mirante.singular.util.wicket.ajax.ActionAjaxLink;
import br.net.mirante.singular.util.wicket.bootstrap.layout.BSContainer;
import br.net.mirante.singular.util.wicket.modal.BSModalBorder;
import org.apache.wicket.Component;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.model.IModel;

import static br.net.mirante.singular.form.wicket.mapper.SingularEventsHandlers.FUNCTION.ADD_MOUSEDOWN_HANDLERS;
import static br.net.mirante.singular.util.wicket.util.Shortcuts.$m;

class MasterDetailModal extends BFModalWindow {

    protected final IModel<SIList<SInstance>>    listModel;
    protected final IModel<String>               listaLabel;
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


    MasterDetailModal(String id,
                      IModel<? extends SInstance> model,
                      IModel<String> listaLabel,
                      WicketBuildContext ctx,
                      ViewMode viewMode,
                      SViewListByMasterDetail view,
                      BSContainer<?> containerExterno) {
        super(id, true, false);

        this.wicketBuilder = ctx.getUiBuilderWicket();
        this.listaLabel = listaLabel;
        this.ctx = ctx;
        this.table = ctx.getContainer();
        this.viewMode = viewMode;
        this.view = view;
        this.listModel = $m.get(() -> (SIList<SInstance>) model.getObject());
        this.containerExterno = containerExterno;

        setSize(BSModalBorder.Size.NORMAL);

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
        closeCallback = this::revert;
        currentInstance = new SInstanceItemListaModel<>(listModel, listModel.getObject().indexOf(listModel.getObject().addNew()));
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
        listModel.getObject().remove(listModel.getObject().size() - 1);
    }

    private void configureNewContent(String prefix, AjaxRequestTarget target) {

        setTitleText($m.ofValue((prefix + " " + listaLabel.getObject()).trim()));

        final BSContainer<?> modalBody     = new BSContainer<>("bogoMips");
        ViewMode             viewModeModal = viewMode;

        setBody(modalBody);

        if (!view.isEditEnabled()) {
            viewModeModal = ViewMode.VISUALIZATION;
        }

        final WicketBuildContext context = new WicketBuildContext(ctx, modalBody, containerExterno, true, currentInstance);

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

}
