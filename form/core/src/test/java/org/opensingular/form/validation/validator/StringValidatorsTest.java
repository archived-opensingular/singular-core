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

package org.opensingular.form.validation.validator;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.opensingular.form.SDictionary;
import org.opensingular.form.SInstance;
import org.opensingular.form.type.core.SIString;
import org.opensingular.form.type.core.STypeString;
import org.opensingular.form.validation.InstanceValidatable;
import org.opensingular.form.validation.ValidationError;
import org.opensingular.form.validation.ValidationErrorLevel;


public class StringValidatorsTest {

    InstanceValidatableMock<SIString> instanceValidatableMock;
    SIString siString;

    @Before
    public void setUp() throws Exception {
        siString = SDictionary.create().newInstance(STypeString.class);
        instanceValidatableMock = new InstanceValidatableMock<>(siString);
    }

    @Test
    public void matches() throws Exception {
        siString.setValue("danilo");
        StringValidators.matches("[a-z]+", "").validate(instanceValidatableMock);
        Assert.assertFalse(instanceValidatableMock.containsErrors);
    }


    @Test
    public void doestNotMatches() throws Exception {
        siString.setValue("danilo");
        StringValidators.matches("\\d", "").validate(instanceValidatableMock);
        Assert.assertTrue(instanceValidatableMock.containsErrors);
    }

    @Test
    public void endsWith() throws Exception {
        siString.setValue("danilo");
        StringValidators.endsWith("ilo", "").validate(instanceValidatableMock);
        Assert.assertFalse(instanceValidatableMock.containsErrors);
    }

    @Test
    public void doestNotEndsWith() throws Exception {
        siString.setValue("danilo");
        StringValidators.endsWith("bajara", "").validate(instanceValidatableMock);
        Assert.assertTrue(instanceValidatableMock.containsErrors);
    }

    @Test
    public void isNotBlank() throws Exception {
        siString.setValue("danilo");
        StringValidators.isNotBlank("").validate(instanceValidatableMock);
        Assert.assertFalse(instanceValidatableMock.containsErrors);
    }

    @Test
    public void isBlank() throws Exception {
        siString.setValue("    ");
        StringValidators.isNotBlank("").validate(instanceValidatableMock);
        Assert.assertTrue(instanceValidatableMock.containsErrors);
    }

    static class InstanceValidatableMock<I extends SInstance> implements InstanceValidatable<I> {

        final I instance;
        boolean containsErrors = false;

        InstanceValidatableMock(I instance) {
            this.instance = instance;
        }

        @Override
        public I getInstance() {
            return instance;
        }

        @Override
        public InstanceValidatable<I> setDefaultLevel(ValidationErrorLevel level) {
            return this;
        }

        @Override
        public ValidationErrorLevel getDefaultLevel() {
            return null;
        }

        @Override
        public ValidationError error(ValidationError error) {
            containsErrors = true;
            return null;
        }

        @Override
        public ValidationError error(String msg) {
            containsErrors = true;
            return null;
        }

        @Override
        public ValidationError error(ValidationErrorLevel level, ValidationError error) {
            containsErrors = true;
            return null;
        }

        @Override
        public ValidationError error(ValidationErrorLevel level, String msg) {
            containsErrors = true;
            return null;
        }
    }

}