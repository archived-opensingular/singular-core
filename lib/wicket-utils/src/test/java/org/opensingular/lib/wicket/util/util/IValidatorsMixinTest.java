/*
 *
 *  * Copyright (C) 2016 Singular Studios (a.k.a Atom Tecnologia) - www.opensingular.com
 *  *
 *  * Licensed under the Apache License, Version 2.0 (the "License");
 *  *  you may not use this file except in compliance with the License.
 *  * You may obtain a copy of the License at
 *  *
 *  * http://www.apache.org/licenses/LICENSE-2.0
 *  *
 *  * Unless required by applicable law or agreed to in writing, software
 *  * distributed under the License is distributed on an "AS IS" BASIS,
 *  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  * See the License for the specific language governing permissions and
 *  * limitations under the License.
 *
 */

package org.opensingular.lib.wicket.util.util;

import static org.opensingular.lib.wicket.util.util.Shortcuts.*;

import java.time.LocalDate;

import org.apache.wicket.validation.IValidatable;
import org.apache.wicket.validation.Validatable;
import org.junit.Assert;
import org.junit.Test;

public class IValidatorsMixinTest {

    @Test
    public void maxLength() {
        IValidatable<String> validatable = new Validatable<>("12345");
        $v.maxLength(3, $m.ofValue("error")).validate(validatable);
        Assert.assertFalse(validatable.isValid());
    }

    @Test
    public void minLength() {
        IValidatable<String> validatable = new Validatable<>("1");
        $v.minLength(3, $m.ofValue("error")).validate(validatable);
        Assert.assertFalse(validatable.isValid());
    }

    @Test
    public void notFutureDate() {
        IValidatable<LocalDate> validatable = new Validatable<>(LocalDate.now().plusDays(1));
        $v.notFutureDate($m.ofValue("error")).validate(validatable);
        Assert.assertFalse(validatable.isValid());
    }

    @Test
    public void validator() {
        IValidatable<String> validatable = new Validatable<>("");
        $v.validator((String it) -> true, $m.ofValue("error")).validate(validatable);
        Assert.assertFalse(validatable.isValid());
    }

}
