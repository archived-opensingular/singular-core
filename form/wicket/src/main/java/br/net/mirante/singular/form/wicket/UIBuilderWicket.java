package br.net.mirante.singular.form.wicket;

import br.net.mirante.singular.form.mform.*;
import br.net.mirante.singular.form.mform.basic.view.*;
import br.net.mirante.singular.form.mform.context.UIBuilder;
import br.net.mirante.singular.form.mform.context.UIComponentMapper;
import br.net.mirante.singular.form.mform.core.*;
import br.net.mirante.singular.form.mform.core.attachment.MTipoAttachment;
import br.net.mirante.singular.form.mform.util.comuns.MTipoAnoMes;
import br.net.mirante.singular.form.wicket.enums.ViewMode;
import br.net.mirante.singular.form.wicket.mapper.*;
import br.net.mirante.singular.form.wicket.mapper.attachment.AttachmentMapper;
import br.net.mirante.singular.form.wicket.mapper.selection.*;

public class UIBuilderWicket implements UIBuilder<IWicketComponentMapper> {

    private final ViewMapperRegistry<IWicketComponentMapper> registry = newViewMapperRegistry();

    ViewMapperRegistry<IWicketComponentMapper> getViewMapperRegistry() {
        return registry;
    }

    public void build(WicketBuildContext ctx, ViewMode viewMode) {
        final IWicketComponentMapper mapper = resolveMapper(ctx.getCurrenttInstance());
        mapper.buildView(ctx.init(this, viewMode));
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
                .register(MTipoSimples.class,    MSelecaoPorSelectView.class,           SelectMapper::new)
                .register(MTipoComposto.class,   MSelecaoPorRadioView.class,            RadioMapper::new)
                .register(MTipoComposto.class,   MSelecaoPorSelectView.class,           SelectMapper::new)
                .register(MTipoComposto.class,   MSelecaoPorModalBuscaView.class,       SelectModalBuscaMapper::new)
                .register(MTipoBoolean.class,                                           BooleanMapper::new)
                .register(MTipoInteger.class,                                           IntegerMapper::new)
                .register(MTipoString.class,                                            StringMapper::new)
                .register(MTipoData.class,                                              DateMapper::new)
                .register(MTipoAnoMes.class,                                            YearMonthMapper::new)
                .register(MTipoAttachment.class,                                        AttachmentMapper::new)
                .register(MTipoString.class,     MSelecaoPorModalBuscaView.class,       SelectModalBuscaMapper::new)
                .register(MTipoLista.class,      MSelecaoMultiplaPorSelectView.class,   MultipleSelectBSMapper::new)
                .register(MTipoLista.class,      MSelecaoMultiplaPorCheckView.class,    MultipleCheckMapper::new)
                .register(MTipoLista.class,      MSelecaoMultiplaPorPicklistView.class, PicklistMapper::new)
                .register(MTipoComposto.class,                                          DefaultCompostoMapper::new)
                .register(MTipoComposto.class,   MTabView.class,                        DefaultCompostoMapper::new)
                .register(MTipoLista.class,                                             TableListaMapper::new)
                .register(MTipoLista.class,      MTableListaView.class,                 TableListaMapper::new)
                .register(MTipoLista.class,      MPanelListaView.class,                 PanelListaMapper::new)
                .register(MTipoLista.class,      MListMasterDetailView.class,           ListMasterDetailMapper::new)
                .register(MTipoString.class,     MTextAreaView.class,                   TextAreaMapper::new)
                .register(MTipoDecimal.class,                                           DecimalMapper::new)
                .register(MTipoMonetario.class,                                         MonetarioMapper::new);
        //@formatter:on
    }
}
