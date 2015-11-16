package br.net.mirante.singular.form.wicket;

import org.apache.wicket.model.IModel;

import br.net.mirante.singular.form.mform.MInstancia;
import br.net.mirante.singular.form.mform.MTipoComposto;
import br.net.mirante.singular.form.mform.MTipoLista;
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

    private static final WicketMapperRegistry MAPPER_REGISTRY = new WicketMapperRegistry();
    static {
        MAPPER_REGISTRY.registerMapper(MTipoBoolean.class, /*   */MView.class, /*                          */BooleanMapper::new);
        MAPPER_REGISTRY.registerMapper(MTipoInteger.class, /*   */MView.class, /*                          */IntegerMapper::new);
        MAPPER_REGISTRY.registerMapper(MTipoString.class, /*    */MView.class, /*                          */StringMapper::new);
        MAPPER_REGISTRY.registerMapper(MTipoString.class, /*    */MSelecaoPorRadioView.class, /*           */RadioMapper::new);
        MAPPER_REGISTRY.registerMapper(MTipoString.class, /*    */MSelecaoPorSelectView.class, /*          */SelectMapper::new);
        MAPPER_REGISTRY.registerMapper(MTipoString.class, /*    */MSelecaoPorSelectBSView.class, /*        */SelectBSMapper::new);
        MAPPER_REGISTRY.registerMapper(MTipoLista.class, /*     */MSelecaoMultiplaPorSelectView.class, /*  */MultipleSelectMapper::new);
        MAPPER_REGISTRY.registerMapper(MTipoLista.class, /*     */MSelecaoMultiplaPorSelectBSView.class, /**/MultipleSelectBSMapper::new);
        MAPPER_REGISTRY.registerMapper(MTipoLista.class, /*     */MSelecaoMultiplaPorCheckView.class, /*   */MultipleCheckMapper::new);
        MAPPER_REGISTRY.registerMapper(MTipoLista.class, /*     */MSelecaoMultiplaPorPicklistView.class, /**/PicklistMapper::new);
        MAPPER_REGISTRY.registerMapper(MTipoData.class, /*      */MView.class, /*                          */DateMapper::new);
        MAPPER_REGISTRY.registerMapper(MTipoAnoMes.class, /*    */MView.class, /*                          */YearMonthMapper::new);
        MAPPER_REGISTRY.registerMapper(MTipoAttachment.class, /**/MView.class, /*                          */AttachmentMapper::new);
        MAPPER_REGISTRY.registerMapper(MTipoComposto.class, /*  */MView.class, /*                          */DefaultCompostoMapper::new);
        MAPPER_REGISTRY.registerMapper(MTipoComposto.class, /*  */MTabView.class, /*                       */DefaultCompostoMapper::new);
        MAPPER_REGISTRY.registerMapper(MTipoLista.class, /*     */MView.class, /*                          */TableListaMapper::new);
        MAPPER_REGISTRY.registerMapper(MTipoLista.class, /*     */MTableListaView.class, /*                */TableListaMapper::new);
        MAPPER_REGISTRY.registerMapper(MTipoLista.class, /*     */MGridListaView.class, /*                 */PanelListaMapper::new);
        MAPPER_REGISTRY.registerMapper(MTipoLista.class, /*     */MPanelListaView.class, /*                */PanelListaMapper::new);
    }

    public static void buildForEdit(WicketBuildContext ctx, IModel<? extends MInstancia> model) {
        Object obj = model.getObject();
        MInstancia instancia = (MInstancia) obj;
        MView view = instancia.getView();
        IWicketComponentMapper mapper = MAPPER_REGISTRY.getMapper(instancia)
            .orElseThrow(() -> createErro(instancia, view, "Não há mappeamento de componente Wicket para o tipo"));
        mapper.buildView(ctx, view, model);
    }

    private static RuntimeException createErro(MInstancia instancia, MView view, String msg) {
        return new RuntimeException(
            msg + " (instancia=" + instancia.getPathFull()
                + ", tipo=" + instancia.getMTipo().getNome()
                + ", classeInstancia=" + instancia.getClass()
                + ", tipo=" + instancia.getMTipo()
                + ", view=" + view
                + ")");
    }
}
