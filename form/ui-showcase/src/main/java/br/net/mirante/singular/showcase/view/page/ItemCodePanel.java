/*
 * Copyright (c) 2016, Mirante and/or its affiliates. All rights reserved.
 * Mirante PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package br.net.mirante.singular.showcase.view.page;

import java.util.List;

import org.apache.wicket.markup.head.IHeaderResponse;
import org.apache.wicket.markup.head.OnDomReadyHeaderItem;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;

import br.net.mirante.singular.form.wicket.util.ProcessadorCondigoFonte;
import br.net.mirante.singular.util.wicket.util.WicketUtils;

public class ItemCodePanel extends Panel {

    @Override
    public void renderHead(IHeaderResponse response) {
        super.renderHead(response);
        final StringBuilder initScript = new StringBuilder();
        initScript.append("SyntaxHighlighter.defaults['toolbar'] = false;");
        initScript.append("SyntaxHighlighter.defaults['quick-code'] = false;");
        initScript.append("SyntaxHighlighter.all();");
        response.render(OnDomReadyHeaderItem.forScript(initScript.toString()));
    }

    public ItemCodePanel(String id, IModel<String> code, IModel<String> extension) {
        super(id);
        final ProcessadorCondigoFonte pcf = new ProcessadorCondigoFonte(code.getObject());
        add(new Label("code", pcf.getFonteProcessado())
                .add(WicketUtils.$b.classAppender(getSyntaxHighlighterConfig(pcf.getLinhasParaDestacar(), extension))));
    }

    private String getSyntaxHighlighterConfig(List<Integer> linhasParaDestacar, IModel<String> extension) {
        StringBuilder config = new StringBuilder();
        config.append(String.format("brush: %s;", extension.getObject()));

        if (!linhasParaDestacar.isEmpty()) {
            config.append(" highlight: [");
            linhasParaDestacar.forEach(l -> {
                config.append(l);
                if (linhasParaDestacar.indexOf(l) != linhasParaDestacar.size() - 1) {
                    config.append(", ");
                }
            });
            config.append("]; ");
        }

        return config.toString();
    }

    @Override
    protected void onConfigure() {
        super.onConfigure();
    }

}
