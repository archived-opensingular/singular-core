package br.net.mirante.singular.view.page.showcase;

import br.net.mirante.singular.form.wicket.util.ProcessadorCondigoFonte;
import br.net.mirante.singular.util.wicket.util.WicketUtils;
import org.apache.wicket.markup.head.IHeaderResponse;
import org.apache.wicket.markup.head.OnDomReadyHeaderItem;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;

import java.util.List;

public class ItemCodePanel extends Panel {

    @Override
    public void renderHead(IHeaderResponse response) {
        super.renderHead(response);
        response.render(OnDomReadyHeaderItem.forScript("SyntaxHighlighter.defaults['toolbar'] = false;SyntaxHighlighter.all();"));
    }

    public ItemCodePanel(String id, IModel<String> code) {
        super(id);
        final ProcessadorCondigoFonte pcf = new ProcessadorCondigoFonte(code.getObject());
        add(new Label("code", pcf.getFonteProcessado()).add(WicketUtils.$b.classAppender(getSyntaxHightliterConfig(pcf.getLinhasParaDestacar()))));
    }

    private String getSyntaxHightliterConfig(List<Integer> linhasParaDestacar) {
        StringBuilder config = new StringBuilder();
        config.append("brush: java;");

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
