package org.opensingular.form.wicket.mapper.tree;

import org.apache.commons.lang3.StringUtils;
import org.apache.wicket.model.Model;
import org.apache.wicket.util.lang.Objects;
import org.opensingular.form.view.SViewTree;
import org.opensingular.form.wicket.WicketBuildContext;
import org.opensingular.form.wicket.component.BFModalWindow;
import org.opensingular.form.wicket.mapper.search.AbstractSearchModalPanel;
import org.opensingular.lib.wicket.util.modal.BSModalBorder;

public class SearchModalTreePanel extends AbstractSearchModalPanel {

    private BFModalWindow modal;
    private SViewTree view;

    public SearchModalTreePanel(String id, WicketBuildContext ctx) {
        super(id, ctx);
        this.view = (SViewTree) ctx.getView();
    }

    @Override
    protected void buildAndAppendModalToRootContainer() {
        modal = new BFModalWindow(ctx.getRootContainer().newChildId(), false, false);
        modal.setTitleText(Model.of(Objects.defaultIfNull(view.getTitle(), StringUtils.EMPTY)));
        SearchModalBodyTreePanel searchModalBody = new SearchModalBodyTreePanel(SELECT_INPUT_MODAL_CONTENT_ID, ctx, this::accept);
        modal.setBody(searchModalBody).setSize(BSModalBorder.Size.valueOf(view.getModalSize()));
        ctx.getRootContainer().appendTag("div", modal);
    }

    @Override
    protected BFModalWindow getModal() {
        return modal;
    }
}
