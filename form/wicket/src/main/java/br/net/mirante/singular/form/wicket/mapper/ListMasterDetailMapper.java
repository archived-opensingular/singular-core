package br.net.mirante.singular.form.wicket.mapper;

import br.net.mirante.singular.form.mform.*;
import br.net.mirante.singular.form.mform.basic.ui.MPacoteBasic;
import br.net.mirante.singular.form.mform.basic.view.MListMasterDetailView;
import br.net.mirante.singular.form.mform.basic.view.MView;
import br.net.mirante.singular.form.wicket.IWicketComponentMapper;
import br.net.mirante.singular.form.wicket.UIBuilderWicket;
import br.net.mirante.singular.form.wicket.WicketBuildContext;
import br.net.mirante.singular.form.wicket.enums.ViewMode;
import br.net.mirante.singular.form.wicket.mapper.components.MetronicPanel;
import br.net.mirante.singular.form.wicket.model.MInstanceRootModel;
import br.net.mirante.singular.form.wicket.model.MInstanciaItemListaModel;
import br.net.mirante.singular.form.wicket.model.MTipoModel;
import br.net.mirante.singular.lambda.IConsumer;
import br.net.mirante.singular.util.wicket.ajax.ActionAjaxButton;
import br.net.mirante.singular.util.wicket.ajax.ActionAjaxLink;
import br.net.mirante.singular.util.wicket.bootstrap.layout.BSContainer;
import br.net.mirante.singular.util.wicket.datatable.BSDataTable;
import br.net.mirante.singular.util.wicket.datatable.BSDataTableBuilder;
import br.net.mirante.singular.util.wicket.datatable.BaseDataProvider;
import br.net.mirante.singular.util.wicket.datatable.column.BSActionPanel.ActionConfig;
import br.net.mirante.singular.util.wicket.modal.BSModalBorder;
import br.net.mirante.singular.util.wicket.modal.BSModalWindow;
import br.net.mirante.singular.util.wicket.resource.Icone;
import com.google.common.base.Strings;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import org.apache.wicket.Component;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;

import static br.net.mirante.singular.util.wicket.util.Shortcuts.$b;
import static br.net.mirante.singular.util.wicket.util.Shortcuts.$m;
import static org.apache.commons.lang3.StringUtils.trimToEmpty;

@SuppressWarnings("serial")
public class ListMasterDetailMapper implements IWicketComponentMapper {


    public void buildView(WicketBuildContext ctx) {

        final IModel<? extends MInstancia> model = ctx.getModel();
        final ViewMode viewMode = ctx.getViewMode();
        final MView view = ctx.getView();

        if (!(view instanceof MListMasterDetailView)) {
            throw new SingularFormException("Error: Mapper " +
                    ListMasterDetailMapper.class.getSimpleName() +
                    " must be associated with a view  of type" +
                    MListMasterDetailView.class.getName() +
                    ".");
        }

        final IModel<String> listaLabel = newLabelModel(model);


        BSContainer externalAtual = new BSContainer<>("externalContainerAtual");
        BSContainer externalIrmao = new BSContainer<>("externalContainerIrmao");

        ctx.getExternalContainer().appendTag("div", true, null, externalAtual);
        ctx.getExternalContainer().appendTag("div", true, null, externalIrmao);


        final MasterDetailModal modal = new MasterDetailModal("mods", model, listaLabel, ctx, viewMode, (MListMasterDetailView) view, externalIrmao, ctx.getUiBuilderWicket());

        externalAtual.appendTag("div", true, null, modal);

        ctx.getContainer().appendTag("div", true, null, new MetronicPanel("panel") {

            @Override
            protected void buildHeading(BSContainer<?> heading, Form<?> form) {
                heading.appendTag("span", new Label("_title", listaLabel));
                heading.add($b.visibleIf($m.get(() -> !Strings.isNullOrEmpty(listaLabel.getObject()))));
            }

            @Override
            protected void buildFooter(BSContainer<?> footer, Form<?> form) {
                if (viewMode.isEdition() && ((MListMasterDetailView) view).isNewElementEnabled()) {
                    appendAdicionarButton(footer, modal);
                }
            }

            @Override
            protected void buildContent(BSContainer<?> content, Form<?> form) {
                content.appendTag("table", true, null, id -> buildTable(id, model, (MListMasterDetailView) view, modal, ctx, viewMode));
            }
        });
    }


    /*
     * DATA TABLE
     */

    /**
     * @param model
     * @return
     */
    @SuppressWarnings("unchecked")
    private IModel<String> newLabelModel(IModel<? extends MInstancia> model) {
        IModel<MILista<MInstancia>> listaModel = $m.get(() -> (MILista<MInstancia>) model.getObject());
        MILista<?> iLista = listaModel.getObject();
        return $m.ofValue(trimToEmpty(iLista.as(MPacoteBasic.aspect()).getLabel()));
    }

    @SuppressWarnings("unchecked")
    private BSDataTable buildTable(String id, IModel<? extends MInstancia> model, MListMasterDetailView view, MasterDetailModal modal, WicketBuildContext ctx, ViewMode viewMode) {

        BSDataTableBuilder builder = new BSDataTableBuilder<>(newDataProvider(model)).withNoRecordsToolbar();

        configureColumns(view.getColumns(), builder, model, modal, ctx, viewMode, view);

        return builder.build(id);
    }

    @SuppressWarnings("unchecked")
    private BaseDataProvider newDataProvider(final IModel<? extends MInstancia> model) {
        return new BaseDataProvider() {

            @Override
            public Iterator iterator(int first, int count, Object sortProperty, boolean ascending) {
                return ((MILista<MInstancia>) model.getObject()).iterator();
            }

            @Override
            public long size() {
                return ((MILista<MInstancia>) model.getObject()).size();
            }

            @Override
            public IModel model(Object object) {
                IModel<MILista<MInstancia>> listaModel = $m.get(() -> (MILista<MInstancia>) model.getObject());
                return new MInstanciaItemListaModel<>(listaModel, listaModel.getObject().indexOf((MInstancia) object));
            }
        };
    }

    @SuppressWarnings("unchecked")
    private void configureColumns(
            Map<String, String> mapColumns,
            BSDataTableBuilder<MInstancia, ?, ?> builder,
            IModel<? extends MInstancia> model,
            MasterDetailModal modal,
            WicketBuildContext ctx,
            ViewMode viewMode,
            MListMasterDetailView view) {

        Map<MTipo, String> mapColumnsTipos = new LinkedHashMap<>();

        if (mapColumns.isEmpty()) {
            MTipo tipo = ((MILista) model.getObject()).getTipoElementos();
            if (tipo instanceof MTipoSimples) {
                mapColumnsTipos.put((MTipoSimples) tipo, null);
            }
            if (tipo instanceof MTipoComposto) {
                ((MTipoComposto) tipo)
                        .getFields()
                        .stream()
                        .filter(mtipo -> mtipo instanceof MTipoSimples)
                        .forEach(mtipo -> mapColumnsTipos.put((MTipoSimples) mtipo, null));

            }
        } else {
            mapColumns.forEach((key, value) -> mapColumnsTipos.put(model.getObject().getDicionario().getTipo(key), value));
        }

        for (Map.Entry<MTipo, String> entry : mapColumnsTipos.entrySet()) {

            IModel<String> labelModel;
            String label = entry.getValue();

            if (label != null) {
                labelModel = $m.ofValue(label);
            } else {
                labelModel = $m.ofValue((String) entry.getKey().getValorAtributo(MPacoteBasic.ATR_LABEL.getNomeCompleto()));
            }

            propertyColumnAppender(builder, labelModel, new MTipoModel(entry.getKey()));
        }

        actionColumnAppender(builder, model, modal, ctx, viewMode, view);


    }

    private void actionColumnAppender(BSDataTableBuilder<MInstancia, ?, ?> builder,
                                      IModel<? extends MInstancia> model,
                                      MasterDetailModal modal,
                                      WicketBuildContext ctx,
                                      ViewMode viewMode,
                                      MListMasterDetailView view) {

        builder.appendActionColumn($m.ofValue(""), actionColumn -> {
            if (viewMode.isEdition() && view.isDeleteElementsEnabled()) {
                actionColumn.appendAction(new ActionConfig().iconeModel(Model.of(Icone.MINUS)).buttonModel(Model.of("red")),
                (target, rowModel) -> {
                    MILista miLista = ((MILista) model.getObject());
                    miLista.remove(miLista.indexOf(rowModel.getObject()));
                    target.add(ctx.getContainer());
                });
            }

            Icone iconeBotaoAbrirModal = viewMode.isEdition() && view.isEditElementEnabled() ? Icone.PENCIL_SQUARE : Icone.EYE;

            actionColumn.appendAction(new ActionConfig().iconeModel(Model.of(iconeBotaoAbrirModal)).buttonModel(Model.of("blue-madison")),
                    (target, rowModel) -> {
                        modal.showExisting(target, rowModel, ctx);
                    }
            );
        });
    }

    /**
     * property column isolado em outro método para isolar o escopo de serialização do lambda do appendPropertyColumn
     */
    private void propertyColumnAppender(BSDataTableBuilder<MInstancia, ?, ?> builder, IModel<String> labelModel, IModel<MTipo<?>> mTipoModel) {
        builder.appendPropertyColumn(labelModel, o -> {
            MIComposto composto = (MIComposto) o;
            MTipoSimples mtipo = (MTipoSimples) mTipoModel.getObject();
            MISimples instancia = ((MISimples) composto.findDescendant(mtipo).get());
            return instancia.getValor();
        });
    }

    @SuppressWarnings("unchecked")
    protected void appendAdicionarButton(BSContainer<?> container, MasterDetailModal modal) {
        container
                .newTemplateTag(t -> ""
                                + "<button"
                                + " wicket:id='_add'"
                                + " class='btn btn-success btn-sm'"
                                + " style='padding:5px 3px 1px;margin-top:3px;margin-right:7px;'><i class='" + Icone.PLUS + "'></i>"
                                + "</button>"
                ).add(
                new AjaxLink("_add") {
                    @Override
                    public void onClick(AjaxRequestTarget target) {
                        modal.showNew(target);
                    }
                });
    }


    private static class MasterDetailModal extends BSModalWindow {

        private final IModel<MILista<MInstancia>> listModel;
        private final IModel<String> listaLabel;
        private final WicketBuildContext ctx;
        private final UIBuilderWicket wicketBuilder;
        private final Component table;
        private final ViewMode viewMode;
        private IModel<MInstancia> currentInstance;
        private IConsumer<AjaxRequestTarget> closeCallback;
        private MListMasterDetailView view;
        private BSContainer containerExterno;

        public MasterDetailModal(String id,
                                 IModel<? extends MInstancia> model,
                                 IModel<String> listaLabel,
                                 WicketBuildContext ctx,
                                 ViewMode viewMode,
                                 MListMasterDetailView view,
                                 BSContainer containerExterno,
                                 UIBuilderWicket wicketBuilder) {
            super(id, true, false);

            this.wicketBuilder = wicketBuilder;
            this.listaLabel = listaLabel;
            this.ctx = ctx;
            this.table = ctx.getContainer();
            this.viewMode = viewMode;
            this.view = view;
            this.listModel = $m.get(() -> (MILista<MInstancia>) model.getObject());
            this.containerExterno = containerExterno;

            this.setSize(BSModalBorder.Size.NORMAL);

            this.addButton(BSModalBorder.ButtonStyle.PRIMARY, $m.ofValue("OK"), new ActionAjaxButton("btn") {
                @Override
                protected void onAction(AjaxRequestTarget target, Form<?> form) {
                    target.add(table);
                    MasterDetailModal.this.hide(target);
                }
            });

            this.addLink(BSModalBorder.ButtonStyle.DANGER, $m.ofValue("Cancelar"), new ActionAjaxLink<Void>("btn-cancelar") {
                @Override
                protected void onAction(AjaxRequestTarget target) {
                    if (closeCallback != null) {
                        closeCallback.accept(target);
                    }
                    target.add(table);
                    MasterDetailModal.this.hide(target);
                }
            });

        }


        protected void showNew(AjaxRequestTarget target) {
            closeCallback = this::revert;
            currentInstance = new MInstanceRootModel<>();
            listModel.getObject().addNovo(instancia -> {
                currentInstance.setObject(instancia);
                MasterDetailModal.this.configureNewContent("Adicionar", target);

            });
        }

        protected void showExisting(AjaxRequestTarget target, IModel<MInstancia> forEdit, WicketBuildContext ctx) {
            String prefix = ctx.getViewMode().isEdition() ? "Editar" : "";
            closeCallback = null;
            currentInstance = forEdit;
            this.configureNewContent(prefix, target);
        }

        private void revert(AjaxRequestTarget target) {
            listModel.getObject().remove(listModel.getObject().size() - 1);
        }

        private void configureNewContent(String prefix, AjaxRequestTarget target) {
            this.setTitleText($m.ofValue((prefix + " " + listaLabel.getObject()).trim()));
            BSContainer modalBody = new BSContainer("bogoMips");
            this.setBody(modalBody);

            ViewMode viewModeModal = viewMode;
            if (!view.isEditElementEnabled()) {
                viewModeModal = ViewMode.VISUALIZATION;
            }

            wicketBuilder.build(new WicketBuildContext(ctx, modalBody, containerExterno, true, currentInstance), viewModeModal);
            target.add(ctx.getExternalContainer());
            target.add(containerExterno);
            this.show(target);
        }
    }


}
