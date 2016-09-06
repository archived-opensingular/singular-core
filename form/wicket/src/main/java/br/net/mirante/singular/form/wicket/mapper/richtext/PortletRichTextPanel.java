package br.net.mirante.singular.form.wicket.mapper.richtext;

import br.net.mirante.singular.commons.lambda.IFunction;
import br.net.mirante.singular.form.wicket.WicketBuildContext;
import br.net.mirante.singular.form.wicket.model.SInstanceValueModel;
import br.net.mirante.singular.util.wicket.jquery.JQuery;
import br.net.mirante.singular.util.wicket.util.WicketUtils;
import org.apache.wicket.Component;
import org.apache.wicket.markup.head.IHeaderResponse;
import org.apache.wicket.markup.head.JavaScriptHeaderItem;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.HiddenField;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.Model;
import org.apache.wicket.util.template.PackageTextTemplate;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import static org.apache.commons.lang3.StringUtils.EMPTY;

public class PortletRichTextPanel extends Panel {

    private HiddenField        hiddenInput;
    private WebMarkupContainer htmlContent;
    private Label              label;


    @Override
    public void renderHead(IHeaderResponse response) {
        super.renderHead(response);
        final PackageTextTemplate packageTextTemplate = new PackageTextTemplate(getClass(), "PortletRichTextPanel.js");
        final Map<String, String> params              = new HashMap<>();
        params.put("label", (String) label.getDefaultModel().getObject());
        params.put("htmlContainer", htmlContent.getMarkupId());
        params.put("hiddenInput", hiddenInput.getMarkupId());
        packageTextTemplate.interpolate(params);
        response.render(JavaScriptHeaderItem.forScript(packageTextTemplate.getString(), "PortletRichTextPanel"));
    }

    public PortletRichTextPanel(String id, WicketBuildContext ctx) {
        super(id);
        build(ctx);
        addBehaviours();
    }

    private void addBehaviours() {
        add(WicketUtils.$b.onReadyScript((IFunction<Component, CharSequence>) component -> {
            return JQuery.$(htmlContent).append(".html(").append(JQuery.$(hiddenInput).append(".val())"));
        }));
    }

    private void build(WicketBuildContext ctx) {
        add(label = new Label("label", Model.of(Optional.ofNullable(ctx.getCurrentInstance().asAtr().getLabel()).orElse(EMPTY))));
        add(htmlContent = new WebMarkupContainer("htmlContent"));
        add(hiddenInput = new HiddenField<>("hiddenInput", new SInstanceValueModel<>(ctx.getModel())));
    }

}