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

package org.opensingular.form.view;

import org.opensingular.form.SInstance;
import org.opensingular.form.provider.Provider;
import org.opensingular.form.provider.ProviderContext;

/**
 * Decide a melhor view para um tipo simples que seja um selection of.
 *
 * @author Daniel C. Bordin
 */
public class ViewRuleTypeSimpleSelectionOf extends ViewRule {

    @Override
    public SView apply(SInstance instance) {
        if (instance != null && instance.asAtrProvider().getProvider() != null) {
            return decideView(instance, instance, instance.asAtrProvider().getProvider());
        }
        return null;
    }

    //TODO: [Fabs] this decision is strange to apply when the value is dynamic
    private SView decideView(SInstance instance, SInstance simple, Provider provider) {
        int size = provider.load(ProviderContext.of(instance)).size();
        /*
         * Tamanho zero indica uma possivel carga condicional e/ou dinamica.
         * Nesse caso é mais produtente escolher combo: MSelecaoPorSelectView
         */
        if (size <= 3 && size != 0 && simple.getType().isRequired()) {
            return newInstance(SViewSelectionByRadio.class);
        }
        return newInstance(SViewSelectionBySelect.class);
    }

}
