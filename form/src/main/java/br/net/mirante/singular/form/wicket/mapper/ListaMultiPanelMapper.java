package br.net.mirante.singular.form.wicket.mapper;

import org.apache.commons.lang3.StringUtils;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.model.IModel;

import br.net.mirante.singular.form.mform.MInstancia;
import br.net.mirante.singular.form.mform.basic.ui.AtrBasic;
import br.net.mirante.singular.form.wicket.WicketBuildContext;
import br.net.mirante.singular.util.wicket.bootstrap.layout.BSCol;
import br.net.mirante.singular.util.wicket.bootstrap.layout.BSContainer;
import br.net.mirante.singular.util.wicket.bootstrap.layout.BSGrid;
import br.net.mirante.singular.util.wicket.bootstrap.layout.IBSComponentFactory;

public class ListaMultiPanelMapper extends AbstractListaMapper {
    public ListaMultiPanelMapper() {
        super(
            ListaMultiPanelMapper::newElementCol,
            null,
            null);
    }
    private static BSCol newElementCol(WicketBuildContext parentCtx, BSGrid grid, IModel<MInstancia> itemModel, int index) {
        BSContainer<?> panel = grid.newColInRow()
            .newTag("div", true, "class='panel panel-default'",
                (IBSComponentFactory<BSContainer<?>>) id -> new BSContainer<>(id));

        MInstancia iItem = itemModel.getObject();
        String label = iItem.as(AtrBasic::new).getLabel();
        if (StringUtils.isNotBlank(label)) {
            panel.newTag("div", true, "class='panel-heading'",
                (IBSComponentFactory<Label>) id -> new Label(id, label));
        }
        BSGrid panelBody = panel.newTag("div", true, "class='panel-body'",
            (IBSComponentFactory<BSGrid>) BSGrid::new);
        return panelBody.newColInRow();
    }
}