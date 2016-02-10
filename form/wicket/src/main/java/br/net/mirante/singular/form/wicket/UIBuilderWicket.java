package br.net.mirante.singular.form.wicket;

import java.util.Collection;
import java.util.Optional;
import java.util.logging.Logger;

import org.apache.wicket.Component;
import org.apache.wicket.markup.repeater.RepeatingView;
import org.apache.wicket.model.IModel;

import br.net.mirante.singular.form.mform.SIComposite;
import br.net.mirante.singular.form.mform.SInstance;
import br.net.mirante.singular.form.mform.STypeComposite;
import br.net.mirante.singular.form.mform.STypeLista;
import br.net.mirante.singular.form.mform.STypeSimple;
import br.net.mirante.singular.form.mform.SingularFormException;
import br.net.mirante.singular.form.mform.basic.view.MBooleanRadioView;
import br.net.mirante.singular.form.mform.basic.view.MDateTimerView;
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
import br.net.mirante.singular.form.mform.core.STypeBoolean;
import br.net.mirante.singular.form.mform.core.STypeData;
import br.net.mirante.singular.form.mform.core.STypeDataHora;
import br.net.mirante.singular.form.mform.core.STypeDecimal;
import br.net.mirante.singular.form.mform.core.STypeInteger;
import br.net.mirante.singular.form.mform.core.STypeLatitudeLongitude;
import br.net.mirante.singular.form.mform.core.STypeMonetario;
import br.net.mirante.singular.form.mform.core.STypeString;
import br.net.mirante.singular.form.mform.core.annotation.AtrAnnotation;
import br.net.mirante.singular.form.mform.core.attachment.STypeAttachment;
import br.net.mirante.singular.form.mform.util.comuns.STypeAnoMes;
import br.net.mirante.singular.form.mform.util.comuns.STypeTelefoneNacional;
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
import br.net.mirante.singular.form.wicket.mapper.TableListMapper;
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

public class UIBuilderWicket implements UIBuilder<IWicketComponentMapper> {

    private final ViewMapperRegistry<IWicketComponentMapper> registry = newViewMapperRegistry();

    ViewMapperRegistry<IWicketComponentMapper> getViewMapperRegistry() {
        return registry;
    }

    public void build(WicketBuildContext ctx, ViewMode viewMode) {
        final IWicketComponentMapper mapper = resolveMapper(ctx.getCurrentInstance());

        if(ctx.isRootContext() && ctx.isAnnotationEnabled()){ //TODO: Fabs: Check is is annotation enabled
            ctx.init(this, viewMode);

            new AnnotationBuilder(this).build(ctx, viewMode, mapper);
        }else{
            mapper.buildView(ctx.init(this, viewMode));
        }

    }

    private IWicketComponentMapper resolveMapper(SInstance instancia) {

        final UIComponentMapper customMapper = instancia.getMTipo().getCustomMapper();
        final MView view = ViewResolver.resolve(instancia);

        if (customMapper != null) {
            if (customMapper instanceof IWicketComponentMapper) {
                return (IWicketComponentMapper) customMapper;
            } else {
                throw new SingularFormException("Para utilizar custom mapper com Wicket, é necessario " + customMapper.getClass().getName()
                        + " implementar IWicketComponentMapper", instancia);
            }
        } else {
            return getViewMapperRegistry().getMapper(instancia, view).orElseThrow(
                    () -> new SingularFormException("Não há mappeamento de componente Wicket para o tipo", instancia, "view=" + view));
        }
    }

    protected ViewMapperRegistry<IWicketComponentMapper> newViewMapperRegistry() {
        //@formatter:off
        return new ViewMapperRegistry<IWicketComponentMapper>()
                .register(STypeSimple.class,    MSelecaoPorRadioView.class,            RadioMapper::new)
                .register(STypeSimple.class,    MSelecaoPorSelectView.class, SelectMapper::new)
                .register(STypeBoolean.class,                                           BooleanMapper::new)
                .register(STypeBoolean.class,    MBooleanRadioView.class,               BooleanRadioMapper::new)
                .register(STypeInteger.class,                                           IntegerMapper::new)
                .register(STypeString.class,                                            StringMapper::new)
                .register(STypeString.class,     MSelecaoPorModalBuscaView.class,       SelectModalBuscaMapper::new)
                .register(STypeString.class,     MTextAreaView.class,                   TextAreaMapper::new)
                .register(STypeData.class,                                              DateMapper::new)
                .register(STypeAnoMes.class,                                            YearMonthMapper::new)
                .register(STypeDecimal.class,                                           DecimalMapper::new)
                .register(STypeMonetario.class,                                         MonetarioMapper::new)
                .register(STypeAttachment.class,                                        AttachmentMapper::new)
                .register(STypeLatitudeLongitude.class,                                 LatitudeLongitudeMapper::new)
                .register(STypeComposite.class,                                          DefaultCompostoMapper::new)
                .register(STypeComposite.class,   MTabView.class,                        TabMapper::new)
                .register(STypeComposite.class,   MSelecaoPorRadioView.class,            RadioMapper::new)
                .register(STypeComposite.class,   MSelecaoPorSelectView.class,           SelectMapper::new)
                .register(STypeComposite.class,   MSelecaoPorModalBuscaView.class,       SelectModalBuscaMapper::new)
                .register(STypeLista.class,      MSelecaoMultiplaPorSelectView.class,   MultipleSelectBSMapper::new)
                .register(STypeLista.class,      MSelecaoMultiplaPorCheckView.class,    MultipleCheckMapper::new)
                .register(STypeLista.class,      MSelecaoMultiplaPorPicklistView.class, PicklistMapper::new)
                .register(STypeLista.class,                                             TableListMapper::new)
                .register(STypeLista.class,      MTableListaView.class,                 TableListMapper::new)
                .register(STypeLista.class,      MPanelListaView.class,                 PanelListaMapper::new)
                .register(STypeLista.class,      MListMasterDetailView.class,           ListMasterDetailMapper::new)
                .register(STypeDataHora.class,                                          DateTimeMapper::new)
                .register(STypeDataHora.class,   MDateTimerView.class,                  DateTimeMapper::new)
                .register(STypeTelefoneNacional.class,                                  TelefoneNacionalMapper::new);
        //@formatter:on
    }
}

class AnnotationBuilder {
    private static final Logger LOGGER = Logger.getLogger(AnnotationBuilder.class.getName());

    private UIBuilderWicket parent;

    AnnotationBuilder(UIBuilderWicket parent){
        this.parent = parent;
    }

    public void build(WicketBuildContext ctx, ViewMode viewMode, IWicketComponentMapper mapper) {
        final BSContainer<?> parentCol = ctx.getContainer();
        BSRow superRow = parentCol.newGrid().newRow();

        WicketBuildContext mainCtx = createMainColumn(ctx, superRow);
        executeMainMapper(viewMode, mapper, mainCtx);

        addAnnotationsFor(ctx, createAnnotationColumn(superRow), (SInstance) ctx.getCurrentInstance());
    }

    private void executeMainMapper(ViewMode viewMode, IWicketComponentMapper mapper, WicketBuildContext mainCtx) {
        mapper.buildView(mainCtx.init(parent, viewMode));
//        mainCtx.configure(mapper);
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

    private void addAnnotationsFor(WicketBuildContext ctx, BSGrid ngrid, SInstance instance) {
        if(instance.as(AtrAnnotation::new).isAnnotated()){
            BSContainer rootContainer = ctx.getRootContainer();
            Optional<Component> referenced = find(rootContainer.getItems(), instance);
            addAnnotationComponent(ngrid, instance, referenced, ctx);
        }
        if(instance instanceof SIComposite){
            addAnnotationsFor(ctx, ngrid, ((SIComposite) instance).getAllFields());
        }
    }

    private void addAnnotationComponent(BSGrid ngrid, SInstance instance,
                                        Optional<Component> targetComponent, WicketBuildContext ctx) {
        if(targetComponent.isPresent()){
            ngrid.newRow().appendTag("div", true, "",
                    (id) -> {
                        return new AnnotationComponent(id, modelFor(instance),
                                targetComponent.get(), ctx);
                    });
            ;
        }else{
            LOGGER.warning("Not possible to render Annotation Component since Target Component was not found.");
        }
    }

    private Optional<Component> find(RepeatingView children, final SInstance target) {
        final Optional<Component>[] result = new Optional[]{Optional.empty()};
        children.visitChildren((x, y) -> {
            IModel<?> m = x.getDefaultModel();
            if(m != null && m.getObject() == target){
                result[0] = Optional.of(x);
            }
            if(!result[0].isPresent() && x instanceof BSContainer){
                result[0] = find(((BSContainer) x).getItems(), target);
            }
        });
        return result[0];
    }

    private MInstanceRootModel<SInstance> modelFor(SInstance instance) {
        MInstanceRootModel<SInstance> model = new MInstanceRootModel<>();
        model.setObject(instance);
        return model;
    }

    private void addAnnotationsFor(WicketBuildContext ctx, BSGrid ngrid, Collection<SInstance> children) {
        for(SInstance field: children){
            addAnnotationsFor(ctx, ngrid, field);
        }
    }

}