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

package org.opensingular.lib.wicket.util.model;

import org.apache.wicket.model.IModel;

import java.util.Comparator;

import static java.util.Comparator.comparing;
import static java.util.Comparator.nullsFirst;

public class IsGtReadOnlyModel<C extends Comparable<C>> implements IReadOnlyModel<Boolean> {
    private final IModel<C> lower;
    private final IModel<C> higher;

    public IsGtReadOnlyModel(IModel<C> lower, IModel<C> higher) {
        this.lower = lower;
        this.higher = higher;
    }

    @Override
    public Boolean getObject() {
        return comparing(IModel<C>::getObject, nullsFirst(Comparator.naturalOrder()))
                .compare(lower, higher) > 0;
    }

    @Override
    public void detach() {
        lower.detach();
        higher.detach();
    }
}