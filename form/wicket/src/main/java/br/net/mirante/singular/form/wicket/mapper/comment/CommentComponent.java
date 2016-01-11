package br.net.mirante.singular.form.wicket.mapper.comment;

import static br.net.mirante.singular.util.wicket.util.WicketUtils.*;

import br.net.mirante.singular.form.mform.MInstancia;
import br.net.mirante.singular.form.mform.basic.ui.AtrBasic;
import br.net.mirante.singular.util.wicket.bootstrap.layout.BSContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;

public class CommentComponent extends Panel {
    public CommentComponent(String id, MInstancia referenced) {
        super(id);
//        this.add(new BSContainer("_popover_div"));
        this.add(new Label("_popover_id",$m.ofValue("_popover_id")));
        this.add(new Label("title",$m.ofValue(referenced.as(AtrBasic::new).getLabel())));
    }
}
