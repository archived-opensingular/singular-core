/*
 * Copyright (C) 2016 Singular Studios (a.k.a Atom Tecnologia) - www.opensingular.com
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
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

package org.opensingular.internal.lib.wicket.test;

import org.apache.wicket.Component;
import org.assertj.core.api.AssertFactory;
import org.opensingular.lib.commons.test.AssertionsBase;
import org.opensingular.lib.commons.test.BaseAssertionsForList;

import javax.annotation.Nonnull;
import java.util.List;

/**
 * Representa um conjunto de asserções voltadas para lista de Componentes Wicket.
 *
 * @author Daniel Bordin
 * @since 2017-02-12
 */
public abstract class AbstractAssertionsForWicketList<SELF extends AbstractAssertionsForWicketList<SELF,
        ELEMENT_ASSERT>, ELEMENT_ASSERT extends AssertionsBase<ELEMENT_ASSERT, Component>>
        extends BaseAssertionsForList<SELF, Component, ELEMENT_ASSERT> {

    public AbstractAssertionsForWicketList(List<Component> target,
            @Nonnull AssertFactory<Component, ELEMENT_ASSERT> assertFactory) {
        super(target, assertFactory);
    }

}
