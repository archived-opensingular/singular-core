package br.net.mirante.singular.showcase.component.input.core.select;

import br.net.mirante.singular.form.mform.SIComposite;
import br.net.mirante.singular.form.mform.provider.SimpleProvider;

import java.util.Arrays;
import java.util.List;

public class IngredienteQuimicoFilteredProvider implements SimpleProvider<IngredienteQuimico, SIComposite> {

    private final List<IngredienteQuimico> ingredientes =
            Arrays.asList(
                    new IngredienteQuimico("Água", "H2O"),
                    new IngredienteQuimico("Água Oxigenada", "H2O2"),
                    new IngredienteQuimico("Gás Oxigênio", "O2"),
                    new IngredienteQuimico("Açúcar", "C12H22O11"));

    @Override
    public List<IngredienteQuimico> load(SIComposite ins) {
        return ingredientes;
    }

}