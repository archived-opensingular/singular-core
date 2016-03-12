package br.net.mirante.singular.exemplos.canabidiol;

import java.util.Optional;

import org.apache.commons.lang3.StringUtils;

import br.net.mirante.singular.form.mform.SIComposite;
import br.net.mirante.singular.form.mform.SInfoType;
import br.net.mirante.singular.form.mform.SInstance;
import br.net.mirante.singular.form.mform.SType;
import br.net.mirante.singular.form.mform.STypeComposite;
import br.net.mirante.singular.form.mform.STypeList;
import br.net.mirante.singular.form.mform.TypeBuilder;
import br.net.mirante.singular.form.mform.basic.ui.AtrBasic;
import br.net.mirante.singular.form.mform.core.SIString;
import br.net.mirante.singular.lambda.IFunction;

@SInfoType(spackage = SPackagePeticaoCanabidiol.class)
public class STypeProdutos extends STypeComposite<SIComposite> {


    @Override
    protected void onLoadType(TypeBuilder tb) {
        super.onLoadType(tb);

        final STypeList<STypeDescricaoProduto, SIComposite> experiencias = this.addFieldListOf("produtos", STypeDescricaoProduto.class);

        STypeDescricaoProduto desc = experiencias.getElementsType();
        String nomeCompletoOutroComposicao = desc.getOutroComposicao().getName();

        final IFunction<SInstance, String> customDisplayFunction = (ins) -> {
            if (ins.getValue() == null) {
                final SType outroComposicao = ins.getDictionary().getType(nomeCompletoOutroComposicao);
                final Optional<SIString> nearest = ins.findNearest(outroComposicao);
                if (nearest.isPresent()) {
                    return nearest.get().toStringDisplay();
                } else {
                    return StringUtils.EMPTY;
                }
            } else {
                return ins.toStringDisplay();
            }
        };

        experiencias
                .withMiniumSizeOf(1)
                // .withView(new MListMasterDetailView()
                // .col(desc.getNomeComercial())
                // .col(desc.getComposicao(), customDisplayFunction)
                // .col(desc.getDescricaoQuantidade(), "Quantidade Solicitada"))
                .as(AtrBasic::new)
.label("Descrição do Produto");
    }
}
