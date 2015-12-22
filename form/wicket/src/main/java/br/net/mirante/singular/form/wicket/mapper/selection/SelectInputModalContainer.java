package br.net.mirante.singular.form.wicket.mapper.selection;

import br.net.mirante.singular.form.mform.MInstancia;
import br.net.mirante.singular.form.mform.MTipo;
import br.net.mirante.singular.form.mform.MTipoComposto;
import br.net.mirante.singular.form.mform.basic.ui.AtrBasic;
import br.net.mirante.singular.form.mform.basic.view.MSelecaoPorModalBuscaView;
import br.net.mirante.singular.util.wicket.bootstrap.layout.BSContainer;
import br.net.mirante.singular.util.wicket.bootstrap.layout.BSControls;
import br.net.mirante.singular.util.wicket.bootstrap.layout.BSGrid;
import br.net.mirante.singular.util.wicket.datatable.BSDataTable;
import br.net.mirante.singular.util.wicket.datatable.BSDataTableBuilder;
import br.net.mirante.singular.util.wicket.datatable.column.BSActionColumn;
import br.net.mirante.singular.util.wicket.modal.BSModalWindow;
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
import org.apache.wicket.request.Response;

import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static br.net.mirante.singular.util.wicket.util.WicketUtils.$b;

/**
 * This component is used to represent a selection placed in a modal search.
 */
public class SelectInputModalContainer extends BSContainer {

    private BSControls formGroup;
    private BSContainer modalContainer;
    private IModel<? extends MInstancia> model;
    private MSelecaoPorModalBuscaView view;
    private TextField<String> valueInput;

    public SelectInputModalContainer(String id, BSControls formGroup, BSContainer modalContainer,
                                     IModel<? extends MInstancia> model,MSelecaoPorModalBuscaView view) {
        super(id);
        this.formGroup = formGroup;
        this.modalContainer = modalContainer;
        this.model = model;
        this.view = view;
    }

    public Component build(){
        MSelectionModalInstanceModel valueModel = new MSelectionModalInstanceModel(model);
        createValueInput(valueModel);

        this.appendTag("input", true, "class=\"form-control\"", valueInput);

        Model<String> f = Model.of("");
        this.appendTag("span", true, "class=\"input-group-btn\"", buildSearchButton(
                model.getObject().getNome() + "_modal", valueModel, f));

        /* input-group-sm precisa ser adicionado aqui por que o form-group atual adiciona o form-group-sm */
        formGroup.appendTag("div", true, "class=\"input-group input-group-sm\"", this);
        return valueInput;
    }

    private void createValueInput(MSelectionModalInstanceModel valueModel) {
        valueInput = new TextField<>(model
                .getObject().getNome() + "selection", valueModel);
        valueInput.setEnabled(false);
        valueInput.add($b.attr("readonly", "readonly"));
    }

    protected Panel buildSearchButton(String id, MSelectionInstanceModel valueModel,
                                      IModel<String> filterModel) {
        BSContainer panel = new BSContainer(id);


        final BSModalWindow searchModal = buildModal(id + "__modal", filterModel);

        panel.appendTag("a", true, "class=\"btn default\"", new AjaxLink("link") {
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

    public BSModalWindow buildModal(String id, IModel<String> filterModel) {
        BSModalWindow searchModal = new BSModalWindow(id, true);
        searchModal.setTitleText(Model.of("Buscar"));
        searchModal.setBody(buildConteudoModal(id, filterModel, searchModal));
        return searchModal;
    }

    public BSGrid buildConteudoModal(String id, IModel<String> filterModel, BSModalWindow modal) {
        BSGrid grid = new BSGrid(id + "_modalBody");

        Component table = buildResultTable(id + "_resultTable", filterModel, modal);

        buildSearchField(id + "_searchField", filterModel, grid, table, modal.getForm());

        grid.appendTag("table", true, "", table);

        return grid;
    }

    private Component buildResultTable(String id, IModel<String> filterModel, final BSModalWindow modal) {
        BSDataTableBuilder builder  = new BSDataTableBuilder<>(buildDataProvider(model, filterModel));
        builder.appendPropertyColumn(Model.of(""), "value");
        MTipo<?> type = model.getObject().getMTipo();
        if(type instanceof MTipoComposto){
            MTipoComposto selectType = (MTipoComposto) type;
            for(String field: view.searchFields()){
                MTipo<?> fieldType = selectType.getCampo(field);
                String label = fieldType.as(AtrBasic::new).getLabel();
                builder.appendPropertyColumn(Model.of(label),
                        o -> {
                            return ((SelectOption) o).getOtherField(field);
                        });
            }
        }
        builder.appendColumn(new BSActionColumn<SelectOption, String>(Model.of(""))
                .appendAction(Model.of("Selecionar"), (target, selectedModel) -> {
                    ((IModel<SelectOption>) valueInput.getDefaultModel())
                            .setObject(selectedModel.getObject());
                    modal.hide(target);
                    target.add(valueInput);
                }));

        BSDataTable<SelectOption, String> table = builder.build("selectionModalTable");
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

        BSContainer inputGroup = new BSContainer(id + "inputGroup");
        formGroup.appendTag("div", true, "class=\"input-group input-group-sm\"", inputGroup);

        TextField inputFiltro = new TextField("termo", filterModel);
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

    public SortableDataProvider<SelectOption, String> buildDataProvider(
            IModel<? extends MInstancia> model, final IModel<String> filtro) {
        MTipo<?> type = model.getObject().getMTipo();
        final List<SelectOption> options = WicketSelectionUtils.createOptions(model, type);
        return new SortableDataProvider<SelectOption, String>() {
            @Override
            public Iterator<? extends SelectOption> iterator(long first, long count) {
                return filterOptions(filtro, options)
                        .collect(Collectors.toList())
                        .iterator();
            }

            @Override
            public long size() {
                return filterOptions(filtro, options).count();
            }

            private Stream<SelectOption> filterOptions(final IModel<String> filter,
                                                       final List<SelectOption> options) {
                return options
                        .stream()
                        .filter(s -> SelectInputModalContainer.this.filter(filter, s));
            }

            @Override
            public IModel<SelectOption> model(SelectOption object) {
                return Model.of(object);
            }
        };
    }

    public boolean filter(IModel<String> filtro, SelectOption s) {
        if (filtro != null && filtro.getObject() != null ) {
            String termo = filtro.getObject().toLowerCase();
            String value = s.getValue().toString().toLowerCase();
            if(value.contains(termo)) return true;
            if (checkFilterAgainstAditionalFields(s, termo)) return true;
            return false;
        }
        return true;
    }

    private boolean checkFilterAgainstAditionalFields(SelectOption s, String termo) {
        for (String field : view.searchFields()){
            Object f = s.getOtherField(field);
            String nValue = f.toString().toLowerCase();
            if(nValue.contains(termo)) return true;
        }
        return false;
    }

}

class MSelectionModalInstanceModel extends MSelectionInstanceModel{

    public MSelectionModalInstanceModel(IModel instanciaModel) {
        super(instanciaModel);
    }

    @Override
    protected Object getSimpleSelection(MInstancia target) {
        SelectOption v = (SelectOption) super.getSimpleSelection(target);
        if( v.getValue() == null ){
            return null;
        }
        return v.getValue();
    }
}