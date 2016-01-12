package br.net.mirante.singular.form.wicket.mapper.comment;

import static br.net.mirante.singular.util.wicket.util.WicketUtils.*;

import br.net.mirante.singular.form.mform.MInstancia;
import br.net.mirante.singular.form.mform.basic.ui.AtrBasic;
import br.net.mirante.singular.util.wicket.bootstrap.layout.BSContainer;
import org.apache.wicket.markup.ComponentTag;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.markup.html.link.ResourceLink;
import org.apache.wicket.markup.html.link.StatelessLink;
import org.apache.wicket.markup.html.panel.Fragment;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;

public class CommentComponent extends Panel {
    private final MInstancia referenced;

    public CommentComponent(String id, MInstancia referenced) {
        super(id);
        this.referenced = referenced;
    }

    @Override
    protected void onInitialize() {
        final String popoverId = "_popover_id_" + getId();
        super.onInitialize();
        WebMarkupContainer popover_modal = new WebMarkupContainer("popover_modal") {
            @Override
            protected void onInitialize() {
                super.onInitialize();
                this.queue(new Label("title",$m.ofValue(referenced.as(AtrBasic::new).getLabel())));
            }
        };
        this.queue(popover_modal);
        Link popover_link = new Link("comment_link") {
            public void onClick() {
            }

            @Override
            protected void onComponentTag(ComponentTag tag) {
                super.onComponentTag(tag);
                tag.put("data-popover-content", "#" + popover_modal.getMarkupId());
            }

            @Override
            protected CharSequence getURL() {
                return "#";
            }
        };
        this.queue(popover_link);
        this.add(new Label("_popover_id",$m.ofValue(popover_link.getMarkupId())));

    }
}
