package br.net.mirante.singular.form.wicket.mapper;

import br.net.mirante.singular.form.mform.MInstancia;
import br.net.mirante.singular.form.mform.basic.view.MView;
import br.net.mirante.singular.form.mform.core.MTipoString;
import br.net.mirante.singular.form.mform.options.MOptionsProvider;
import br.net.mirante.singular.form.wicket.model.MInstanciaValorModel;
import br.net.mirante.singular.util.wicket.bootstrap.layout.BSContainer;
import br.net.mirante.singular.util.wicket.bootstrap.layout.BSControls;
import br.net.mirante.singular.util.wicket.bootstrap.layout.BSGrid;
import br.net.mirante.singular.util.wicket.datatable.BSDataTable;
import br.net.mirante.singular.util.wicket.datatable.BSDataTableBuilder;
import br.net.mirante.singular.util.wicket.datatable.column.BSActionColumn;
import br.net.mirante.singular.util.wicket.modal.BSModalWindow;
import org.apache.commons.lang3.StringUtils;
import org.apache.wicket.Component;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.ajax.markup.html.form.AjaxSubmitLink;
import org.apache.wicket.behavior.Behavior;
import org.apache.wicket.extensions.markup.html.repeater.util.SortableDataProvider;
import org.apache.wicket.markup.ComponentTag;
import org.apache.wicket.markup.MarkupStream;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.request.Response;

import java.io.Serializable;
import java.util.Iterator;
import java.util.stream.Collectors;

import static br.net.mirante.singular.util.wicket.util.WicketUtils.$b;

@SuppressWarnings("serial")
public class SelectModalBuscaMapper implements ControlsFieldComponentMapper {


    @Override
    public Component appendInput(MView view, BSContainer bodyContainer, BSControls formGroup, IModel<? extends MInstancia> model, IModel<String> labelModel) {
        return formGroupAppender(formGroup, bodyContainer, model);
    }

    public MOptionsProvider getProvider(IModel<? extends MInstancia> model) {
        MOptionsProvider provider = ((MTipoString) model.getObject().getMTipo()).getProviderOpcoes();
        return provider;
    }


    protected Component formGroupAppender(BSControls formGroup, BSContainer modalContainer, IModel<? extends MInstancia> model) {
        MInstanciaValorModel valueModel = new MInstanciaValorModel<>(model);
        BSContainer panel = new BSContainer(model.getObject().getNome() + "inputGrupo");
        TextField<String> t = new TextField<>(model.getObject().getNome() + "selection", valueModel);
        t.setEnabled(false);
        t.add($b.attr("readonly", "readonly"));

        panel.appendTag("input", true, "class=\"form-control\"", t);

        Model<Filtro> f = Model.of(new Filtro());
        panel.appendTag("span", true, "class=\"input-group-btn\"", buildSearchButton(model.getObject().getNome() + "modal", t, modalContainer, model, valueModel, f));

        /* input-group-sm precisa ser adicionado aqui por que o form-group atual adiciona o form-group-sm */
        formGroup.appendTag("div", true, "class=\"input-group input-group-sm\"", panel);

        return t;
    }


    protected Panel buildSearchButton(String id, Component valueInput, BSContainer modalContainer, IModel<? extends MInstancia> model, MInstanciaValorModel valueModel, IModel<Filtro> filterModel) {
        BSContainer panel = new BSContainer(id);


        final BSModalWindow searchModal = buildModal(id + "__modal", valueInput, modalContainer, model, filterModel);

        panel.appendTag("a", true, "class=\"btn btn-default\"", new AjaxLink("link") {
            @Override
            public void onClick(AjaxRequestTarget target) {
                searchModal.show(target);
            }

            @Override
            public void onComponentTagBody(MarkupStream markupStream, ComponentTag openTag) {
                Response response = getResponse();
                response.write("\n");
                response.write("<i class=\"fa fa-search\"></i>");
                super.onComponentTagBody(markupStream, openTag);
            }
        });

        modalContainer.appendTag("div", true, "", searchModal);

        return panel;
    }

    public BSModalWindow buildModal(String id, Component valueInput, BSContainer modalContainer, IModel<? extends MInstancia> model, IModel<Filtro> filterModel) {
        BSModalWindow searchModal = new BSModalWindow(id, true);
        searchModal.setTitleText(Model.of("Buscar"));
        searchModal.setBody(buildConteudoModal(id, valueInput, model, filterModel, searchModal));
        return searchModal;
    }

    public BSGrid buildConteudoModal(String id, Component valueInput, IModel<? extends MInstancia> model, IModel<Filtro> filterModel, BSModalWindow modal) {
        BSGrid grid = new BSGrid(id + "_modalBody");

        Component table = buildResultTable(id + "resultTable", valueInput, model, filterModel, modal);

        buildSearchField(id + "searchField", filterModel, grid, table, modal.getForm());

        grid.appendTag("table", true, "", table);

        return grid;
    }

    private Component buildResultTable(String id, final Component valueInput, final IModel<? extends MInstancia> model, IModel<Filtro> filterModel, final BSModalWindow modal) {
        BSDataTable<Dado, Filtro> table = new BSDataTableBuilder<>(buildDataProvider(model, filterModel))
                .appendPropertyColumn(Model.of("descricao"), "descricao")
                .appendColumn(new BSActionColumn<Dado, Filtro>(Model.of(""))
                        .appendAction(Model.of("Selecionar"), (target, selectedModel) -> {
                            ((IModel<String>) valueInput.getDefaultModel()).setObject(selectedModel.getObject().getDescricao());
                            modal.hide(target);
                            target.add(valueInput);
                        }))
                .build("processos");
        table.add(new Behavior() {
            @Override
            public void onConfigure(Component component) {
                super.onConfigure(component);
                component.setVisible(((BSDataTable) component).getDataProvider().size() > 0);
            }
        });
        table.setVisible(false);
        return table;
    }

    public void buildSearchField(String id, IModel<?> filterModel, BSGrid grid, Component table, Form<?> form) {
        BSControls formGroup = grid.newFormGroup();

        BSContainer inputGroup = new BSContainer(id + "inputGrup");
        formGroup.appendTag("div", true, "class=\"input-group input-group-sm\"", inputGroup);

        TextField inputFiltro = new TextField("termo", new PropertyModel(filterModel, "termo"));
        inputGroup.appendTag("input", true, "class=\"form-control\"", inputFiltro);

        BSContainer inputGroupButton = new BSContainer(id + "inputGroupButton");
        inputGroup.appendTag("span", true, "class=\"input-group-btn\"", inputGroupButton);
        inputGroupButton.appendTag("a", true, "class=\"btn btn-default\"", new AjaxSubmitLink("link", form) {

            @Override
            protected void onSubmit(AjaxRequestTarget target, Form<?> form) {
                target.add(table);
            }

            @Override
            public void onComponentTagBody(MarkupStream markupStream, ComponentTag openTag) {
                Response response = getResponse();
                response.write("\n");
                response.write("<i class=\"fa fa-search\"></i>");
                super.onComponentTagBody(markupStream, openTag);
            }
        });
    }

    public SortableDataProvider<Dado, Filtro> buildDataProvider(
            IModel<? extends MInstancia> model, final IModel<Filtro> filtro) {
        return new SortableDataProvider<Dado, Filtro>() {
            @Override
            public Iterator<? extends Dado> iterator(long first, long count) {
                Iterator<? extends Dado> it = getProvider(model)
                        .getOpcoes(model.getObject())
                        .getValor()
                        .stream()
                        .map(Object::toString)
                        .filter(s -> SelectModalBuscaMapper.this.filtrar(filtro, s))
                        .map(s -> new Dado(s))
                        .collect(Collectors.toList())
                        .iterator();
                return it;
            }

            @Override
            public long size() {
                long size = getProvider(model)
                        .getOpcoes(model.getObject())
                        .getValor()
                        .stream()
                        .map(Object::toString)
                        .filter(s -> SelectModalBuscaMapper.this.filtrar(filtro, s))
                        .count();
                return size;
            }

            @Override
            public IModel<Dado> model(Dado object) {
                return Model.of(object);
            }
        };
    }

    public boolean filtrar(IModel<Filtro> filtro, String s) {
        if (filtro != null && filtro.getObject() != null) {
            String termo = filtro.getObject().getTermo();
            return termo == null
                    || s.toLowerCase().contains(termo.toLowerCase());
        } else {
            return false;
        }
    }

    public static class Dado implements Serializable {

        public String descricao;

        public Dado() {
        }

        public Dado(String descricao) {
            this.descricao = descricao;
        }

        public String getDescricao() {
            return descricao;
        }

        public void setDescricao(String descricao) {
            this.descricao = descricao;
        }
    }

    public static class Filtro implements Serializable {
        private String termo = "";

        public String getTermo() {
            return termo;
        }

        public void setTermo(String termo) {
            this.termo = termo;
        }
    }

    @Override
    public String getReadOnlyFormatedText(IModel<? extends MInstancia> model) {
        if (model.getObject() != null && model.getObject().getValor() != null) {
            return String.valueOf(model.getObject().getValor());
        }
        return StringUtils.EMPTY;
    }
}
