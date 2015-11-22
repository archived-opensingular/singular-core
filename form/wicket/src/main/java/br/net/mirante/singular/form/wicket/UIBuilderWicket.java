package br.net.mirante.singular.form.wicket;

import org.apache.wicket.model.IModel;

import br.net.mirante.singular.form.mform.MInstancia;
import br.net.mirante.singular.form.mform.MTipoComposto;
import br.net.mirante.singular.form.mform.MTipoLista;
import br.net.mirante.singular.form.mform.MTipoSimples;
import br.net.mirante.singular.form.mform.SingularFormException;
import br.net.mirante.singular.form.mform.basic.view.MGridListaView;
import br.net.mirante.singular.form.mform.basic.view.MPanelListaView;
import br.net.mirante.singular.form.mform.basic.view.MSelecaoMultiplaPorCheckView;
import br.net.mirante.singular.form.mform.basic.view.MSelecaoMultiplaPorPicklistView;
import br.net.mirante.singular.form.mform.basic.view.MSelecaoMultiplaPorSelectBSView;
import br.net.mirante.singular.form.mform.basic.view.MSelecaoMultiplaPorSelectView;
import br.net.mirante.singular.form.mform.basic.view.MSelecaoPorRadioView;
import br.net.mirante.singular.form.mform.basic.view.MSelecaoPorSelectBSView;
import br.net.mirante.singular.form.mform.basic.view.MSelecaoPorSelectView;
import br.net.mirante.singular.form.mform.basic.view.MTabView;
import br.net.mirante.singular.form.mform.basic.view.MTableListaView;
import br.net.mirante.singular.form.mform.basic.view.MView;
import br.net.mirante.singular.form.mform.basic.view.ViewMapperRegistry;
import br.net.mirante.singular.form.mform.basic.view.ViewResolver;
import br.net.mirante.singular.form.mform.core.MTipoBoolean;
import br.net.mirante.singular.form.mform.core.MTipoData;
import br.net.mirante.singular.form.mform.core.MTipoInteger;
import br.net.mirante.singular.form.mform.core.MTipoString;
import br.net.mirante.singular.form.mform.core.attachment.MTipoAttachment;
import br.net.mirante.singular.form.mform.util.comuns.MTipoAnoMes;
import br.net.mirante.singular.form.wicket.mapper.BooleanMapper;
import br.net.mirante.singular.form.wicket.mapper.DateMapper;
import br.net.mirante.singular.form.wicket.mapper.DefaultCompostoMapper;
import br.net.mirante.singular.form.wicket.mapper.IntegerMapper;
import br.net.mirante.singular.form.wicket.mapper.MultipleCheckMapper;
import br.net.mirante.singular.form.wicket.mapper.MultipleSelectBSMapper;
import br.net.mirante.singular.form.wicket.mapper.MultipleSelectMapper;
import br.net.mirante.singular.form.wicket.mapper.PanelListaMapper;
import br.net.mirante.singular.form.wicket.mapper.PicklistMapper;
import br.net.mirante.singular.form.wicket.mapper.RadioMapper;
import br.net.mirante.singular.form.wicket.mapper.SelectBSMapper;
import br.net.mirante.singular.form.wicket.mapper.SelectMapper;
import br.net.mirante.singular.form.wicket.mapper.StringMapper;
import br.net.mirante.singular.form.wicket.mapper.TableListaMapper;
import br.net.mirante.singular.form.wicket.mapper.YearMonthMapper;
import br.net.mirante.singular.form.wicket.mapper.attachment.AttachmentMapper;

public class UIBuilderWicket {

    private static final ViewMapperRegistry<IWicketComponentMapper> MAPPERS = new ViewMapperRegistry<>();

    static {
        //@formatter:off
        MAPPERS.register(MTipoSimples.class,    MSelecaoPorRadioView.class,            RadioMapper::new);
        MAPPERS.register(MTipoSimples.class,    MSelecaoPorSelectView.class,           SelectMapper::new);
        MAPPERS.register(MTipoSimples.class,    MSelecaoPorSelectBSView.class,         SelectBSMapper::new);
        MAPPERS.register(MTipoBoolean.class,                                           BooleanMapper::new);
        MAPPERS.register(MTipoInteger.class,                                           IntegerMapper::new);
        MAPPERS.register(MTipoString.class,                                            StringMapper::new);
        MAPPERS.register(MTipoData.class,                                              DateMapper::new);
        MAPPERS.register(MTipoAnoMes.class,                                            YearMonthMapper::new);
        MAPPERS.register(MTipoAttachment.class,                                        AttachmentMapper::new);
        MAPPERS.register(MTipoLista.class,      MSelecaoMultiplaPorSelectView.class,   MultipleSelectMapper::new);
        MAPPERS.register(MTipoLista.class,      MSelecaoMultiplaPorSelectBSView.class, MultipleSelectBSMapper::new);
        MAPPERS.register(MTipoLista.class,      MSelecaoMultiplaPorCheckView.class,    MultipleCheckMapper::new);
        MAPPERS.register(MTipoLista.class,      MSelecaoMultiplaPorPicklistView.class, PicklistMapper::new);
        MAPPERS.register(MTipoComposto.class,                                          DefaultCompostoMapper::new);
        MAPPERS.register(MTipoComposto.class,   MTabView.class,                        DefaultCompostoMapper::new);
        MAPPERS.register(MTipoLista.class,                                             TableListaMapper::new);
        MAPPERS.register(MTipoLista.class,      MTableListaView.class,                 TableListaMapper::new);
        MAPPERS.register(MTipoLista.class,      MGridListaView.class,                  PanelListaMapper::new);
        MAPPERS.register(MTipoLista.class,      MPanelListaView.class,                 PanelListaMapper::new);
        //@formatter:on
    }

    public static void buildForEdit(WicketBuildContext ctx, IModel<? extends MInstancia> model) {
        Object obj = model.getObject();
        MInstancia instancia = (MInstancia) obj;
        MView view = ViewResolver.resolve(instancia);
        ctx.setContainerInstance(instancia);
        IWicketComponentMapper mapper = MAPPERS.getMapper(instancia, view)
                .orElseThrow(() -> createErro(instancia, view, "Não há mappeamento de componente Wicket para o tipo"));
        mapper.buildView(ctx, view, model);
    }

    private static SingularFormException createErro(MInstancia instancia, MView view, String msg) {
        return new SingularFormException(
            msg + " (instancia=" + instancia.getPathFull()
                + ", tipo=" + instancia.getMTipo().getNome()
                + ", classeInstancia=" + instancia.getClass()
                + ", tipo=" + instancia.getMTipo()
                + ", view=" + view
                + ")");
    }
}
