package br.net.mirante.singular.pet.server.wicket.view.rascunho;

import static br.net.mirante.singular.util.wicket.util.WicketUtils.$b;
import static br.net.mirante.singular.util.wicket.util.WicketUtils.$m;

import java.util.Iterator;

import javax.inject.Inject;

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.attributes.AjaxRequestAttributes;
import org.apache.wicket.ajax.markup.html.form.AjaxButton;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Button;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.StringResourceModel;

import br.net.mirante.singular.form.wicket.component.BFModalBorder;
import br.net.mirante.singular.pet.module.wicket.view.template.Content;
import br.net.mirante.singular.pet.server.wicket.dao.PeticaoDAO;
import br.net.mirante.singular.pet.server.wicket.model.Peticao;
import br.net.mirante.singular.util.wicket.datatable.BSDataTable;
import br.net.mirante.singular.util.wicket.datatable.BSDataTableBuilder;
import br.net.mirante.singular.util.wicket.datatable.BaseDataProvider;
import br.net.mirante.singular.util.wicket.datatable.IBSAction;
import br.net.mirante.singular.util.wicket.datatable.column.BSActionColumn;
import br.net.mirante.singular.util.wicket.resource.Icone;

public class RascunhoContent extends Content {

    public static final int ROWS_PER_PAGE = 10;

    @Inject
    private PeticaoDAO peticaoDAO;

    private BSDataTable<Peticao, String>    listTable;

    private final BFModalBorder deleteModal  = new BFModalBorder("deleteModal");
    private final BFModalBorder viewXmlModal = new BFModalBorder("viewXmlModal");
    private Form<?>             deleteForm   = new Form<>("delete-form");

    private String filtro = "";
    private Peticao currentModel;

    public RascunhoContent(String id) {
        super(id);
    }

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
        queue(new Link<String>("novo") {
            @Override
            public void onClick() {}

            @Override
            protected boolean getStatelessHint() {
                return true;
            }

            @Override
            protected CharSequence getURL() {
                return "/canabidiol/form/edit?type=mform.peticao.canabidiol.PeticionamentoCanabidiol";
            }
        }.setBody(getMessage("label.button.insert")).add($b.attr("target", "blank")));

        listTable = setupDataTable();
        queue(listTable);

        deleteModal.queue(deleteForm.queue(new AjaxButton("delete-btn") {
            @Override
            protected void onSubmit(AjaxRequestTarget target, Form<?> form) {
                peticaoDAO.remove(currentModel);
                currentModel = null;
                target.add(RascunhoContent.this.listTable);
                deleteModal.hide(target);
            }
        }).queue(new AjaxButton("cancel-delete-btn") {
            @Override
            protected void onSubmit(AjaxRequestTarget target, Form<?> form) {
                currentModel = null;
                deleteModal.hide(target);
            }
        }));

        queue(deleteModal);

    }

    private BSDataTable<Peticao, String> setupDataTable() {

        return new BSDataTableBuilder<>(createDataProvider())
                .appendPropertyColumn(getMessage("label.table.column.number"),
                        "id", Peticao::getId)
                .appendPropertyColumn(getMessage("label.table.column.description"),
                        "description", Peticao::getDescription)
                .appendPropertyColumn(getMessage("label.table.column.process"),
                        "process", Peticao::getProcess)
                .appendPropertyColumn(getMessage("label.table.column.creation.date"),
                        "creationDate", Peticao::getCreationDate)
                .appendColumn(new BSActionColumn<Peticao, String>(getMessage("label.table.column.actions"))
                        .appendAction(getMessage("label.table.column.delete"),
                        Icone.MINUS, this::deleteSelected))
                .setRowsPerPage(ROWS_PER_PAGE)
                .build("tabela");
    }

    private BaseDataProvider<Peticao, String> createDataProvider() {
        return new BaseDataProvider<Peticao, String>() {

            @Override
            public long size() {
                return peticaoDAO.countQuickSearch(filtro);
            }

            @Override
            public Iterator<? extends Peticao> iterator(int first, int count,
                                                               String sortProperty, boolean ascending) {
                return peticaoDAO.quickSearch(filtro, first, count, sortProperty, ascending).iterator();
            }
        };
    }

    private void deleteSelected(AjaxRequestTarget target, IModel<Peticao> model) {
        currentModel = model.getObject();
        deleteModal.show(target);
    }

    @Override
    protected IModel<?> getContentTitlelModel() {
        return $m.ofValue("Rascunho");
    }

    @Override
    protected IModel<?> getContentSubtitlelModel() {
        return $m.ofValue("Petições de rascunho");
    }

    private StringResourceModel getMessage(String prop) {
        return new StringResourceModel(prop.trim(), this, null);
    }

    public String getFiltro() {
        return filtro;
    }

    public void setFiltro(String filtro) {
        this.filtro = filtro;
    }
}
