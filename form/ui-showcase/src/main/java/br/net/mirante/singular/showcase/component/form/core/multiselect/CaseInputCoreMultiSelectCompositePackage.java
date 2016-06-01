/*
 * Copyright (c) 2016, Mirante and/or its affiliates. All rights reserved.
 * Mirante PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package br.net.mirante.singular.showcase.component.form.core.multiselect;

import java.io.Serializable;

import br.net.mirante.singular.form.PackageBuilder;
import br.net.mirante.singular.form.SIComposite;
import br.net.mirante.singular.form.SPackage;
import br.net.mirante.singular.form.STypeComposite;
import br.net.mirante.singular.form.STypeList;
import br.net.mirante.singular.form.converter.SInstanceConverter;
import br.net.mirante.singular.form.type.core.STypeString;
import br.net.mirante.singular.form.util.transformer.Value;
import br.net.mirante.singular.showcase.component.CaseItem;
import br.net.mirante.singular.showcase.component.Group;

/**
 * Para usar um tipo composto na seleção, este deve ser do tipo MTipoSelectItem. <br/>
 * É permitido se mudar quais campos serão utilizados como chave e valor.
 */
@CaseItem(componentName = "Multi Select", subCaseName = "Tipo Composto", group = Group.INPUT)
public class CaseInputCoreMultiSelectCompositePackage extends SPackage {

    @Override
    protected void onLoadPackage(PackageBuilder pb) {
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
