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

package org.opensingular.form.validation;

import org.junit.Assert;
import org.junit.Test;
import org.opensingular.form.SDictionary;
import org.opensingular.form.SIComposite;
import org.opensingular.form.SInstance;
import org.opensingular.form.SType;
import org.opensingular.form.SingularFormProcessing;

import java.util.Collection;
import java.util.Set;

public class TestCircularDependencyValidation {

    @Test
    public void testCircularValidation() throws Exception {
        SDictionary dictionary = SDictionary.create();
        SIComposite instance = dictionary.newInstance(STypeCircularValidationOnFieldProcess.class);
        STypeCircularValidationOnFieldProcess type = (STypeCircularValidationOnFieldProcess) instance.getType();

        new InstanceValidationContext().validateAll(instance);

        Set<SInstance> visitedInstances = SingularFormProcessing.executeFieldProcessLifecycle(instance.getField(type.whatever), false);

        Assert.assertEquals("whatever", visitedInstances.stream().map(SInstance::getType).map(SType::getNameSimple).filter("whatever"::equals).findFirst().orElse(""));
        Assert.assertEquals("whoever", visitedInstances.stream().map(SInstance::getType).map(SType::getNameSimple).filter("whoever"::equals).findFirst().orElse(""));

        Collection<ValidationError> errors =  instance.getDocument().getValidationErrors();

        Assert.assertEquals(2, errors.stream().distinct().count());
        errors.forEach(System.out::println);
    }



    @Test
    public void testCircularValidationWithRequired() throws Exception {
        SDictionary dictionary = SDictionary.create();
        SIComposite instance = dictionary.newInstance(STypeCircularValidationWithRequiredFields.class);
        STypeCircularValidationWithRequiredFields type = (STypeCircularValidationWithRequiredFields) instance.getType();

        System.out.println("Validate all: ");
        new InstanceValidationContext().validateAll(instance);
        Collection<ValidationError> errors =  instance.getDocument().getValidationErrors();
        errors.forEach(System.out::println);

        instance.setValue(type.whoever, "nada");

        SingularFormProcessing.executeFieldProcessLifecycle(instance.getField(type.whoever), false);

        System.out.println("Validate on 'whoever' field process:");
        errors =  instance.getDocument().getValidationErrors();
        errors.forEach(System.out::println);
        Assert.assertEquals(1, errors.stream().distinct().count());
    }
}
