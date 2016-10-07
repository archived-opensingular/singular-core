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

package org.opensingular.form.exemplos.notificacaosimplificada.form.vocabulario;

import org.opensingular.form.exemplos.notificacaosimplificada.domain.EmbalagemPrimariaBasica;
import org.opensingular.form.exemplos.notificacaosimplificada.domain.generic.VocabularioControlado;
import org.opensingular.form.SIComposite;
import org.opensingular.form.SInfoType;
import org.opensingular.form.STypeComposite;
import org.opensingular.form.TypeBuilder;
import org.opensingular.form.converter.SInstanceConverter;
import org.opensingular.form.type.core.STypeInteger;
import org.opensingular.form.type.core.STypeString;
import org.opensingular.form.util.transformer.Value;

@SInfoType(spackage = SPackageVocabularioControlado.class)
public class STypeEmbalagemPrimaria extends STypeComposite<SIComposite> {

    public STypeString  descricao;
    public STypeInteger id;

    @Override
    protected void onLoadType(TypeBuilder tb) {
        super.onLoadType(tb);
        id = this.addFieldInteger("id");
        descricao = this.addFieldString("descricao");
        {
            this
                    .asAtrBootstrap()
                    .colPreference(6)
                    .asAtr()
                    .label("Embalagem primária")
                    .required();
            this.autocompleteOf(EmbalagemPrimariaBasica.class)
                    .id("${id}")
                    .display(VocabularioControlado::getDescricao)
                    .converter(new SInstanceConverter<EmbalagemPrimariaBasica, SIComposite>() {
                        @Override
                        public void fillInstance(SIComposite ins, EmbalagemPrimariaBasica obj) {
                            ins.setValue(id, obj.getId());
                            ins.setValue(descricao, obj.getDescricao());
                        }

                        @Override
                        public EmbalagemPrimariaBasica toObject(SIComposite ins) {
                            return SPackageVocabularioControlado.dominioService(ins).findEmbalagensBasicas(null)
                                    .stream().filter(u -> Integer.valueOf(u.getId().intValue()).equals(Value.of(ins, id)))
                                    .findFirst()
                                    .orElse(null);
                        }
                    })
                    .filteredProvider((ins, query) -> SPackageVocabularioControlado.dominioService(ins).findEmbalagensBasicas(query));
        }
    }


}
