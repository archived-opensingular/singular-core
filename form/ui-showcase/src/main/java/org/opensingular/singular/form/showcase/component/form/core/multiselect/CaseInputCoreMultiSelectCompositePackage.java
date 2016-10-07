/*
 * Copyright (C) 2016 Singular Studios (a.k.a Atom Tecnologia) - www.opensingular.com
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.opensingular.singular.form.showcase.component.form.core.multiselect;

import java.io.Serializable;

import org.opensingular.form.PackageBuilder;
import org.opensingular.form.SIComposite;
import org.opensingular.form.SPackage;
import org.opensingular.form.STypeComposite;
import org.opensingular.form.STypeList;
import org.opensingular.form.converter.SInstanceConverter;
import org.opensingular.form.type.core.STypeString;
import org.opensingular.form.util.transformer.Value;
import org.opensingular.singular.form.showcase.component.CaseItem;
import org.opensingular.singular.form.showcase.component.Group;

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
