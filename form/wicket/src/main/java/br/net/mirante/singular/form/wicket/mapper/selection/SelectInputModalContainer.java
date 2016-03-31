/*
 * Copyright (c) 2016, Mirante and/or its affiliates. All rights reserved.
 * Mirante PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package br.net.mirante.singular.form.wicket.mapper.selection;

import static br.net.mirante.singular.util.wicket.util.WicketUtils.$b;
import static br.net.mirante.singular.util.wicket.util.WicketUtils.$m;

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
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.request.Response;

import br.net.mirante.singular.commons.lambda.IConsumer;
import br.net.mirante.singular.form.mform.SIComposite;
import br.net.mirante.singular.form.mform.SISimple;
import br.net.mirante.singular.form.mform.SInstance;
import br.net.mirante.singular.form.mform.SType;
import br.net.mirante.singular.form.mform.STypeComposite;
import br.net.mirante.singular.form.mform.STypeSimple;
import br.net.mirante.singular.form.mform.SingularFormException;
import br.net.mirante.singular.form.mform.basic.view.SViewSelectionBySearchModal;
import br.net.mirante.singular.form.mform.options.SOptionsConfig;
import br.net.mirante.singular.form.mform.util.transformer.Value;
import br.net.mirante.singular.form.wicket.behavior.AjaxUpdateInputBehavior;
import br.net.mirante.singular.form.wicket.component.BFModalWindow;
import br.net.mirante.singular.form.wicket.model.IMInstanciaAwareModel;
import br.net.mirante.singular.util.wicket.ajax.ActionAjaxButton;
import br.net.mirante.singular.util.wicket.bootstrap.layout.BSContainer;
import br.net.mirante.singular.util.wicket.bootstrap.layout.BSControls;
import br.net.mirante.singular.util.wicket.bootstrap.layout.BSGrid;
import br.net.mirante.singular.util.wicket.datatable.BSDataTable;
import br.net.mirante.singular.util.wicket.datatable.BSDataTableBuilder;
import br.net.mirante.singular.util.wicket.datatable.column.BSActionColumn;
import br.net.mirante.singular.util.wicket.modal.BSModalBorder;
import br.net.mirante.singular.util.wicket.resource.Icone;

/**
 * This component is used to represent a selection placed in a modal search.
 */
class SelectInputModalContainer extends BSContainer {

    private BSControls                    formGroup;
    private BSContainer                   modalContainer;
    private IModel<? extends SInstance>   model;
    private SViewSelectionBySearchModal   view;
    private TextField<?>                  valueInput;
    private IMInstanciaAwareModel<String> valueInputModel;

    private IConsumer<IModel<?>> clearModel = m -> m.setObject(null);

    SelectInputModalContainer(String id,
                              BSControls formGroup,
                              BSContainer modalContainer,
                              IModel<? extends SInstance> model,
                              SViewSelectionBySearchModal view,
                              IMInstanciaAwareModel<String> valueInputModel) {
        super(id);
        this.formGroup = formGroup;
        this.modalContainer = modalContainer;
        this.model = model;
        this.view = view;
        this.valueInputModel = valueInputModel;
    }

    public Component build() {

        createValueInput();

        this.appendTag("input", true, "class=\"form-control\"", valueInput);
        this.appendTag("span", true, "class=\"input-group-btn\"", buildSearchButton(model.getObject().getName() + "_modal", Model.of("")));

        /* input-group-sm precisa ser adicionado aqui por que o form-group atual adiciona o form-group-sm */
        formGroup.appendTag("div", true, "class=\"input-group input-group-sm\"", this);

        return valueInput;
    }

    private void createValueInput() {
        valueInput = new TextField<>(model.getObject().getName() + "selection", valueInputModel);
        valueInput.setOutputMarkupId(true);
        valueInput.add($b.attr("readonly", "readonly"));
    }

    private Panel buildSearchButton(String id, IModel<String> filterModel) {

        final BSContainer   panel       = new BSContainer(id);
        final BFModalWindow searchModal = buildModal(id + "__modal", filterModel);

        panel.appendTag("a", true, "class='btn default'", new AjaxLink("search_link") {
            @Override
            public void onClick(AjaxRequestTarget target) {
                target.add(modalContainer);
                searchModal.getModalBorder().setVisible(true);
//                searchModal.show(target);
                target.appendJavaScript(getConfigureBackdropScript());
            }

            private String getConfigureBackdropScript() {
                String js = "";
                js += " (function (zindex){ ";
                js += "     $('.modal-backdrop').each(function(index) { ";
                js += "         $(this).css('z-index', zindex-1+index); ";
                js += "     }); ";
                js += "     $('.modal').each(function(index) { ";
                js += "         $(this).css('z-index', zindex+index); ";
                js += "     }); ";
                js += " })(10050); ";
                return js;
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

    private BFModalWindow buildModal(String id, IModel<String> filterModel) {
        final BFModalWindow searchModal = new BFModalWindow(id, true);
        searchModal.setTitleText(Model.of("Buscar"));
        searchModal.setBody(buildConteudoModal(id, filterModel, searchModal));
        /**
         * Limpa o filtro apos fechar a modal
         */
        searchModal.setCloseIconCallback(target -> clearModel.accept(filterModel));
        searchModal.addButton(BSModalBorder.ButtonStyle.DANGER, $m.ofValue("Limpar"),
                new ActionAjaxButton("limpar") {
                    @Override
                    protected void onAction(AjaxRequestTarget target, Form<?> form) {
//                        model.getObject().clearInstance();
                        target.add(valueInput);
                        searchModal.hide(target);
                    }
                });
        return searchModal;
    }

    private BSGrid buildConteudoModal(String id, IModel<String> filterModel, BFModalWindow modal) {
        BSGrid grid = new BSGrid(id + "_modalBody");

        Component table = buildResultTable(filterModel, modal);

        buildSearchField(id + "_searchField", filterModel, grid, table, modal.getForm());

        grid.appendTag("table", true, "", table);

        return grid;
    }

    private Component buildResultTable(IModel<String> filterModel, final BFModalWindow modal) {

        final BSDataTableBuilder<SelectOption, Void, IColumn<SelectOption, Void>> builder;
        builder = new BSDataTableBuilder<>(buildDataProvider(model, filterModel));

        builder.appendPropertyColumn(Model.of(""), "selectLabel");
        appendAdditionalSearchFields(builder);
        builder
                .appendColumn(new BSActionColumn<SelectOption, Void>(Model.of(""))
                        .appendAction(Model.of("Selecionar"), Icone.HAND_UP, (target, selectedModel) -> {
                            valueInput.clearInput();

                            selectedModel
                                    .getObject()
                                    .copyValueToInstance(model.getObject());

                            valueInput.getBehaviors(AjaxUpdateInputBehavior.class).forEach(b -> b.onUpdate(target));
                            target.add(valueInput);
                            modal.hide(target);
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
                boolean isEmpty = ((BSDataTable) component).getDataProvider().size() == 0;
                if (isEmpty) {
                    addInfoMessage("Nenhum registro encontrado.");
                }
                component.setVisible(!isEmpty);
            }
        });
        table.setVisible(false);
        return table;
    }

    private void appendAdditionalSearchFields(BSDataTableBuilder<SelectOption, Void, IColumn<SelectOption, Void>> builder) {
        for (String field : view.searchFields()) {
            builder.appendPropertyColumn(Model.of(getAdditionalSearchFieldLabel(field)), m -> {
                final STypeComposite selectType = (STypeComposite) model.getObject().getType();
                final SType<?>       fieldType  = selectType.getField(field);
                final SOptionsConfig provider   = model.getObject().getOptionsConfig();
                final SInstance      instance   = provider.getValueFromKey(String.valueOf(m.getValue()));
                return Value.of(instance, (STypeSimple) fieldType);
            });
        }
    }

    private String getAdditionalSearchFieldLabel(String searchField) {
        STypeComposite selectType = (STypeComposite) model.getObject().getType();
        SType<?>       fieldType  = selectType.getField(searchField);
        if (!(fieldType instanceof STypeSimple)) {
            throw new SingularFormException(String.format("Search Fields must be a field of MTipoSimples! found: %s ", fieldType == null ? null : fieldType.getClass().getName()));
        }
        return fieldType.asAtrBasic().getLabel();
    }

    private void buildSearchField(String id, IModel<String> filterModel, BSGrid grid, Component table, Form<?> form) {
        BSControls formGroup = grid.newFormGroup();

        BSContainer inputGroup = new BSContainer(id + "inputGroup");
        formGroup.appendTag("div", true, "class=\"input-group input-group-sm\"", inputGroup);

        TextField<String> inputFiltro = new TextField<>("termo", filterModel);
        inputGroup.appendTag("input", true, "class=\"form-control\"", inputFiltro);

        BSContainer inputGroupButton = new BSContainer(id + "inputGroupButton");
        inputGroup.appendTag("span", true, "class=\"input-group-btn\"", inputGroupButton);
        inputGroupButton.appendTag("a", true, "class=\"btn default\"", new AjaxSubmitLink("link", form) {

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

    private SortableDataProvider<SelectOption, Void> buildDataProvider(
            IModel<? extends SInstance> model, final IModel<String> filtro) {
        SType<?>                 type    = model.getObject().getType();
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
            return value.contains(termo) || checkFilterAgainstAditionalFields(s, termo);
        }
        return true;
    }

    private boolean checkFilterAgainstAditionalFields(SelectOption s, String termo) {
        final SOptionsConfig miProvider = model.getObject().getOptionsConfig();
        final SInstance      si         = miProvider.getValueFromKey(String.valueOf(s.getValue()));

        if (SIComposite.class.isAssignableFrom(si.getClass())) {
            for (String field : view.searchFields()) {
                final Object value  = Value.of((SISimple<?>) ((SIComposite) si).getField(field));
                final String nValue = String.valueOf(value).toLowerCase();
                if (nValue.contains(termo)) {
                    return true;
                }
            }
        }

        return false;
    }

}
