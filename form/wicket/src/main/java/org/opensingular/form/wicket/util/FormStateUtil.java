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

package org.opensingular.form.wicket.util;

import org.opensingular.form.SInstance;
import org.opensingular.form.util.transformer.Value;

import java.io.Serializable;

import static org.opensingular.form.util.transformer.Value.dehydrate;
import static org.opensingular.form.util.transformer.Value.hydrate;

public class FormStateUtil {

    public static FormState keepState(SInstance instance) {
        return new FormState(dehydrate(instance));
    }

    public static void restoreState(final SInstance instance, final FormState state) {
        instance.clearInstance();
        hydrate(instance, state.value);
    }

    public static class FormState implements Serializable {
        final Value.Content value;

        FormState(Value.Content value) {
            this.value = value;
        }
    }
}
