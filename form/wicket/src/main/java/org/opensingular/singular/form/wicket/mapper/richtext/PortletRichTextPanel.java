package org.opensingular.singular.form.wicket.mapper.richtext;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.wicket.markup.head.IHeaderResponse;
import org.apache.wicket.markup.head.JavaScriptHeaderItem;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Button;
import org.apache.wicket.markup.html.form.HiddenField;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.Model;
import org.apache.wicket.util.template.PackageTextTemplate;

import static org.opensingular.singular.util.wicket.jquery.JQuery.$;
import static org.opensingular.singular.util.wicket.util.WicketUtils.$b;
import static org.apache.commons.lang3.StringUtils.EMPTY;

import org.opensingular.singular.commons.util.Loggable;
import org.opensingular.singular.form.SingularFormException;
import org.opensingular.singular.form.wicket.WicketBuildContext;
import org.opensingular.singular.form.wicket.model.SInstanceValueModel;
import org.opensingular.singular.util.wicket.util.JavaScriptUtils;

public class PortletRichTextPanel extends Panel implements Loggable {

    private static String HTML_NEW_TAB;

    static {
        HTML_NEW_TAB = Optional.ofNullable(PortletRichTextPanel.class.getResourceAsStream("PortletRichTextNewTab.html"))
                .map(in -> {
                    try {
                        return JavaScriptUtils.javaScriptEscape(IOUtils.toString(in, "UTF-8"));
                    } catch (IOException e) {
                        throw new SingularFormException("Não foi possivel extrair o conteudo html", e);
                    }
                }).orElse(StringUtils.EMPTY);
    }

    private HiddenField hiddenInput;
    private Label       htmlContent;
    private Label       label;
    private String      hash;

    @Override
    public void renderHead(IHeaderResponse response) {
        super.renderHead(response);
        try (PackageTextTemplate packageTextTemplate = new PackageTextTemplate(getClass(), "PortletRichTextPanel.js")) {
            final Map<String, String> params = new HashMap<>();
            params.put("label", (String) label.getDefaultModel().getObject());
            params.put("htmlContainer", htmlContent.getMarkupId());
            params.put("hiddenInput", hiddenInput.getMarkupId());
            params.put("hash", hash);
            params.put("html", HTML_NEW_TAB);
            packageTextTemplate.interpolate(params);
            response.render(JavaScriptHeaderItem.forScript(packageTextTemplate.getString(), hash));
        } catch (IOException e) {
            getLogger().error(e.getMessage(), e);
        }
    }

    public PortletRichTextPanel(String id, WicketBuildContext ctx) {
        super(id);
        hash = RandomStringUtils.random(10, true, false);
        build(ctx);
        addBehaviours();
    }

    private void addBehaviours() {
        add($b.onReadyScript(c -> $(htmlContent).append(".html(").append($(hiddenInput).append(".val())"))));
    }

    private void build(WicketBuildContext ctx) {
        add(label = new Label("label", Model.of(Optional.ofNullable(ctx.getCurrentInstance().asAtr().getLabel()).orElse(EMPTY))));
        add(htmlContent = new Label("htmlContent", new SInstanceValueModel<>(ctx.getModel())));
        add(hiddenInput = new HiddenField<>("hiddenInput", new SInstanceValueModel<>(ctx.getModel())));
        add(new Button("button") {
            @Override
            protected String getOnClickScript() {
                return "openNewTabWithCKEditor" + hash + "();";
            }
        });
        htmlContent.setEscapeModelStrings(false);
    }

}