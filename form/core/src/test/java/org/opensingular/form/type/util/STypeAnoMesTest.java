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

package org.opensingular.form.type.util;

import org.junit.Test;

import java.time.YearMonth;

import static org.assertj.core.api.Assertions.assertThat;

public class STypeAnoMesTest {

    STypeYearMonth type    = new STypeYearMonth();
    YearMonth      dec2015 = YearMonth.of(2015, 12),
            jan2015        = YearMonth.of(2015, 1),
            jan01          = YearMonth.of(1, 1);

    @Test
    public void formatingString() {
        assertThat(type.toStringPersistence(dec2015)).isEqualTo("12/2015");
        assertThat(type.toStringPersistence(jan2015)).isEqualTo("01/2015");
        assertThat(type.toStringPersistence(jan01)).isEqualTo("01/0001");
    }

    @Test
    public void convertingValueString() {
        assertThat(packUnpackString(dec2015)).isEqualTo(dec2015);
        assertThat(packUnpackString(jan2015)).isEqualTo(jan2015);
        assertThat(packUnpackString(jan01)).isEqualTo(jan01);
    }

    @Test
    public void convertingValueInteger() {
        assertThat(packUnpackInteger(dec2015)).isEqualTo(dec2015);
        assertThat(packUnpackInteger(jan2015)).isEqualTo(jan2015);
        assertThat(packUnpackInteger(jan01)).isEqualTo(jan01);
    }

    private YearMonth packUnpackString(YearMonth value) {
        String generatedStr = type.toStringPersistence(value);
        return type.convertNotNativeNotString(generatedStr);
    }

    private YearMonth packUnpackInteger(YearMonth value) {
        String generatedStr = type.toStringPersistence(value);
        Integer generatedInt = Integer.parseInt(generatedStr.replaceAll("[^0-9]+", ""));
        return type.convertNotNativeNotString(generatedInt);
    }

}
