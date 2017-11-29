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

import org.hamcrest.Matchers;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.opensingular.form.AbstractTestOneType;

import java.io.File;
import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(Parameterized.class)
public class STypeDecimalTest extends AbstractTestOneType<STypeDecimal, SIBigDecimal> {

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    STypeDecimal type = new STypeDecimal();

    public STypeDecimalTest(TestFormConfig testFormConfig) {
        super(testFormConfig, STypeDecimal.class);
    }

    @Test
    public void stringConversions() {
        assertThat(type.convert(null)).isNull();
        assertThat(type.convert("")).isNull();

        assertThat(type.convert("0")).isEqualTo(BigDecimal.ZERO);
        assertThat(type.convert("1")).isEqualTo(BigDecimal.ONE);
        assertThat(type.convert("-1")).isEqualTo(new BigDecimal("-1"));
        assertThat(type.convert("1,9")).isEqualTo(new BigDecimal("1.9"));
        assertThat(type.convert("00000000000001,9")).isEqualTo(new BigDecimal("1.9"));
        assertThat(type.convert("00.000.000.000.001,9")).isEqualTo(new BigDecimal("1.9"));
        assertThat(type.convert("-1,9")).isEqualTo(new BigDecimal("-1.9"));
    }

    @Test
    public void numberConversions() {
        assertThat(type.convert(0L)).isEqualTo(BigDecimal.ZERO);
        assertThat(type.convert(1L)).isEqualTo(BigDecimal.ONE);
        assertThat(type.convert(-1L)).isEqualTo(new BigDecimal("-1"));
        assertThat(type.convert(5.5)).isEqualTo(new BigDecimal("5.5"));
    }

    @Test
    public void unknownTypeException() {
        thrown.expect(RuntimeException.class);
        thrown.expectMessage(Matchers.containsString("não consegue converter o valor"));

        type.convert(new File(""));
    }

    @Test
    public void unparseableString() {
        thrown.expect(RuntimeException.class);
        thrown.expectMessage(Matchers.containsString("não consegue converter o valor"));

        type.convert("1XPTO");
    }

    @Test
    public void wrongSymbols() {
        thrown.expect(RuntimeException.class);
        thrown.expectMessage(Matchers.containsString("não consegue converter o valor"));

        type.convert("00,000,000,000,001.9");
    }
}
