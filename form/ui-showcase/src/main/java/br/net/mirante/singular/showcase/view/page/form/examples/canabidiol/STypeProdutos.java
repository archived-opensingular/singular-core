package br.net.mirante.singular.showcase.view.page.form.examples.canabidiol;

import java.util.Optional;

import org.apache.commons.lang3.StringUtils;

import br.net.mirante.singular.form.mform.MInfoTipo;
import br.net.mirante.singular.form.mform.SIComposite;
import br.net.mirante.singular.form.mform.SInstance;
import br.net.mirante.singular.form.mform.SType;
import br.net.mirante.singular.form.mform.STypeComposite;
import br.net.mirante.singular.form.mform.STypeLista;
import br.net.mirante.singular.form.mform.TipoBuilder;
import br.net.mirante.singular.form.mform.basic.ui.AtrBasic;
import br.net.mirante.singular.form.mform.basic.view.MListMasterDetailView;
import br.net.mirante.singular.form.mform.core.SIString;
import br.net.mirante.singular.lambda.IFunction;

@MInfoTipo(nome = "MTipoProdutos", pacote = SPackagePeticaoCanabidiol.class)
public class STypeProdutos extends STypeComposite<SIComposite> {


    @Override
    protected void onLoadType(TipoBuilder tb) {
        super.onLoadType(tb);

        final STypeLista<STypeDescricaoProduto, SIComposite> experiencias = this.addCampoListaOf("produtos", STypeDescricaoProduto.class);

        STypeDescricaoProduto desc = experiencias.getTipoElementos();
        String nomeCompletoOutroComposicao = desc.getOutroComposicao().getNome();

        final IFunction<SInstance, String> customDisplayFunction = (ins) -> {
            if (ins.getValor() == null) {
                final SType outroComposicao = ins.getDicionario().getTipo(nomeCompletoOutroComposicao);
                final Optional<SIString> nearest = ins.findNearest(outroComposicao);
                if (nearest.isPresent()) {
                    return nearest.get().getDisplayString();
                } else {
                    return StringUtils.EMPTY;
                }
            } else {
                return ins.getDisplayString();
            }
        };

        experiencias
                .withView(new MListMasterDetailView()
                        .col(desc.getNomeComercial())
                        .col(desc.getComposicao(), customDisplayFunction)
                        .col(desc.getDescricaoQuantidade(), "Quantidade Solicitada"))
                .as(AtrBasic::new)
                .label("Descrição do Produto");

    }


}
