package br.net.mirante.singular.form.wicket;

import br.net.mirante.singular.form.mform.MInstancia;
import br.net.mirante.singular.form.mform.MTipoComposto;
import br.net.mirante.singular.form.mform.MTipoLista;
import br.net.mirante.singular.form.mform.MTipoSimples;
import br.net.mirante.singular.form.mform.SingularFormException;
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
import br.net.mirante.singular.form.wicket.mapper.annotation.AnnotationDefaultMapper;
import br.net.mirante.singular.form.wicket.mapper.attachment.AttachmentMapper;
import br.net.mirante.singular.form.wicket.mapper.selection.BooleanRadioMapper;
import br.net.mirante.singular.form.wicket.mapper.selection.MultipleCheckMapper;
import br.net.mirante.singular.form.wicket.mapper.selection.MultipleSelectBSMapper;
import br.net.mirante.singular.form.wicket.mapper.selection.PicklistMapper;
import br.net.mirante.singular.form.wicket.mapper.selection.RadioMapper;
import br.net.mirante.singular.form.wicket.mapper.selection.SelectMapper;
import br.net.mirante.singular.form.wicket.mapper.selection.SelectModalBuscaMapper;

public class UIBuilderWicket implements UIBuilder<IWicketComponentMapper> {

    private final ViewMapperRegistry<IWicketComponentMapper> registry = newViewMapperRegistry();

    ViewMapperRegistry<IWicketComponentMapper> getViewMapperRegistry() {
        return registry;
    }

    public void build(WicketBuildContext ctx, ViewMode viewMode) {
        final IWicketComponentMapper mapper = resolveMapper(ctx.getCurrenttInstance());
        mapper.buildView(ctx.init(this, viewMode));
        ctx.configure(mapper);
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
                .register(MTipoComposto.class,   MAnnotationView.class,                 AnnotationDefaultMapper::new)
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
