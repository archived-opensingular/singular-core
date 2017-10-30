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

import org.apache.wicket.markup.html.basic.Label;
import org.assertj.core.api.AbstractCharSequenceAssert;
import org.assertj.core.api.Assertions;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * Represents a set of assertion for Wicket's Label component class.
 *
 * @author Daniel Bordin
 * @since  2017-10-28
 */
public class AssertionsWLabel extends AssertionsWComponentBase<Label, AssertionsWLabel> {

    public AssertionsWLabel(@Nullable Label label) {
        super(label);
    }

    /** Returns a String assertive for the label text. */
    @Nonnull
    public AbstractCharSequenceAssert<?, String> assertValue() {
        return Assertions.assertThat(getTarget().getDefaultModelObjectAsString());

    }
}
