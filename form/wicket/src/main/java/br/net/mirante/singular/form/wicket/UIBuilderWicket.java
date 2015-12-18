package br.net.mirante.singular.form.wicket;

import br.net.mirante.singular.form.mform.*;
import br.net.mirante.singular.form.mform.basic.view.*;
import br.net.mirante.singular.form.mform.context.UIBuilder;
import br.net.mirante.singular.form.mform.context.UIComponentMapper;
import br.net.mirante.singular.form.mform.core.*;
import br.net.mirante.singular.form.mform.core.attachment.MTipoAttachment;
import br.net.mirante.singular.form.mform.options.MTipoSelectItem;
import br.net.mirante.singular.form.mform.util.comuns.MTipoAnoMes;
import br.net.mirante.singular.form.wicket.enums.ViewMode;
import br.net.mirante.singular.form.wicket.mapper.*;
import br.net.mirante.singular.form.wicket.mapper.attachment.AttachmentMapper;
import br.net.mirante.singular.form.wicket.mapper.selection.*;
import org.apache.wicket.model.IModel;

public class UIBuilderWicket implements UIBuilder<IWicketComponentMapper> {

    private final ViewMapperRegistry<IWicketComponentMapper> MAPPERS = new ViewMapperRegistry<>();

    {
        //@formatter:off
        MAPPERS.register(MTipoSimples.class,    MSelecaoPorRadioView.class,            RadioMapper::new);
        MAPPERS.register(MTipoSimples.class,    MSelecaoPorSelectView.class,           SelectMapper::new);
        MAPPERS.register(MTipoSelectItem.class, MSelecaoPorRadioView.class,            RadioMapper::new);
        MAPPERS.register(MTipoSelectItem.class, MSelecaoPorSelectView.class,           SelectBSMapper::new);
        MAPPERS.register(MTipoSelectItem.class, MSelecaoPorModalBuscaView.class,       SelectModalBuscaMapper::new);
        MAPPERS.register(MTipoBoolean.class,                                           BooleanMapper::new);
        MAPPERS.register(MTipoInteger.class,                                           IntegerMapper::new);
        MAPPERS.register(MTipoString.class,                                            StringMapper::new);
        MAPPERS.register(MTipoData.class,                                              DateMapper::new);
        MAPPERS.register(MTipoAnoMes.class,                                            YearMonthMapper::new);
        MAPPERS.register(MTipoAttachment.class,                                        AttachmentMapper::new);
        MAPPERS.register(MTipoString.class,     MSelecaoPorModalBuscaView.class,       SelectModalBuscaMapper::new);
        MAPPERS.register(MTipoLista.class,      MSelecaoMultiplaPorSelectView.class,   MultipleSelectBSMapper::new);
        MAPPERS.register(MTipoLista.class,      MSelecaoMultiplaPorCheckView.class,    MultipleCheckMapper::new);
        MAPPERS.register(MTipoLista.class,      MSelecaoMultiplaPorPicklistView.class, PicklistMapper::new);
        MAPPERS.register(MTipoComposto.class,                                          DefaultCompostoMapper::new);
        MAPPERS.register(MTipoComposto.class,   MTabView.class,                        DefaultCompostoMapper::new);
        MAPPERS.register(MTipoLista.class,                                             TableListaMapper::new);
        MAPPERS.register(MTipoLista.class,      MTableListaView.class,                 TableListaMapper::new);
        MAPPERS.register(MTipoLista.class,      MPanelListaView.class,                 PanelListaMapper::new);
        MAPPERS.register(MTipoLista.class,      MListMasterDetailView.class,           ListMasterDetailMapper::new);
        MAPPERS.register(MTipoString.class,     MTextAreaView.class,                   TextAreaMapper::new);
        MAPPERS.register(MTipoDecimal.class,                                           DecimalMapper::new);
        MAPPERS.register(MTipoMonetario.class,                                         MonetarioMapper::new);
        //@formatter:on
    }

    UIBuilderWicket() {
    }

    ViewMapperRegistry<IWicketComponentMapper> getMAPPERS() {
        return MAPPERS;
    }

    public void buildForEdit(WicketBuildContext ctx, IModel<? extends MInstancia> model) {
        build(ctx, model, ViewMode.EDITION);
    }

    public void buildForView(WicketBuildContext ctx, IModel<? extends MInstancia> model) {
        build(ctx, model, ViewMode.VISUALIZATION);
    }

    public void build(WicketBuildContext ctx, IModel<? extends MInstancia> model, ViewMode viewMode) {
        Object obj = model.getObject();
        MInstancia instancia = (MInstancia) obj;
        MView view = ViewResolver.resolve(instancia);

        ctx.init(model);

        final IWicketComponentMapper mapper;
        final UIComponentMapper customMapper = instancia.getMTipo().getCustomMapper();

        if (customMapper != null) {
            if (customMapper instanceof IWicketComponentMapper) {
                mapper = (IWicketComponentMapper) customMapper;
            } else {
                throw new SingularFormException("Para utilizar custom mapper com Wicket, é necessario implementar IWicketComponentMapper");
            }
        } else {
            mapper = MAPPERS.getMapper(instancia, view)
                    .orElseThrow(() -> createErro(instancia, view, "Não há mappeamento de componente Wicket para o tipo"));
        }

        mapper.buildView(this, ctx, view, model, viewMode);
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
}
