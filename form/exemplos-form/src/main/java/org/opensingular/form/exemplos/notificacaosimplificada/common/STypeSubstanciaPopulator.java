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

package org.opensingular.form.exemplos.notificacaosimplificada.common;

import java.util.List;
import java.util.function.Function;

import org.apache.commons.lang3.tuple.Triple;

import org.opensingular.form.exemplos.notificacaosimplificada.domain.Substancia;
import org.opensingular.form.exemplos.util.TripleConverter;
import org.opensingular.form.exemplos.notificacaosimplificada.service.DominioService;
import org.opensingular.form.SIComposite;
import org.opensingular.form.SInstance;
import org.opensingular.form.SType;
import org.opensingular.form.STypeComposite;
import org.opensingular.form.STypeList;
import org.opensingular.form.STypeSimple;
import org.opensingular.form.util.transformer.Value;
import org.opensingular.form.view.SViewListByTable;
import org.opensingular.form.view.SViewReadOnly;

public class STypeSubstanciaPopulator {

    private final STypeComposite<?>                     root;
    private final SType<?>                              dependentType;
    private final STypeSimple                           idConfiguracaoLinhaProducao;
    private final Function<SInstance, List<Substancia>> substanciasSupplier;

    public STypeSubstanciaPopulator(
            STypeComposite<?> root,
            SType<?> dependentType,
            STypeSimple idConfiguracaoLinhaProducao,
            Function<SInstance, List<Substancia>> substanciasSupplier) {
        this.root = root;
        this.dependentType = dependentType;
        this.idConfiguracaoLinhaProducao = idConfiguracaoLinhaProducao;
        this.substanciasSupplier = substanciasSupplier;
    }

    private DominioService dominioService(SInstance ins) {
        return ins.getDocument().lookupService(DominioService.class);
    }

    public STypeList<STypeComposite<SIComposite>, SIComposite> populate() {

        final STypeList<STypeComposite<SIComposite>, SIComposite> substancias = root.addFieldListOfComposite("substancias", "concentracaoSubstancia");

        final STypeComposite<?> concentracaoSubstancia = substancias.getElementsType();
        final STypeComposite<?> substancia             = concentracaoSubstancia.addFieldComposite("substancia");

        final STypeSimple idSubstancia                          = substancia.addFieldInteger("id");
        final STypeSimple idConfiguracaoLinhaProducaoSubstancia = substancia.addFieldInteger("configuracaoLinhaProducao");
        final STypeSimple substanciaDescricao                   = substancia.addFieldString("descricao");

        {
            substancias
                    .withView(() -> new SViewListByTable().disableNew().disableDelete())
                    .withUpdateListener(list -> {
                        for (Substancia s : substanciasSupplier.apply(list)) {
                            final SIComposite cs = list.addNew();
                            final SIComposite si = (SIComposite) cs.getField(substancia.getNameSimple());
                            si.setValue(idSubstancia, s.getId());
                            si.setValue(idConfiguracaoLinhaProducaoSubstancia, Value.of(list, idConfiguracaoLinhaProducao));
                            si.setValue(substanciaDescricao, s.getDescricao());
                        }
                    })
                    .asAtr()
                    .label("Substância")
                    .dependsOn(dependentType)
                    .exists(i -> Value.notNull(i, idConfiguracaoLinhaProducao));
        }

        final String substanciaSimpleName = substanciaDescricao.getNameSimple();
        {
            substancia
                    .withView(SViewReadOnly::new)
                    .asAtr()
                    .displayString("${descricao}")
                    .label("Nome")
                    .asAtrBootstrap()
                    .colPreference(6);
        }

        final STypeComposite<SIComposite> concentracao             = concentracaoSubstancia.addFieldComposite("concentracao");
        final SType<?>          idConcentracacao         = concentracao.addFieldInteger("id");
        final STypeSimple       idSubstanciaConcentracao = concentracao.addFieldInteger("idSubstancia");
        final STypeSimple       descConcentracao         = concentracao.addFieldString("descricao");
        {
            concentracao
                    .asAtr()
                    .required()
                    .label("Concentração")
                    .asAtrBootstrap()
                    .colPreference(6);

            concentracao.selectionOf(Triple.class)
                    .id(t -> String.valueOf(t.getLeft()))
                    .display("${right}")
                    .converter(new TripleConverter(idConcentracacao, idSubstanciaConcentracao, descConcentracao))
                    .simpleProvider((ins) -> {
                        Integer id = (Integer) Value.of(ins, idSubstancia);
                        return dominioService(ins).concentracoes(id);
                    });
        }

        return substancias;
    }
}
