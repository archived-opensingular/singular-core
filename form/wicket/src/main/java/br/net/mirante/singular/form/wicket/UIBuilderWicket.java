package br.net.mirante.singular.form.wicket;

import br.net.mirante.singular.form.mform.*;
import br.net.mirante.singular.form.mform.basic.view.MAnnotationView;
import br.net.mirante.singular.form.mform.basic.view.MBooleanRadioView;
import br.net.mirante.singular.form.mform.basic.view.MListMasterDetailView;
import br.net.mirante.singular.form.mform.basic.view.MPanelListaView;
import br.net.mirante.singular.form.mform.basic.view.MSelecaoMultiplaPorCheckView;
import br.net.mirante.singular.form.mform.basic.view.MSelecaoMultiplaPorPicklistView;
import br.net.mirante.singular.form.mform.basic.view.MSelecaoMultiplaPorSelectView;
import br.net.mirante.singular.form.mform.basic.view.MSelecaoPorModalBuscaView;
import br.net.mirante.singular.form.mform.basic.view.MSelecaoPorRadioView;
import br.net.mirante.singular.form.mform.basic.view.MSelecaoPorSelectView;
import br.net.mirante.singular.form.mform.basic.view.MTabView;
import br.net.mirante.singular.form.mform.basic.view.MTableListaView;
import br.net.mirante.singular.form.mform.basic.view.MTextAreaView;
import br.net.mirante.singular.form.mform.basic.view.MView;
import br.net.mirante.singular.form.mform.basic.view.ViewMapperRegistry;
import br.net.mirante.singular.form.mform.basic.view.ViewResolver;
import br.net.mirante.singular.form.mform.context.UIBuilder;
import br.net.mirante.singular.form.mform.context.UIComponentMapper;
import br.net.mirante.singular.form.mform.core.MTipoBoolean;
import br.net.mirante.singular.form.mform.core.MTipoData;
import br.net.mirante.singular.form.mform.core.MTipoDataHora;
import br.net.mirante.singular.form.mform.core.MTipoDecimal;
import br.net.mirante.singular.form.mform.core.MTipoInteger;
import br.net.mirante.singular.form.mform.core.MTipoLatitudeLongitude;
import br.net.mirante.singular.form.mform.core.MTipoMonetario;
import br.net.mirante.singular.form.mform.core.MTipoString;
import br.net.mirante.singular.form.mform.core.attachment.MTipoAttachment;
import br.net.mirante.singular.form.mform.util.comuns.MTipoAnoMes;
import br.net.mirante.singular.form.mform.util.comuns.MTipoTelefoneNacional;
import br.net.mirante.singular.form.wicket.enums.ViewMode;
import br.net.mirante.singular.form.wicket.mapper.BooleanMapper;
import br.net.mirante.singular.form.wicket.mapper.DateMapper;
import br.net.mirante.singular.form.wicket.mapper.DateTimeMapper;
import br.net.mirante.singular.form.wicket.mapper.DecimalMapper;
import br.net.mirante.singular.form.wicket.mapper.DefaultCompostoMapper;
import br.net.mirante.singular.form.wicket.mapper.IntegerMapper;
import br.net.mirante.singular.form.wicket.mapper.LatitudeLongitudeMapper;
import br.net.mirante.singular.form.wicket.mapper.ListMasterDetailMapper;
import br.net.mirante.singular.form.wicket.mapper.MonetarioMapper;
import br.net.mirante.singular.form.wicket.mapper.PanelListaMapper;
import br.net.mirante.singular.form.wicket.mapper.StringMapper;
import br.net.mirante.singular.form.wicket.mapper.TabMapper;
import br.net.mirante.singular.form.wicket.mapper.TableListaMapper;
import br.net.mirante.singular.form.wicket.mapper.TelefoneNacionalMapper;
import br.net.mirante.singular.form.wicket.mapper.TextAreaMapper;
import br.net.mirante.singular.form.wicket.mapper.YearMonthMapper;
import br.net.mirante.singular.form.wicket.mapper.annotation.AnnotationComponent;
import br.net.mirante.singular.form.wicket.mapper.attachment.AttachmentMapper;
import br.net.mirante.singular.form.wicket.mapper.selection.BooleanRadioMapper;
import br.net.mirante.singular.form.wicket.mapper.selection.MultipleCheckMapper;
import br.net.mirante.singular.form.wicket.mapper.selection.MultipleSelectBSMapper;
import br.net.mirante.singular.form.wicket.mapper.selection.PicklistMapper;
import br.net.mirante.singular.form.wicket.mapper.selection.RadioMapper;
import br.net.mirante.singular.form.wicket.mapper.selection.SelectMapper;
import br.net.mirante.singular.form.wicket.mapper.selection.SelectModalBuscaMapper;
import br.net.mirante.singular.form.wicket.model.MInstanceRootModel;
import br.net.mirante.singular.util.wicket.bootstrap.layout.BSCol;
import br.net.mirante.singular.util.wicket.bootstrap.layout.BSContainer;
import br.net.mirante.singular.util.wicket.bootstrap.layout.BSGrid;
import br.net.mirante.singular.util.wicket.bootstrap.layout.BSRow;

import java.util.Collection;

public class UIBuilderWicket implements UIBuilder<IWicketComponentMapper> {

    private final ViewMapperRegistry<IWicketComponentMapper> registry = newViewMapperRegistry();

    ViewMapperRegistry<IWicketComponentMapper> getViewMapperRegistry() {
        return registry;
    }

    public void build(WicketBuildContext ctx, ViewMode viewMode) {
        final IWicketComponentMapper mapper = resolveMapper(ctx.getCurrenttInstance());

        if(ctx.isRootContext()){ //TODO: Fabs: Check is is annotation enabled
            ctx.init(this, viewMode);

            new AnnotationBuilder(this).build(ctx, viewMode, mapper);
        }else{
            mapper.buildView(ctx.init(this, viewMode));
            ctx.configure(mapper);
        }

    }

    private IWicketComponentMapper resolveMapper(MInstancia instancia) {

        final UIComponentMapper customMapper = instancia.getMTipo().getCustomMapper();
        final MView view = ViewResolver.resolve(instancia);

        if (customMapper != null) {
            if (customMapper instanceof IWicketComponentMapper) {
                return (IWicketComponentMapper) customMapper;
            } else {
                throw new SingularFormException("Para utilizar custom mapper com Wicket, é necessario implementar IWicketComponentMapper");
            }
        } else {
            return getViewMapperRegistry().getMapper(instancia, view)
                .orElseThrow(() -> createErro(instancia, view, "Não há mappeamento de componente Wicket para o tipo"));
        }

    }

    private SingularFormException createErro(MInstancia instancia, MView view, String msg) {
        return new SingularFormException(
            msg + " (instancia=" + instancia.getPathFull()
                + ", tipo=" + instancia.getMTipo().getNome()
                + ", classeInstancia=" + instancia.getClass()
                + ", tipo=" + instancia.getMTipo()
                + ", view=" + view
                + ")");
    }

    protected ViewMapperRegistry<IWicketComponentMapper> newViewMapperRegistry() {
        //@formatter:off
        return new ViewMapperRegistry<IWicketComponentMapper>()
                .register(MTipoSimples.class,    MSelecaoPorRadioView.class,            RadioMapper::new)
                .register(MTipoSimples.class,    MSelecaoPorSelectView.class, SelectMapper::new)
                .register(MTipoBoolean.class,                                           BooleanMapper::new)
                .register(MTipoBoolean.class,    MBooleanRadioView.class,               BooleanRadioMapper::new)
                .register(MTipoInteger.class,                                           IntegerMapper::new)
                .register(MTipoString.class,                                            StringMapper::new)
                .register(MTipoString.class,     MSelecaoPorModalBuscaView.class,       SelectModalBuscaMapper::new)
                .register(MTipoString.class,     MTextAreaView.class,                   TextAreaMapper::new)
                .register(MTipoData.class,                                              DateMapper::new)
                .register(MTipoAnoMes.class,                                            YearMonthMapper::new)
                .register(MTipoDecimal.class,                                           DecimalMapper::new)
                .register(MTipoMonetario.class,                                         MonetarioMapper::new)
                .register(MTipoAttachment.class,                                        AttachmentMapper::new)
                .register(MTipoLatitudeLongitude.class,                                 LatitudeLongitudeMapper::new)
                .register(MTipoComposto.class,                                          DefaultCompostoMapper::new)
                .register(MTipoComposto.class,   MTabView.class,                        TabMapper::new)
                .register(MTipoComposto.class,   MSelecaoPorRadioView.class,            RadioMapper::new)
                .register(MTipoComposto.class,   MSelecaoPorSelectView.class,           SelectMapper::new)
                .register(MTipoComposto.class,   MSelecaoPorModalBuscaView.class,       SelectModalBuscaMapper::new)
                .register(MTipoLista.class,      MSelecaoMultiplaPorSelectView.class,   MultipleSelectBSMapper::new)
                .register(MTipoLista.class,      MSelecaoMultiplaPorCheckView.class,    MultipleCheckMapper::new)
                .register(MTipoLista.class,      MSelecaoMultiplaPorPicklistView.class, PicklistMapper::new)
                .register(MTipoLista.class,                                             TableListaMapper::new)
                .register(MTipoLista.class,      MTableListaView.class,                 TableListaMapper::new)
                .register(MTipoLista.class,      MPanelListaView.class,                 PanelListaMapper::new)
                .register(MTipoLista.class,      MListMasterDetailView.class,           ListMasterDetailMapper::new)
                .register(MTipoDataHora.class,                                          DateTimeMapper::new)
                .register(MTipoTelefoneNacional.class,                                  TelefoneNacionalMapper::new);
        //@formatter:on
    }
}

class AnnotationBuilder {

    private UIBuilderWicket parent;

    AnnotationBuilder(UIBuilderWicket parent){
        this.parent = parent;
    }

    public void build(WicketBuildContext ctx, ViewMode viewMode, IWicketComponentMapper mapper) {
        final BSContainer<?> parentCol = ctx.getContainer();
        BSRow superRow = parentCol.newGrid().newRow();

        WicketBuildContext mainCtx = createMainColumn(ctx, superRow);
        executeMainMapper(viewMode, mapper, mainCtx);

        addAnnotationsFor(ctx, createAnnotationColumn(superRow), (MInstancia) ctx.getCurrenttInstance());
    }

    private void executeMainMapper(ViewMode viewMode, IWicketComponentMapper mapper, WicketBuildContext mainCtx) {
        mapper.buildView(mainCtx.init(parent, viewMode));
        mainCtx.configure(mapper);
    }

    private WicketBuildContext createMainColumn(WicketBuildContext ctx, BSRow superRow) {
        BSCol supercol = superRow.newCol(9).setCssClass("col-sm-9");
        final BSGrid formGrid = supercol.newGrid();
        return new WicketBuildContext(ctx, formGrid, ctx.getExternalContainer(),
                false, ctx.getModel());
    }

    private BSGrid createAnnotationColumn(BSRow superRow) {
        return superRow.newCol(3).setCssClass("col-sm-3 .hidden-xs").newGrid();
    }

    private void addAnnotationsFor(WicketBuildContext ctx, BSGrid ngrid, MInstancia instance) {
        if(instance.getMTipo().getView() instanceof MAnnotationView){
            addAnnotationComponent(ngrid, instance);
        }
        if(instance instanceof MIComposto){
            addAnnotationsFor(ctx, ngrid, ((MIComposto) instance).getAllFields());
        }
    }

    private void addAnnotationComponent(BSGrid ngrid, MInstancia instance) {
        ngrid.newRow().appendTag("div", true, "style=\"float: left;\"",
                (id) -> {
                    return new AnnotationComponent(id, view(instance), modelFor(instance));
                });
        ;
    }

    private MAnnotationView view(MInstancia instance) {
        return (MAnnotationView) instance.getMTipo().getView();
    }

    private MInstanceRootModel<MInstancia> modelFor(MInstancia instance) {
        MInstanceRootModel<MInstancia> model = new MInstanceRootModel<>();
        model.setObject(instance);
        return model;
    }

    private void addAnnotationsFor(WicketBuildContext ctx, BSGrid ngrid, Collection<MInstancia> children) {
        for(MInstancia field: children){
            addAnnotationsFor(ctx, ngrid, field);
        }
    }

}