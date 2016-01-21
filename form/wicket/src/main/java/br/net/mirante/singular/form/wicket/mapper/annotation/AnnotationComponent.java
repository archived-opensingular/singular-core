package br.net.mirante.singular.form.wicket.mapper.annotation;

import static br.net.mirante.singular.util.wicket.util.WicketUtils.*;

import br.net.mirante.singular.form.mform.basic.ui.AtrBasic;
import br.net.mirante.singular.form.mform.basic.view.MAnnotationView;
import br.net.mirante.singular.form.mform.core.annotation.AtrAnnotation;
import br.net.mirante.singular.form.mform.core.annotation.MIAnnotation;
import br.net.mirante.singular.form.wicket.model.AbstractMInstanciaModel;
import br.net.mirante.singular.form.wicket.model.MInstanciaValorModel;
import org.apache.commons.lang3.StringUtils;
import org.apache.wicket.markup.ComponentTag;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.CheckBox;
import org.apache.wicket.markup.html.form.TextArea;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.PropertyModel;

/**
 * This is the visual component of an annotated field on screen.
 * 
 * @author Fabricio Buzeto
 */
public class AnnotationComponent extends Panel {
    private MAnnotationView view;
    private final AbstractMInstanciaModel referenced;
    private final MIAnnotation target;

    public AnnotationComponent(String id, MAnnotationView view, AbstractMInstanciaModel referenced) {
        super(id);
        this.view = view;
        this.referenced = referenced;
        this.target = referenced.getMInstancia().as(AtrAnnotation::new).annotation();
        target.setTargetId(referenced.getMInstancia().getId());
    }

    @Override
    protected void onInitialize() {
        super.onInitialize();
        this.queue(new Label("target_label",$m.ofValue(title())));
        this.queue(new TextArea<>("comment_field", new PropertyModel(target, "text")));
        this.queue(new CheckBox("approval_field", new PropertyModel<Boolean>(target, "approved")));
    }

    private String title() {
        if(StringUtils.isNoneBlank(view.title())) return view.title();
        String label = labelOf(referenced);
        if(StringUtils.isNoneBlank(label))  return String.format("Comentários sobre %s", label);
        return "Comentários";
    }

    private static String labelOf(AbstractMInstanciaModel target) {
        return target.getMInstancia().as(AtrBasic::new).getLabel();
    }
}
