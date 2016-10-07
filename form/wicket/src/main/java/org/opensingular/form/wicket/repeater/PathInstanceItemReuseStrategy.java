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

package org.opensingular.form.wicket.repeater;

import org.opensingular.form.SInstance;
import org.apache.wicket.markup.repeater.IItemFactory;
import org.apache.wicket.markup.repeater.IItemReuseStrategy;
import org.apache.wicket.markup.repeater.Item;
import org.apache.wicket.model.IModel;

import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;

/**
 * Estratégia pare reuso de itens baseado no path da instancia.
 */
public class PathInstanceItemReuseStrategy implements IItemReuseStrategy {

    @Override
    public <T> Iterator<Item<T>> getItems(IItemFactory<T> factory,
                                          Iterator<IModel<T>> newModels,
                                          Iterator<Item<T>> existingItems) {

        final Map<String, Item<T>> pathAndItem = buildExisistingItensPathAndItemMap(existingItems);

        return new Iterator<Item<T>>() {

            private int index = 0;

            @Override
            public boolean hasNext() {
                return newModels.hasNext();
            }

            @Override
            public Item<T> next() {
                index += 1;

                final IModel<T> next = newModels.next();
                final Optional<String> path = getPath(next);

                if (path.isPresent() && pathAndItem.containsKey(path.get())) {
                    return pathAndItem.get(path.get());
                } else {
                    return factory.newItem(index, next);
                }
            }
        };
    }

    /**
     * Constroi um mapa de path e item
     * @param existingItens os itens existentes
     * @param <T> tipo do model
     * @return o mapa
     */
    private <T> Map<String, Item<T>> buildExisistingItensPathAndItemMap(Iterator<Item<T>> existingItens) {
        final Map<String, Item<T>> pathAndItem = new LinkedHashMap<>();

        while (existingItens.hasNext()) {
            final Item<T> i = existingItens.next();
            getPath(i.getModel()).ifPresent(path -> pathAndItem.put(path, i));
        }

        return pathAndItem;
    }

    /**
     * Recupera o Path a partir de um model
     * @param model o model que representa a instancia
     * @param <T> o tipo do model
     * @return o path
     */
    private <T> Optional<String> getPath(IModel<T> model) {
        final T object = model.getObject();

        if (object instanceof SInstance) {
            return Optional.of(((SInstance) object).getPathFromRoot());
        }

        return Optional.empty();
    }

}
