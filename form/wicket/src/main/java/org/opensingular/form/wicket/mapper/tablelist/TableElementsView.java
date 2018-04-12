package org.opensingular.form.wicket.mapper.tablelist;

import org.apache.wicket.MarkupContainer;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.repeater.Item;
import org.apache.wicket.model.IModel;
import org.opensingular.form.SIComposite;
import org.opensingular.form.SIList;
import org.opensingular.form.SInstance;
import org.opensingular.form.SType;
import org.opensingular.form.STypeComposite;
import org.opensingular.form.view.AbstractSViewListWithControls;
import org.opensingular.form.view.SView;
import org.opensingular.form.view.SViewListByTable;
import org.opensingular.form.wicket.ISValidationFeedbackHandlerListener;
import org.opensingular.form.wicket.SValidationFeedbackHandler;
import org.opensingular.form.wicket.WicketBuildContext;
import org.opensingular.form.wicket.feedback.FeedbackFence;
import org.opensingular.form.wicket.mapper.buttons.ElementsView;
import org.opensingular.form.wicket.mapper.buttons.RemoverButton;
import org.opensingular.form.wicket.mapper.components.ConfirmationModal;
import org.opensingular.form.wicket.model.SInstanceFieldModel;
import org.opensingular.lib.wicket.util.bootstrap.layout.BSContainer;
import org.opensingular.lib.wicket.util.bootstrap.layout.IBSGridCol;
import org.opensingular.lib.wicket.util.bootstrap.layout.table.BSTDataCell;
import org.opensingular.lib.wicket.util.bootstrap.layout.table.BSTRow;

import static org.opensingular.form.wicket.IWicketComponentMapper.HIDE_LABEL;
import static org.opensingular.lib.wicket.util.util.Shortcuts.$b;
import static org.opensingular.lib.wicket.util.util.Shortcuts.$m;

public class TableElementsView extends ElementsView {

    private final WicketBuildContext ctx;
    private final SView view;
    private final Form<?> form;
    private final ConfirmationModal confirmationModal;

    public TableElementsView(String id, IModel<SIList<SInstance>> model, WicketBuildContext ctx, Form<?> form,
            WebMarkupContainer parentContainer, ConfirmationModal confirmationModal) {
        super(id, model, parentContainer);
        this.confirmationModal = confirmationModal;
        super.setRenderedChildFunction(c -> ((MarkupContainer) c).get("_r"));
        this.ctx = ctx;
        this.view = ctx.getView();
        this.form = form;
    }

    @Override
    protected void populateItem(Item<SInstance> item) {

        final BSTRow row       = new BSTRow("_r", IBSGridCol.BSGridSize.MD);
        final IModel<SInstance> itemModel = item.getModel();
        final SInstance         instance  = itemModel.getObject();

        SValidationFeedbackHandler feedbackHandler = SValidationFeedbackHandler.bindTo(new FeedbackFence(row))
                .addInstanceModel(itemModel)
                .addListener(ISValidationFeedbackHandlerListener.withTarget(t -> t.add(row)));

        row.setDefaultModel(itemModel);
        row.add($b.classAppender("singular-form-table-row can-have-error"));
        row.add($b.classAppender("has-errors", $m.ofValue(feedbackHandler).map(SValidationFeedbackHandler::containsNestedErrors)));

        if (!(view instanceof SViewListByTable)) {
            return;
        }

        final SViewListByTable viewListByTable = (SViewListByTable) view;

        if (viewListByTable.isInsertEnabled() && ctx.getViewMode().isEdition()) {
            final BSTDataCell actionColumn = row.newCol();
            actionColumn.add($b.attrAppender("style", "width:20px", ";"));
            appendInserirButton(this, form, item, actionColumn);
        }

        if ((instance instanceof SIComposite) && viewListByTable.isRenderCompositeFieldsAsColumns()) {
            final SIComposite       ci = (SIComposite) instance;
            final STypeComposite<?> ct = ci.getType();

            for (SType<?> ft : ct.getFields()) {
                IModel<SInstance> fm = new SInstanceFieldModel<>(item.getModel(), ft.getNameSimple());
                ctx.createChild(row.newCol(), ctx.getExternalContainer(), fm).setHint(HIDE_LABEL, Boolean.TRUE).build();
            }
        } else {
            ctx.createChild(row.newCol(), ctx.getExternalContainer(), itemModel).setHint(HIDE_LABEL, Boolean.FALSE).build();
        }

        if (ctx.getViewMode().isEdition()) {
            final BSTDataCell actionColumn = row.newCol();
            actionColumn.add($b.attrAppender("style", "width:20px", ";"));
            appendRemoverButton(this, form, item, actionColumn, confirmationModal, viewListByTable);
        }

        item.add(row);

    }

    protected RemoverButton appendRemoverButton(ElementsView elementsView, Form<?> form, Item<SInstance> item,
            BSContainer<?> cell, ConfirmationModal confirmationModal, AbstractSViewListWithControls viewListByTable) {
        return new RemoverButton("_remover_", form, elementsView,item,confirmationModal){
            @Override
            protected void behaviorAfterRemoveItem(AjaxRequestTarget target) {
                behaviorAfterRemoveButton(target);
            }
        }.createRemoverButton(cell, viewListByTable);
    }

    protected void behaviorAfterRemoveButton(AjaxRequestTarget target) {}

}
