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

package org.opensingular.form.provider;

import org.opensingular.form.SIComposite;
import org.opensingular.form.SIList;
import org.opensingular.form.SInstance;
import org.opensingular.form.STypeComposite;
import org.opensingular.form.SingularFormException;
import org.opensingular.form.util.transformer.SCompositeListBuilder;
import org.opensingular.form.util.transformer.Value;
import org.opensingular.form.util.transformer.Value.Content;

import java.util.ArrayList;
import java.util.List;

public interface SSimpleProvider extends SimpleProvider<Content, SInstance> {

    @Override
    default List<Content> load(SInstance ins) {

        STypeComposite typeComposite = null;

        if (ins instanceof SIList) {
            typeComposite = (STypeComposite) ((SIList) ins).getElementsType();
        } else if (ins instanceof SIComposite) {
            typeComposite = (STypeComposite) ins.getType();
        }

        if (typeComposite == null) {
            throw new SingularFormException("Não foi possivel obter o tipo da instancia", ins);
        }

        final SSimpleProviderListBuilder builder = new SSimpleProviderListBuilder(new SCompositeListBuilder(typeComposite, ins));
        final List<Content>         listMap = new ArrayList<>();

        fill(builder);
        builder.getList().forEach(i -> listMap.add(Value.dehydrate(i)));

        return listMap;
    }

    void fill(SSimpleProviderListBuilder builder);

}