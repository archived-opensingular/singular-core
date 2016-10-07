package br.net.mirante.singular.exemplos.ggtox.primariasimplificada.listeners;

import br.net.mirante.singular.commons.lambda.IConsumer;
import br.net.mirante.singular.exemplos.ggtox.primariasimplificada.util.IngredienteAtivoUtil;
import br.net.mirante.singular.form.SIComposite;
import br.net.mirante.singular.form.SInstance;

public class IngredienteAtivoUpdateListener<T extends SInstance> implements IConsumer<T> {

    @Override
    public void accept(SInstance siComposites) {
        IngredienteAtivoUtil.IngedienteAtivoTypes data = IngredienteAtivoUtil.collectData(siComposites);
        for (SIComposite ativoAmostra : data.getAtivosAmostras()) {
            if (!data.getIngredientesAtivosMap().containsKey(ativoAmostra.getField(data.getAtivoAmostraType().idAtivo).getValue())) {
                ativoAmostra.clearInstance();
            }
        }
    }
}
