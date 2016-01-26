package br.net.mirante.singular.form.wicket.mapper.annotation;

import static br.net.mirante.singular.util.wicket.util.WicketUtils.*;

import br.net.mirante.singular.form.mform.basic.ui.AtrBasic;
import br.net.mirante.singular.form.mform.core.annotation.AtrAnnotation;
        import br.net.mirante.singular.form.mform.core.annotation.MIAnnotation;
        import br.net.mirante.singular.form.wicket.model.AbstractMInstanciaModel;
import br.net.mirante.singular.form.wicket.model.MInstanceRootModel;
import br.net.mirante.singular.form.wicket.model.MInstanciaCampoModel;
import br.net.mirante.singular.form.wicket.model.MInstanciaValorModel;
import br.net.mirante.singular.util.wicket.util.WicketUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.wicket.Component;
import org.apache.wicket.markup.ComponentTag;
import org.apache.wicket.markup.head.HeaderItem;
import org.apache.wicket.markup.head.IHeaderResponse;
import org.apache.wicket.markup.head.JavaScriptReferenceHeaderItem;
import org.apache.wicket.markup.head.OnDomReadyHeaderItem;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.CheckBox;
import org.apache.wicket.markup.html.form.TextArea;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.request.resource.PackageResourceReference;

/**
 * This is the visual component of an annotated field on screen.
 *
 * @author Fabricio Buzeto
 */
public class AnnotationComponent extends Panel {
    private final AbstractMInstanciaModel referenced;
    private final Component referencedComponent;

    public AnnotationComponent(String id, AbstractMInstanciaModel referenced, Component referencedComponent) {
        super(id);
        this.referenced = referenced;
        this.referencedComponent = referencedComponent;

    }

    @Override
    protected void onInitialize() {
        super.onInitialize();
        MIAnnotation target = referenced.getMInstancia().as(AtrAnnotation::new).annotation();
        target.setTargetId(referenced.getMInstancia().getId());

        this.queue(new Label("target_label",$m.ofValue(title())));
        this.queue(new TextArea<>("comment_field", new MInstanciaValorModel(new MInstanceRootModel<>(target.getCampo("text")))));
        this.queue(new CheckBox("approval_field", new MInstanciaValorModel(new MInstanceRootModel<>(target.getCampo("isApproved")))));
        this.add(new Label("referenced_id",$m.ofValue(referencedComponent.getMarkupId())));
        this.add(new Label("this_id",$m.ofValue(this.getMarkupId())));
    }

    private String title() {
        if(StringUtils.isNoneBlank(annotated().label())) return annotated().label();
        String label = labelOf(referenced);
        if(StringUtils.isNoneBlank(label))  return String.format("Comentários sobre %s", label);
        return "Comentários";
    }

    private AtrAnnotation annotated() {
        return referenced.getMInstancia().as(AtrAnnotation::new);
    }

    private static String labelOf(AbstractMInstanciaModel target) {
        return target.getMInstancia().as(AtrBasic::new).getLabel();
    }

    @Override
    public void renderHead(IHeaderResponse response) {
        super.renderHead(response);
        final PackageResourceReference customJS = new PackageResourceReference(getClass(), "annotation.js");
        response.render(JavaScriptReferenceHeaderItem.forReference(customJS));
    }

    protected void onConfigure() {
        super.onConfigure();
        this.add(WicketUtils.$b.attrAppender("style", "display: none;", ""));
        this.add(WicketUtils.$b.attrAppender("class", "portlet box border-default", ""));
    }
}
