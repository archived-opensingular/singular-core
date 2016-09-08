package br.net.mirante.singular.form.wicket.mapper.richtext;

import br.net.mirante.singular.commons.util.Loggable;
import br.net.mirante.singular.form.SingularFormException;
import br.net.mirante.singular.form.io.IOUtil;
import br.net.mirante.singular.form.wicket.WicketBuildContext;
import br.net.mirante.singular.form.wicket.model.SInstanceValueModel;
import org.apache.commons.lang3.StringUtils;
import org.apache.wicket.markup.head.IHeaderResponse;
import org.apache.wicket.markup.head.JavaScriptHeaderItem;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.HiddenField;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.Model;
import org.apache.wicket.util.template.PackageTextTemplate;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import static br.net.mirante.singular.util.wicket.jquery.JQuery.$;
import static br.net.mirante.singular.util.wicket.util.WicketUtils.$b;
import static org.apache.commons.lang3.StringUtils.EMPTY;

public class PortletRichTextPanel extends Panel implements Loggable {

    private static String HTML_NEW_TAB;

    static {
        HTML_NEW_TAB = Optional.ofNullable(PortletRichTextPanel.class.getResourceAsStream("PortletRichTextNewTab.html"))
                .map(in -> {
                    try {
                        final StringBuilder builder = new StringBuilder();
                        IOUtil.readLines(in).forEach(line -> builder.append(line.trim().replaceAll("\\r?\\n", "")));
                        return builder.toString();
                    } catch (IOException e) {
                        throw new SingularFormException("Não foi possivel extrair o conteudo html", e);
                    }
                }).orElse(StringUtils.EMPTY);
    }

    private HiddenField        hiddenInput;
    private WebMarkupContainer htmlContent;
    private Label              label;

    @Override
    public void renderHead(IHeaderResponse response) {
        super.renderHead(response);
        try (PackageTextTemplate packageTextTemplate = new PackageTextTemplate(getClass(), "PortletRichTextPanel.js")) {
            final Map<String, String> params = new HashMap<>();

            params.put("label", (String) label.getDefaultModel().getObject());
            params.put("htmlContainer", htmlContent.getMarkupId());
            params.put("hiddenInput", hiddenInput.getMarkupId());
            params.put("html", HTML_NEW_TAB);

            packageTextTemplate.interpolate(params);
            response.render(JavaScriptHeaderItem.forScript(packageTextTemplate.getString(), "PortletRichTextPanel"));
        } catch (IOException e) {
            getLogger().error(e.getMessage(), e);
        }
    }

    public PortletRichTextPanel(String id, WicketBuildContext ctx) {
        super(id);
        build(ctx);
        addBehaviours();
    }

    private void addBehaviours() {
        add($b.onReadyScript(c -> $(htmlContent).append(".html(").append($(hiddenInput).append(".val())"))));
    }

    private void build(WicketBuildContext ctx) {
        add(label = new Label("label", Model.of(Optional.ofNullable(ctx.getCurrentInstance().asAtr().getLabel()).orElse(EMPTY))));
        add(htmlContent = new WebMarkupContainer("htmlContent"));
        add(hiddenInput = new HiddenField<>("hiddenInput", new SInstanceValueModel<>(ctx.getModel())));
    }

}