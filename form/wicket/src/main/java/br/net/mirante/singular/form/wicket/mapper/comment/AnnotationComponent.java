package br.net.mirante.singular.form.wicket.mapper.comment;

import static br.net.mirante.singular.util.wicket.util.WicketUtils.*;

import br.net.mirante.singular.form.mform.MInstancia;
import br.net.mirante.singular.form.mform.basic.ui.AtrBasic;
import br.net.mirante.singular.form.wicket.model.MInstanciaCampoModel;
import br.net.mirante.singular.util.wicket.bootstrap.layout.BSContainer;
import org.apache.wicket.markup.ComponentTag;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.TextArea;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.markup.html.link.ResourceLink;
import org.apache.wicket.markup.html.link.StatelessLink;
import org.apache.wicket.markup.html.panel.Fragment;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;

public class AnnotationComponent extends Panel {
    private final MInstancia referenced;
    private MInstancia target;

    public AnnotationComponent(String id, MInstancia referenced, MInstancia target) {
        super(id);
        this.referenced = referenced;
        this.target = target;
    }

    @Override
    protected void onInitialize() {
        final String popoverId = "_popover_id_" + getId();
        super.onInitialize();
        WebMarkupContainer popover_modal = new WebMarkupContainer("popover_modal") {
            @Override
            protected void onInitialize() {
                super.onInitialize();
                this.queue(new Label("target_label",$m.ofValue(referenced.as(AtrBasic::new).getLabel())));
                this.queue(new TextArea<>("comment_field",$m.ofValue()));
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
