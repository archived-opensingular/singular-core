/*
 * Copyright (c) 2016, Mirante and/or its affiliates. All rights reserved.
 * Mirante PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package br.net.mirante.singular.showcase.component.input.core.select;

import br.net.mirante.singular.form.mform.PackageBuilder;
import br.net.mirante.singular.form.mform.SIComposite;
import br.net.mirante.singular.form.mform.SPackage;
import br.net.mirante.singular.form.mform.STypeComposite;
import br.net.mirante.singular.form.mform.provider.FilteredProvider;

import java.io.Serializable;
import java.util.Arrays;
import java.util.stream.Collectors;

public class CaseInputCoreSelectCompositePackage extends SPackage {

    @Override
    protected void carregarDefinicoes(PackageBuilder pb) {
        STypeComposite<?> tipoMyForm = pb.createCompositeType("testForm");

        final STypeComposite<SIComposite> ingredienteQuimico = tipoMyForm.addFieldComposite("ingredienteQuimico");
        ingredienteQuimico.asAtrBasic().label("Ingrediente Quimico");

        ingredienteQuimico.addFieldString("formulaQuimica");
        ingredienteQuimico.addFieldString("nome");

        ingredienteQuimico.autocompleteOf(IngredienteQuimico.class)
                .id(iq -> iq.nome)
                .display("${nome} - ${formulaQuimica}")
                .autoConverter(IngredienteQuimico.class)
                .provider((FilteredProvider<IngredienteQuimico, SIComposite>) (ins, filter) -> Arrays.asList(
                        new IngredienteQuimico("Água", "H2O"),
                        new IngredienteQuimico("Água Oxigenada", "H2O2"),
                        new IngredienteQuimico("Gás Oxigênio", "O2"),
                        new IngredienteQuimico("Açúcar", "C12H22O11")
                ).stream().filter(iq -> filter == null
                        || iq.formulaQuimica.toUpperCase().contains(filter.toUpperCase())
                        || iq.nome.toUpperCase().contains(filter.toUpperCase())).collect(Collectors.toList()));

    }

    public static class IngredienteQuimico implements Serializable {

        private String nome;
        private String formulaQuimica;

        public IngredienteQuimico() {
        }

        public IngredienteQuimico(String nome, String formulaQuimica) {
            this.nome = nome;
            this.formulaQuimica = formulaQuimica;
        }

        public String getNome() {
            return nome;
        }

        public void setNome(String nome) {
            this.nome = nome;
        }

        public String getFormulaQuimica() {
            return formulaQuimica;
        }

        public void setFormulaQuimica(String formulaQuimica) {
            this.formulaQuimica = formulaQuimica;
        }

    }

}
