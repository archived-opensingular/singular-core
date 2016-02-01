package br.net.mirante.singular.pet.server.wicket.view.racunho;

import static br.net.mirante.singular.util.wicket.util.WicketUtils.$m;

import javax.inject.Inject;

import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Button;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.model.IModel;

import br.net.mirante.singular.pet.module.wicket.view.template.Content;
import br.net.mirante.singular.pet.server.wicket.dao.PeticaoDAO;

public class RascunhoContent extends Content {

    @Inject
    private PeticaoDAO peticaoDAO;

    public RascunhoContent(String id) {
        super(id);
    }

    private String filtro;

    public RascunhoContent(String id, boolean withInfoLink, boolean withBreadcrumb) {
        super(id, withInfoLink, withBreadcrumb);
    }

    @Override
    protected void onInitialize() {
        super.onInitialize();

        Form form = new Form("form") {
            @Override
            protected void onSubmit() {
                super.onSubmit();
            }
        };

        add(form);

        queue(new Label("filtroRapidoLabel", getString("label.filtro.rapido")));
        queue(new TextField<String>("filtroRapido", $m.property(this, "filtro")));
        queue(new Button("pesquisar", $m.ofValue(getString("label.pesquisar"))));

        queue(new ListView<Object>("tabela") {

            @Override
            protected void populateItem(ListItem<Object> item) {

                item.add(new Label("numero"));
                item.add(new Label("descricao"));
                item.add(new Label("processo"));
                item.add(new Label("dataCriacao"));
            }
        });

        peticaoDAO.listAll();
    }

    @Override
    protected IModel<?> getContentTitlelModel() {
        return $m.ofValue("Rascunho");
    }

    @Override
    protected IModel<?> getContentSubtitlelModel() {
        return $m.ofValue("Petições de rascunho");
    }

    public String getFiltro() {
        return filtro;
    }

    public void setFiltro(String filtro) {
        this.filtro = filtro;
    }
}
