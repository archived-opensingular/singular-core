package br.net.mirante.singular.pet.commons.wicket.view.template;

import java.util.List;

import br.net.mirante.singular.pet.commons.wicket.PetApplication;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.wicket.Component;
import org.apache.wicket.ajax.AbstractDefaultAjaxBehavior;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.markup.head.IHeaderResponse;
import org.apache.wicket.markup.head.OnDomReadyHeaderItem;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.request.cycle.RequestCycle;
import org.apache.wicket.request.handler.TextRequestHandler;

import br.net.mirante.singular.lambda.ISupplier;
import br.net.mirante.singular.util.wicket.menu.MetronicMenu;
import br.net.mirante.singular.util.wicket.menu.MetronicMenuItem;
import br.net.mirante.singular.util.wicket.resource.Icone;

public class Menu extends Panel {

    /**
     *
     */
    private static final long serialVersionUID = 7622791136418841943L;

    public Menu(String id) {
        super(id);
        add(buildMenu());
    }

    protected MetronicMenu buildMenu() {
        MetronicMenu menu = new MetronicMenu("menu");

        menu.addItem(new MetronicMenuItem(Icone.HOME, "Início", PetApplication.get().getHomePage()));

        return menu;
    }

    protected static class AddContadoresBehaviour extends AbstractDefaultAjaxBehavior {

        private final List<Pair<Component, ISupplier<String>>> itens;

        public AddContadoresBehaviour(List<Pair<Component, ISupplier<String>>> itens) {
            this.itens = itens;
        }

        @Override
        public void renderHead(Component component, IHeaderResponse response) {
            super.renderHead(component, response);
            StringBuilder js = new StringBuilder();
            js.append("window.Singular = window.Singular || {};");
            js.append("window.Singular.atualizarContadores = function(){ ");
            js.append(" $(document).ready(function(){ ");
            js.append("     $(document).ready(function(){");
            js.append("         $.getJSON('").append(getCallbackUrl()).append("', function(json) { ");
            for (int i = 0; i < itens.size(); i++) {
                final String markupId = itens.get(i).getLeft().getMarkupId();
                final String currentItem = "item" + i;
                js.append("var ").append(currentItem).append(" = ").append(" $('#").append(markupId).append("');");
                js.append(currentItem).append(".hide(); ");
                js.append(currentItem).append(".addClass('badge badge-danger'); ");
                js.append(currentItem).append(".html(json.").append(currentItem).append(");");
                js.append(currentItem).append(".fadeIn('slow'); ");
            }
            js.append("         });");
            js.append("     });");
            js.append(" });");
            js.append("};");
            js.append("window.Singular.atualizarContadores(); ");
            response.render(OnDomReadyHeaderItem.forScript(js));
        }

        @Override
        protected void respond(AjaxRequestTarget target) {
            final String type = "application/json";
            final String encoding = "UTF-8";
            final StringBuilder json = new StringBuilder();
            json.append("{");
            for (int i = 0; i < itens.size(); i++) {
                json.append("\"item").append(i).append("\"").append(":").append(itens.get(i).getRight().get());
                if (i + 1 != itens.size()) {
                    json.append(",");
                }
            }
            json.append("}");
            RequestCycle.get().scheduleRequestHandlerAfterCurrent(new TextRequestHandler(type, encoding, json.toString()));
        }
    }
}
