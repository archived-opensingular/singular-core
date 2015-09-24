package br.net.mirante.singular.form.wicket.mapper;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import org.apache.wicket.model.IModel;

import br.net.mirante.singular.form.mform.MILista;
import br.net.mirante.singular.form.mform.MInstancia;
import br.net.mirante.singular.form.mform.MTipo;
import br.net.mirante.singular.form.mform.MTipoComposto;
import br.net.mirante.singular.form.wicket.AtrWicket;
import br.net.mirante.singular.form.wicket.WicketBuildContext;
import br.net.mirante.singular.util.wicket.bootstrap.layout.BSCol;

public class GridListaMapper extends AbstractListaMapper {
    public GridListaMapper() {
        super(
            null,
            GridListaMapper::configureCurrentContext,
            GridListaMapper::configureChildContext);
    }
    private static void configureCurrentContext(WicketBuildContext ctx, IModel<MILista<MInstancia>> model) {
        MTipo<?> tElementos = model.getObject().getTipoElementos();
        if (tElementos instanceof MTipoComposto<?>) {
            MTipoComposto<?> tElemento = (MTipoComposto<?>) tElementos;
            Set<String> camposElemento = tElemento.getCampos();
            if (!camposElemento.isEmpty()) {
                ctx.setHint(DefaultCompostoMapper.COL_WIDTHS, resolveColWidths(tElemento));
            }
        }
    }
    private static HashMap<String, Integer> resolveColWidths(MTipoComposto<?> tElemento) {
        Set<String> camposSemLargura = new HashSet<>();

        HashMap<String, Integer> colWidths = new HashMap<>();
        int colunasRestantes = BSCol.MAX_COLS;
        for (String nomeCampo : tElemento.getCampos()) {
            MTipo<?> tCampo = tElemento.getCampo(nomeCampo);
            int larguraPref = tCampo.as(AtrWicket::new).getLarguraPref(-1);
            if (larguraPref >= 0) {
                colWidths.put(nomeCampo, larguraPref);
                colunasRestantes -= larguraPref;
            } else {
                camposSemLargura.add(nomeCampo);
            }
        }
        if (!camposSemLargura.isEmpty()) {
            if (colunasRestantes <= 0) {
                // caso não sobre nenhuma coluna livre, atribuir largura 1 para os campos restantes
                colunasRestantes = camposSemLargura.size();
            }
            int baseColWidth = colunasRestantes / camposSemLargura.size();
            int largerColWidth = baseColWidth + (colunasRestantes - camposSemLargura.size() * baseColWidth);
            for (Iterator<String> it = camposSemLargura.iterator(); it.hasNext();) {
                String nome = it.next();
                int colWidth = (it.hasNext()) ? baseColWidth : largerColWidth;
                colWidths.put(nome, colWidth);
            }
        }
        return colWidths;
    }
    private static void configureChildContext(WicketBuildContext ctx, IModel<MILista<MInstancia>> model, int index) {
        ctx.setHint(ControlsFieldComponentMapper.NO_DECORATION, index > 0);
    }
}