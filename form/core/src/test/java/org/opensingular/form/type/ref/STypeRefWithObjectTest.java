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

package org.opensingular.form.type.ref;

import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.opensingular.form.*;
import org.opensingular.form.document.SDocument;
import org.opensingular.form.provider.ProviderContext;

import java.io.Serializable;
import java.util.Collections;
import java.util.List;

@RunWith(Parameterized.class)
public class STypeRefWithObjectTest extends TestCaseForm {

    public STypeRefWithObjectTest(TestFormConfig testFormConfig) {
        super(testFormConfig);
    }

    @Test
    public void testRef() throws Exception {
        SDictionary dictionary = createTestDictionary();
        PackageBuilder packageBuilder = dictionary.createNewPackage("br.foo.bar");
        STypeComposite<SIComposite> root = packageBuilder.createCompositeType("root");
        root.addField("car", CarRef.class);
        SIComposite iRoot = root.newInstance();
        SInstance car = iRoot.getField("car");
        List<Serializable> values = car.asAtrProvider().getProvider().load(ProviderContext.of(car));
        car.asAtrProvider().getConverter().fillInstance(car, values.get(0));
        Assert.assertThat(car.getValue("display"), Matchers.equalTo("Fox - Silver - 100km/h"));
        Assert.assertThat(car.getValue("key"), Matchers.equalTo("1"));
    }

    @SInfoPackage(name = "foo.bar")
    public static class CarPackage extends SPackage {

    }

    public static class Car {
        public Integer cod;
        public String model;
        public String color;
        public String maxSpeed;
    }

    @SInfoType(name = "CarRef", spackage = CarPackage.class)
    public static class CarRef extends STypeRef<Car> {
        @Override
        protected String getKeyValue(Car car) {
            return String.valueOf(car.cod);
        }

        @Override
        protected String getDisplayValue(Car car) {
            return String.format("%s - %s - %s", car.model, car.color, car.maxSpeed);
        }

        @Override
        protected List<Car> loadValues(SDocument document) {
            Car car1 = new Car();
            car1.cod = 1;
            car1.model = "Fox";
            car1.color = "Silver";
            car1.maxSpeed = "100km/h";
            return Collections.singletonList(car1);
        }
    }

}