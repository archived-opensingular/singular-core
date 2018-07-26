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
import org.apache.wicket.markup.html.form.TextField;

import javax.annotation.Nonnull;

/**
 * Representa um conjunto de asserções voltadas para componentes genéricos do Wicket.
 *
 * @author Daniel Bordin on 12/02/2017.
 */
public class AssertionsSimpleWComponent extends AssertionsSimpleWComponentBase<AssertionsSimpleWComponent, Component> {

    public AssertionsSimpleWComponent(Component c) {
        super(c);
    }

    /**
     * Verifica se o componente é um TextField e sendo retorna um assertiva específica para esse caso. Dispara exception
     * senão for um TextField Wicket.
     */
    @Nonnull
    public AssertionsSimpleWTextField asTextField() {
        return new AssertionsSimpleWTextField(getTarget(TextField.class));
    }
}