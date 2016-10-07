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

package org.opensingular.form.exemplos.notificacaosimplificada.form.dinamizado;

import org.opensingular.form.exemplos.notificacaosimplificada.form.vocabulario.SPackageVocabularioControlado;
import org.opensingular.form.exemplos.notificacaosimplificada.form.vocabulario.STypeLinhaProducao;
import org.opensingular.form.SInfoType;
import org.opensingular.form.provider.STextQueryProvider;

@SInfoType(spackage = SPackageNotificacaoSimplificadaDinamizado.class)
public class STypeLinhaProducaoDinamizado extends STypeLinhaProducao {

    @Override
    protected STextQueryProvider getProvider() {
        return (STextQueryProvider) (builder, query) -> SPackageVocabularioControlado.dominioService(builder.getCurrentInstance()).listarLinhasProducaoDinamizado(query).forEach(lp -> {
            builder.add().set(id, lp.getId()).set(descricao, lp.getDescricao());
        });
    }

}