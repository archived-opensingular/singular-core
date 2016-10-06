/*
 * Copyright (c) 2016, Mirante and/or its affiliates. All rights reserved.
 * Mirante PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package org.opensingular.singular.showcase.view.page;

import java.util.List;

import org.apache.wicket.markup.head.IHeaderResponse;
import org.apache.wicket.markup.head.OnDomReadyHeaderItem;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;

import org.opensingular.singular.form.wicket.util.ProcessadorCodigoFonte;
import org.opensingular.singular.util.wicket.util.WicketUtils;

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

    public ItemCodePanel(String id, IModel<String> code, String extension) {
        super(id);
        final ProcessadorCodigoFonte pcf = new ProcessadorCodigoFonte(code.getObject());
        add(new Label("code", pcf.getFonteProcessado())
                .add(WicketUtils.$b.classAppender(getSyntaxHighlighterConfig(pcf.getLinhasParaDestacar(), extension))));
    }

    private String getSyntaxHighlighterConfig(List<Integer> linhasParaDestacar, String extension) {
        StringBuilder config = new StringBuilder();
        if ("xsd".equalsIgnoreCase(extension)) {
            extension = "xml";
        }
        config.append(String.format("brush: %s;", extension));

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
