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

package org.opensingular.form.wicket.helpers;

import org.apache.wicket.markup.html.form.TextField;
import org.fest.assertions.api.Assertions;
import org.fest.assertions.api.StringAssert;

/**
 * Representa um conjunto de asserções voltadas para TextField do Wicket.
 *
 * @author Daniel Bordin on 12/02/2017.
 */
public class AssertionsWTextField extends AssertionsWComponentBase<TextField, AssertionsWTextField> {

    public AssertionsWTextField(TextField c) {
        super(c);
    }

    /** Retorna um objeto de assertiva em cima do valor texto do TextField. */
    public StringAssert assertValue() {
        return Assertions.assertThat(getTarget().getValue());

    }
}
