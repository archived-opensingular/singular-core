/*
 * Copyright (c) 2016, Mirante and/or its affiliates. All rights reserved.
 * Mirante PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package br.net.mirante.singular.showcase.component.input.core.multiselect;

import br.net.mirante.singular.form.mform.*;
import br.net.mirante.singular.form.mform.converter.SInstanceConverter;
import br.net.mirante.singular.form.mform.core.STypeString;
import br.net.mirante.singular.form.mform.util.transformer.Value;

import java.io.Serializable;

public class CaseInputCoreMultiSelectCompositePackage extends SPackage {

    @Override
    protected void carregarDefinicoes(PackageBuilder pb) {
        STypeComposite<?> root = pb.createCompositeType("testForm");

        final STypeList<STypeComposite<SIComposite>, SIComposite> componentesQuimicos = root.addFieldListOfComposite("componentesQuimicos", "componenteQuimico");
        final STypeComposite<SIComposite>                         componenteQuimico   = componentesQuimicos.getElementsType();

        final STypeString nome           = componenteQuimico.addFieldString("nome");
        final STypeString formulaQuimica = componenteQuimico.addFieldString("formulaQuimica");

        componentesQuimicos.selectionOf(ComponenteQuimico.class)
                .id(ComponenteQuimico::getNome)
                .display("${formulaQuimica} - ${nome}")
                .converter(new SInstanceConverter<ComponenteQuimico, SIComposite>() {
                    @Override
                    public void fillInstance(SIComposite ins, ComponenteQuimico obj) {
                        ins.setValue(nome, obj.getNome());
                        ins.setValue(formulaQuimica, obj.getFormulaQuimica());
                    }

                    @Override
                    public ComponenteQuimico toObject(SIComposite ins) {
                        return new ComponenteQuimico(Value.of(ins, formulaQuimica), Value.of(ins, nome));
                    }
                })
                .simpleProviderOf(
                        new ComponenteQuimico("Água", "h2o"),
                        new ComponenteQuimico("Água Oxigenada", "h2o2"),
                        new ComponenteQuimico("Gás Oxigênio", "o2"),
                        new ComponenteQuimico("Açúcar", "C12H22O11")
                );

    }

    public static class ComponenteQuimico implements Serializable {
        private String nome;
        private String formulaQuimica;

        public ComponenteQuimico() {
        }

        public ComponenteQuimico(String formulaQuimica, String nome) {
            this.formulaQuimica = formulaQuimica;
            this.nome = nome;
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
