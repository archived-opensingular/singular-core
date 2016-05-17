/*
 * Copyright (c) 2016, Mirante and/or its affiliates. All rights reserved.
 * Mirante PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package br.net.mirante.singular.studio.component.input.core.select;

import java.util.Arrays;
import java.util.List;

import br.net.mirante.singular.form.SIComposite;
import br.net.mirante.singular.form.provider.SimpleProvider;

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