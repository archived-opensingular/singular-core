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

import org.opensingular.form.exemplos.notificacaosimplificada.domain.CategoriaRegulatoriaMedicamento;
import org.opensingular.form.exemplos.notificacaosimplificada.service.DominioService;
import org.opensingular.form.SIComposite;
import org.opensingular.form.SInfoType;
import org.opensingular.form.STypeComposite;
import org.opensingular.form.TypeBuilder;
import org.opensingular.form.provider.STextQueryProvider;
import org.opensingular.form.type.core.STypeInteger;
import org.opensingular.form.type.core.STypeString;
import org.opensingular.form.view.SViewAutoComplete;

@SInfoType(spackage = SPackageVocabularioControlado.class)
public class STypeCategoriaRegulatoria extends STypeComposite<SIComposite> {

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
                    .label("Classe")
                    .required();
            this.setView(SViewAutoComplete::new);

            this.autocomplete()
                    .id(id)
                    .display(descricao)
                    .filteredProvider((STextQueryProvider) (builder, query) -> {
                        builder
                                .getCurrentInstance()
                                .getDocument()
                                .lookupService(DominioService.class)
                                .buscarVocabulario(CategoriaRegulatoriaMedicamento.class, query)
                                .forEach(vc -> builder.add().set(id, vc.getId()).set(descricao, vc.getDescricao()));
                    });

        }
    }


}
