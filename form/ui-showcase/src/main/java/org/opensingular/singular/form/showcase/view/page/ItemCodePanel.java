/*
 * Copyright (C) 2016 Singular Studios (a.k.a Atom Tecnologia) - www.opensingular.com
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.opensingular.singular.form.showcase.view.page;

import java.util.List;

import org.apache.wicket.markup.head.IHeaderResponse;
import org.apache.wicket.markup.head.OnDomReadyHeaderItem;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;

import org.opensingular.form.wicket.util.ProcessadorCodigoFonte;
import org.opensingular.lib.wicket.util.util.WicketUtils;

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
