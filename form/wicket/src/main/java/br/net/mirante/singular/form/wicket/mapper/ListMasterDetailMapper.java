package br.net.mirante.singular.form.wicket.mapper;

import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;

import org.apache.wicket.Component;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.behavior.Behavior;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;

import com.google.common.base.Strings;

import br.net.mirante.singular.form.mform.SIComposite;
import br.net.mirante.singular.form.mform.SISimple;
import br.net.mirante.singular.form.mform.SInstance;
import br.net.mirante.singular.form.mform.SList;
import br.net.mirante.singular.form.mform.SType;
import br.net.mirante.singular.form.mform.STypeComposite;
import br.net.mirante.singular.form.mform.STypeSimple;
import br.net.mirante.singular.form.mform.SingularFormException;
import br.net.mirante.singular.form.mform.basic.ui.SPackageBasic;
import br.net.mirante.singular.form.mform.basic.view.MListMasterDetailView;
import br.net.mirante.singular.form.mform.basic.view.MView;
import br.net.mirante.singular.form.wicket.IWicketComponentMapper;
import br.net.mirante.singular.form.wicket.UIBuilderWicket;
import br.net.mirante.singular.form.wicket.WicketBuildContext;
import br.net.mirante.singular.form.wicket.component.BFModalWindow;
import br.net.mirante.singular.form.wicket.enums.ViewMode;
import br.net.mirante.singular.form.wicket.mapper.components.MetronicPanel;
import br.net.mirante.singular.form.wicket.model.MInstanceRootModel;
import br.net.mirante.singular.form.wicket.model.MTipoModel;
import br.net.mirante.singular.form.wicket.model.SInstanceItemListaModel;
import br.net.mirante.singular.lambda.IConsumer;
import br.net.mirante.singular.util.wicket.ajax.ActionAjaxButton;
import br.net.mirante.singular.util.wicket.ajax.ActionAjaxLink;
import br.net.mirante.singular.util.wicket.bootstrap.layout.BSContainer;
import br.net.mirante.singular.util.wicket.datatable.BSDataTable;
import br.net.mirante.singular.util.wicket.datatable.BSDataTableBuilder;
import br.net.mirante.singular.util.wicket.datatable.BaseDataProvider;
import br.net.mirante.singular.util.wicket.datatable.column.BSActionPanel.ActionConfig;
import br.net.mirante.singular.util.wicket.modal.BSModalBorder;
import br.net.mirante.singular.util.wicket.resource.Icone;
import static br.net.mirante.singular.util.wicket.util.Shortcuts.$b;
import static br.net.mirante.singular.util.wicket.util.Shortcuts.$m;
import static org.apache.commons.lang3.StringUtils.trimToEmpty;

@SuppressWarnings("serial")
public class ListMasterDetailMapper implements IWicketComponentMapper {


    public void buildView(WicketBuildContext ctx) {

        final IModel<? extends SInstance> model = ctx.getModel();
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
                if (viewMode.isEdition() && ((MListMasterDetailView) view).isNewElementEnabled()) {
                    appendAddButton(heading, modal);
                }
            }

            @Override
            protected void buildFooter(BSContainer<?> footer, Form<?> form) {
                footer.setVisible(false);
            }

            @Override
            protected void buildContent(BSContainer<?> content, Form<?> form) {
                content.appendTag("table", true, null, (id) -> {
                    BSDataTable bsDataTable = buildTable(id, model, (MListMasterDetailView) view, modal, ctx, viewMode);
                    bsDataTable.add(new Behavior() {
                        @Override
                        public void onConfigure(Component component) {
                            super.onConfigure(component);
                            if (ctx.getCurrentInstance() instanceof SList) {
                                component.setVisible(!((SList) ctx.getCurrentInstance()).isEmpty());
                            }
                        }
                    });
                    return bsDataTable;
                });
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
    private IModel<String> newLabelModel(IModel<? extends SInstance> model) {
        IModel<SList<SInstance>> listaModel = $m.get(() -> (SList<SInstance>) model.getObject());
        SList<?> iLista = listaModel.getObject();
        return $m.ofValue(trimToEmpty(iLista.as(SPackageBasic.aspect()).getLabel()));
    }

    @SuppressWarnings("unchecked")
    private BSDataTable buildTable(String id, IModel<? extends SInstance> model, MListMasterDetailView view, MasterDetailModal modal, WicketBuildContext ctx, ViewMode viewMode) {

        BSDataTableBuilder builder = new BSDataTableBuilder<>(newDataProvider(model)).withNoRecordsToolbar();

        configureColumns(view.getColumns(), builder, model, modal, ctx, viewMode, view);

        return builder.build(id);
    }

    @SuppressWarnings("unchecked")
    private BaseDataProvider newDataProvider(final IModel<? extends SInstance> model) {
        return new BaseDataProvider() {

            @Override
            public Iterator iterator(int first, int count, Object sortProperty, boolean ascending) {
                return ((SList<SInstance>) model.getObject()).iterator();
            }

            @Override
            public long size() {
                return ((SList<SInstance>) model.getObject()).size();
            }

            @Override
            public IModel model(Object object) {
                IModel<SList<SInstance>> listaModel = $m.get(() -> (SList<SInstance>) model.getObject());
                return new SInstanceItemListaModel<>(listaModel, listaModel.getObject().indexOf((SInstance) object));
            }
        };
    }

    @SuppressWarnings("unchecked")
    private void configureColumns(
            Map<String, String> mapColumns,
            BSDataTableBuilder<SInstance, ?, ?> builder,
            IModel<? extends SInstance> model,
            MasterDetailModal modal,
            WicketBuildContext ctx,
            ViewMode viewMode,
            MListMasterDetailView view) {

        Map<SType, String> mapColumnsTipos = new LinkedHashMap<>();

        if (mapColumns.isEmpty()) {
            SType tipo = ((SList) model.getObject()).getTipoElementos();
            if (tipo instanceof STypeSimple) {
                mapColumnsTipos.put((STypeSimple) tipo, null);
            }
            if (tipo instanceof STypeComposite) {
                ((STypeComposite) tipo)
                        .getFields()
                        .stream()
                        .filter(mtipo -> mtipo instanceof STypeSimple)
                        .forEach(mtipo -> mapColumnsTipos.put((STypeSimple) mtipo, null));

            }
        } else {
            mapColumns.forEach((key, value) -> mapColumnsTipos.put(model.getObject().getDicionario().getTipo(key), value));
        }

        for (Map.Entry<SType, String> entry : mapColumnsTipos.entrySet()) {

            IModel<String> labelModel;
            String label = entry.getValue();

            if (label != null) {
                labelModel = $m.ofValue(label);
            } else {
                labelModel = $m.ofValue((String) entry.getKey().getValorAtributo(SPackageBasic.ATR_LABEL.getNomeCompleto()));
            }

            propertyColumnAppender(builder, labelModel, new MTipoModel(entry.getKey()));
        }

        actionColumnAppender(builder, model, modal, ctx, viewMode, view);


    }

    private void actionColumnAppender(BSDataTableBuilder<SInstance, ?, ?> builder, IModel<? extends SInstance> model,
                                      MasterDetailModal modal, WicketBuildContext ctx, ViewMode viewMode,
                                      MListMasterDetailView view) {
        builder.appendActionColumn($m.ofValue(""), actionColumn -> {
            if (viewMode.isEdition() && view.isDeleteElementsEnabled()) {
                actionColumn.appendAction(
                        new ActionConfig()
                                .iconeModel(Model.of(Icone.MINUS))
                                .buttonModel(Model.of("red"))
                                .style($m.ofValue("padding:5px 3px 1px;")),
                        (target, rowModel) -> {
                            SList sList = ((SList) model.getObject());
                            sList.remove(sList.indexOf(rowModel.getObject()));
                            target.add(ctx.getContainer());
                        });
            }
            final Icone openModalIcon = viewMode.isEdition() && view.isEditElementEnabled() ? Icone.PENCIL_SQUARE : Icone.EYE;
            actionColumn.appendAction(
                    new ActionConfig()
                            .iconeModel(Model.of(openModalIcon))
                            .buttonModel(Model.of("blue-madison"))
                            .style($m.ofValue("padding:5px 3px 1px;")),
                    (target, rowModel) -> {
                        modal.showExisting(target, rowModel, ctx);
                    }
            );
        });
    }

    /**
     * property column isolado em outro método para isolar o escopo de serialização do lambda do appendPropertyColumn
     */
    private void propertyColumnAppender(BSDataTableBuilder<SInstance, ?, ?> builder, IModel<String> labelModel, IModel<SType<?>> mTipoModel) {
        builder.appendPropertyColumn(labelModel, o -> {
            SIComposite composto = (SIComposite) o;
            STypeSimple mtipo = (STypeSimple) mTipoModel.getObject();
            SISimple instancia = ((SISimple) composto.findDescendant(mtipo).get());
            return instancia.getDisplayString();
        });
    }

    @SuppressWarnings("unchecked")
    protected void appendAddButton(BSContainer<?> container, MasterDetailModal modal) {
        container
                .newTemplateTag(t -> ""
                                + "<button"
                                + " wicket:id='_add'"
                                + " class='btn btn-success btn-sm pull-right'"
                                + " style='padding:5px 3px 1px;'><i class='" + Icone.PLUS + "'></i>"
                                + "</button>"
                ).add(
                new AjaxLink("_add") {
                    @Override
                    public void onClick(AjaxRequestTarget target) {
                        modal.showNew(target);
                    }
                });
    }


    private static class MasterDetailModal extends BFModalWindow {

        private final IModel<SList<SInstance>> listModel;
        private final IModel<String> listaLabel;
        private final WicketBuildContext ctx;
        private final UIBuilderWicket wicketBuilder;
        private final Component table;
        private final ViewMode viewMode;
        private IModel<SInstance> currentInstance;
        private IConsumer<AjaxRequestTarget> closeCallback;
        private MListMasterDetailView view;
        private BSContainer containerExterno;

        public MasterDetailModal(String id,
                                 IModel<? extends SInstance> model,
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
            this.listModel = $m.get(() -> (SList<SInstance>) model.getObject());
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

        protected void showExisting(AjaxRequestTarget target, IModel<SInstance> forEdit, WicketBuildContext ctx) {
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

        @Override
        public void show(AjaxRequestTarget target) {
            super.show(target);
            target.appendJavaScript(getConfigureBackdropScript());
        }

        private String getConfigureBackdropScript() {
            String js = "";
            js += " (function (zindex){ ";
            js += "     $('.modal-backdrop').each(function(index) { ";
            js += "         var zIndex = $(this).css('z-index'); ";
            js += "         $(this).css('z-index', zindex-1+index); ";
            js += "     }); ";
            js += "     $('.modal').each(function(index) { ";
            js += "         var zIndex = $(this).css('z-index'); ";
            js += "         $(this).css('z-index', zindex+index); ";
            js += "     }); ";
            js += " })(10050); ";
            return js;
        }

    }


}
