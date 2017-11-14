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

package org.opensingular.form.type.core;

import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.opensingular.form.SDictionary;
import org.opensingular.form.type.util.SIYearMonth;
import org.opensingular.form.type.util.STypeYearMonth;

import java.time.YearMonth;
import java.util.Arrays;

import static org.assertj.core.api.Assertions.assertThat;


@RunWith(value = Parameterized.class)
public class STypeYearMonthTest {

    private static STypeYearMonth type;

    private final YearMonth result;
    private final String persistent;
    private final SIYearMonth value;

    public STypeYearMonthTest(String input, YearMonth result, String persistent){

        this.value = type.newInstance();
        this.value.setValue(input);
        this.result = result;
        this.persistent = persistent;
    }

    @BeforeClass
    public static void setupType(){
        SDictionary dict = SDictionary.create();
        type = dict.getType(STypeYearMonth.class);
    }

    @Parameterized.Parameters(name = "{index}: ({0})")
    public static Iterable<Object[]> data1() {
        return Arrays.asList(new Object[][] {
                { "", null , null },
                { "11/2016", YearMonth.of(2016,11) , "11/2016" },
//                { "1/2016", YearMonth.of(2016,1) , "01/2016" },
//                { "12016", YearMonth.of(2016,1) , "01/2016" },
//                { "012016", YearMonth.of(2016,1) , "01/2016" },
        });
    }

    @Test public void convertInputToData() {
        assertThat(value.getValue()).isEqualTo(result);
    }

    @Test public void convertInputToPersistent(){
        assertThat(value.toStringPersistence()).isEqualTo(persistent);
    }

}
