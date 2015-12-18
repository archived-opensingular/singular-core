package br.net.mirante.singular.form.wicket;

import br.net.mirante.singular.form.mform.MInstancia;
import br.net.mirante.singular.form.mform.MTipoComposto;
import br.net.mirante.singular.form.mform.MTipoLista;
import br.net.mirante.singular.form.mform.MTipoSimples;
import br.net.mirante.singular.form.mform.SingularFormException;
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
import br.net.mirante.singular.form.mform.core.MTipoBoolean;
import br.net.mirante.singular.form.mform.core.MTipoData;
import br.net.mirante.singular.form.mform.core.MTipoDecimal;
import br.net.mirante.singular.form.mform.core.MTipoInteger;
import br.net.mirante.singular.form.mform.core.MTipoMonetario;
import br.net.mirante.singular.form.mform.core.MTipoString;
import br.net.mirante.singular.form.mform.core.attachment.MTipoAttachment;
import br.net.mirante.singular.form.mform.options.MTipoSelectItem;
import br.net.mirante.singular.form.mform.util.comuns.MTipoAnoMes;
import br.net.mirante.singular.form.wicket.enums.ViewMode;
import br.net.mirante.singular.form.wicket.mapper.BooleanMapper;
import br.net.mirante.singular.form.wicket.mapper.DateMapper;
import br.net.mirante.singular.form.wicket.mapper.DecimalMapper;
import br.net.mirante.singular.form.wicket.mapper.DefaultCompostoMapper;
import br.net.mirante.singular.form.wicket.mapper.IntegerMapper;
import br.net.mirante.singular.form.wicket.mapper.ListMasterDetailMapper;
import br.net.mirante.singular.form.wicket.mapper.MonetarioMapper;
import br.net.mirante.singular.form.wicket.mapper.PanelListaMapper;
import br.net.mirante.singular.form.wicket.mapper.RadioMapper;
import br.net.mirante.singular.form.wicket.mapper.SelectBSMapper;
import br.net.mirante.singular.form.wicket.mapper.StringMapper;
import br.net.mirante.singular.form.wicket.mapper.TableListaMapper;
import br.net.mirante.singular.form.wicket.mapper.TextAreaMapper;
import br.net.mirante.singular.form.wicket.mapper.YearMonthMapper;
import br.net.mirante.singular.form.wicket.mapper.attachment.AttachmentMapper;
import br.net.mirante.singular.form.wicket.mapper.selection.MultipleCheckMapper;
import br.net.mirante.singular.form.wicket.mapper.selection.MultipleSelectBSMapper;
import br.net.mirante.singular.form.wicket.mapper.selection.PicklistMapper;
import br.net.mirante.singular.form.wicket.mapper.selection.SelectMapper;
import br.net.mirante.singular.form.wicket.mapper.selection.SelectModalBuscaMapper;
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

        IWicketComponentMapper mapper = MAPPERS.getMapper(instancia, view)
                .orElseThrow(() -> createErro(instancia, view, "Não há mappeamento de componente Wicket para o tipo"));
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
