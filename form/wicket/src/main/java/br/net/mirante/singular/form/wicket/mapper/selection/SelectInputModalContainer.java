package br.net.mirante.singular.form.wicket.mapper.selection;

import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.apache.wicket.Component;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.ajax.markup.html.form.AjaxSubmitLink;
import org.apache.wicket.behavior.Behavior;
import org.apache.wicket.extensions.markup.html.repeater.data.table.IColumn;
import org.apache.wicket.extensions.markup.html.repeater.util.SortableDataProvider;
import org.apache.wicket.markup.ComponentTag;
import org.apache.wicket.markup.MarkupStream;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.request.Response;

import br.net.mirante.singular.form.mform.SIComposite;
import br.net.mirante.singular.form.mform.SISimple;
import br.net.mirante.singular.form.mform.SInstance;
import br.net.mirante.singular.form.mform.SType;
import br.net.mirante.singular.form.mform.STypeComposite;
import br.net.mirante.singular.form.mform.STypeSimple;
import br.net.mirante.singular.form.mform.SingularFormException;
import br.net.mirante.singular.form.mform.basic.ui.AtrBasic;
import br.net.mirante.singular.form.mform.basic.view.MSelecaoPorModalBuscaView;
import br.net.mirante.singular.form.mform.options.MOptionsConfig;
import br.net.mirante.singular.form.mform.util.transformer.Value;
import br.net.mirante.singular.form.wicket.component.BFModalWindow;
import br.net.mirante.singular.lambda.IConsumer;
import br.net.mirante.singular.util.wicket.bootstrap.layout.BSContainer;
import br.net.mirante.singular.util.wicket.bootstrap.layout.BSControls;
import br.net.mirante.singular.util.wicket.bootstrap.layout.BSGrid;
import br.net.mirante.singular.util.wicket.datatable.BSDataTable;
import br.net.mirante.singular.util.wicket.datatable.BSDataTableBuilder;
import br.net.mirante.singular.util.wicket.datatable.column.BSActionColumn;
import static br.net.mirante.singular.util.wicket.util.WicketUtils.$b;

/**
 * This component is used to represent a selection placed in a modal search.
 */
public class SelectInputModalContainer extends BSContainer {

    private BSControls formGroup;
    private BSContainer modalContainer;
    private IModel<? extends SInstance> model;
    private MSelecaoPorModalBuscaView view;
    private Label valueLabel;
    private IModel<String> valueLabelModel;
    private IConsumer<IModel<?>> clearModel = m -> m.setObject(null);

    public SelectInputModalContainer(String id, BSControls formGroup, BSContainer modalContainer,
                                     IModel<? extends SInstance> model, MSelecaoPorModalBuscaView view, IModel<String> valueLabelModel) {
        super(id);
        this.formGroup = formGroup;
        this.modalContainer = modalContainer;
        this.model = model;
        this.view = view;
        this.valueLabelModel = valueLabelModel;
    }

    public Component build() {
        MSelectionModalInstanceModel valueModel = new MSelectionModalInstanceModel(model);
        createValueInput();

        this.appendTag("span", true, "class=\"form-control\"", valueLabel);

        Model<String> f = Model.of("");
        this.appendTag("span", true, "class=\"input-group-btn\"", buildSearchButton(
                model.getObject().getNome() + "_modal", valueModel, f));

        /* input-group-sm precisa ser adicionado aqui por que o form-group atual adiciona o form-group-sm */
        formGroup.appendTag("div", true, "class=\"input-group input-group-sm\"", this);
        return valueLabel;
    }

    private void createValueInput() {
        valueLabel = new Label(model.getObject().getNome() + "selection", valueLabelModel);
        valueLabel.add($b.attr("readonly", "readonly"));
    }

    protected Panel buildSearchButton(String id, MSelectionInstanceModel valueModel,
                                      IModel<String> filterModel) {
        BSContainer panel = new BSContainer(id);


        final BFModalWindow searchModal = buildModal(id + "__modal", filterModel);

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

    public BFModalWindow buildModal(String id, IModel<String> filterModel) {
        BFModalWindow searchModal = new BFModalWindow(id, true);
        searchModal.setTitleText(Model.of("Buscar"));
        searchModal.setBody(buildConteudoModal(id, filterModel, searchModal));
        /**
         * Limpa o filtro apos fechar a modal
         */
        searchModal.setCloseIconCallback(target -> clearModel.accept(filterModel));
        return searchModal;
    }

    public BSGrid buildConteudoModal(String id, IModel<String> filterModel, BFModalWindow modal) {
        BSGrid grid = new BSGrid(id + "_modalBody");

        Component table = buildResultTable(id + "_resultTable", filterModel, modal);

        buildSearchField(id + "_searchField", filterModel, grid, table, modal.getForm());

        grid.appendTag("table", true, "", table);

        return grid;
    }

    private Component buildResultTable(String id, IModel<String> filterModel, final BFModalWindow modal) {
        final BSDataTableBuilder<SelectOption, Void, IColumn<SelectOption, Void>> builder
                = new BSDataTableBuilder<>(buildDataProvider(model, filterModel));
        builder.appendPropertyColumn(Model.of(""), "selectLabel");
        appendAdditionalSearchFields(builder);
        builder.appendColumn(new BSActionColumn<SelectOption, Void>(Model.of(""))
                .appendAction(Model.of("Selecionar"), (target, selectedModel) -> {
                    selectedModel.getObject().copyValueToInstance(model.getObject());
                    modal.hide(target);
                    target.add(valueLabel);
                    /**
                     * Limpa o filtro apos fechar a modal
                     */
                    clearModel.accept(filterModel);
                }));

        BSDataTable<SelectOption, Void> table = builder.build("selectionModalTable");
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

    private void appendAdditionalSearchFields(BSDataTableBuilder<SelectOption, Void, IColumn<SelectOption, Void>> builder) {
        for (String field : view.searchFields()) {
            builder.appendPropertyColumn(Model.of(getAdditionalSearchFieldLabel(field)), m -> {
                final STypeComposite selectType = (STypeComposite) model.getObject().getMTipo();
                final SType<?> fieldType = selectType.getCampo(field);
                final MOptionsConfig provider = model.getObject().getOptionsConfig();
                final SInstance instance = provider.getValueFromKey(String.valueOf(m.getValue()));
                return Value.of(instance, (STypeSimple) fieldType);
            });
        }
    }

    private String getAdditionalSearchFieldLabel(String searchField) {
        STypeComposite selectType = (STypeComposite) model.getObject().getMTipo();
        SType<?> fieldType = selectType.getCampo(searchField);
        if (!(fieldType instanceof STypeSimple)) {
            throw new SingularFormException(String.format("Search Fields must be a field of MTipoSimples! found: %s ", fieldType == null ? null : fieldType.getClass().getName()));
        }
        return fieldType.as(AtrBasic::new).getLabel();
    }

    public void buildSearchField(String id, IModel<String> filterModel, BSGrid grid, Component table, Form<?> form) {
        BSControls formGroup = grid.newFormGroup();

        BSContainer inputGroup = new BSContainer(id + "inputGroup");
        formGroup.appendTag("div", true, "class=\"input-group input-group-sm\"", inputGroup);

        TextField<String> inputFiltro = new TextField<>("termo", filterModel);
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

    public SortableDataProvider<SelectOption, Void> buildDataProvider(
            IModel<? extends SInstance> model, final IModel<String> filtro) {
        SType<?> type = model.getObject().getMTipo();
        final List<SelectOption> options = WicketSelectionUtils.createOptions(model, type);
        return new SortableDataProvider<SelectOption, Void>() {
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
        if (filtro != null && filtro.getObject() != null) {
            String termo = filtro.getObject().toLowerCase();
            String value = s.getSelectLabel().toLowerCase();
            if (value.contains(termo)) return true;
            return checkFilterAgainstAditionalFields(s, termo);
        }
        return true;
    }

    private boolean checkFilterAgainstAditionalFields(SelectOption s, String termo) {
        final MOptionsConfig miProvider = model.getObject().getOptionsConfig();
        final SInstance si = miProvider.getValueFromKey(String.valueOf(s.getValue()));

        if (SIComposite.class.isAssignableFrom(si.getClass())) {
            for (String field : view.searchFields()) {
                final Object value = Value.of((SISimple<?>) ((SIComposite) si).getCampo(field));
                final String nValue = String.valueOf(value).toLowerCase();
                if (nValue.contains(termo)) {
                    return true;
                }
            }
        }

        return false;
    }


    static class MSelectionModalInstanceModel extends MSelectionInstanceModel {

        public MSelectionModalInstanceModel(IModel instanciaModel) {
            super(instanciaModel);
        }

        @Override
        protected Object getSimpleSelection(SInstance target, MOptionsConfig provider) {
            SelectOption v = (SelectOption) super.getSimpleSelection(target, provider);
            if (v.getValue() == null) {
                return null;
            }
            return v.getValue();
        }
    }

}
