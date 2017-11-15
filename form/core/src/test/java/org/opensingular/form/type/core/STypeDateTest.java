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

import org.joda.time.DateTime;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.opensingular.form.AbstractTestOneType;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(Parameterized.class)
public class STypeDateTest extends AbstractTestOneType<STypeDate, SIDate> {

    public STypeDateTest(TestFormConfig testFormConfig) {
        super(testFormConfig, STypeDate.class);
    }

    @Test public void storesDateInISOFormat(){
        SIDate d = newInstance();

        DateTime reference = DateTime.parse("2016-01-01");
        d.setValue(reference.toDate());
        assertThat(d.toStringPersistence()).isEqualTo("2016-01-01");
    }

    @Test public void displaysDateInLatinFormat(){
        SIDate d = newInstance();

        DateTime reference = DateTime.parse("2016-01-01");
        d.setValue(reference.toDate());
        assertThat(d.toStringDisplayDefault()).isEqualTo("01/01/2016");
    }

    @Test public void selectLabelIsInLatinFormat(){
        SIDate d = newInstance();

        DateTime reference = DateTime.parse("2016-01-01");
        d.setValue(reference.toDate());
        assertThat(d.toStringDisplayDefault()).isEqualTo("01/01/2016");
    }

    @Test public void convertsFromISOForrmat(){
        SIDate d = newInstance();

        DateTime reference = DateTime.parse("2016-01-01");
        d.setValue("2016-01-01");
        assertThat(d.getValue()).isEqualTo(reference.toDate());
    }

    @Test public void convertsLatinForrmat(){
        SIDate d = newInstance();

        DateTime reference = DateTime.parse("2016-01-01");
        d.setValue("01/01/2016");
        assertThat(d.getValue()).isEqualTo(reference.toDate());
    }

    @Ignore
    @Test(expected = Exception.class) public void rejectsNotStandartFormart(){
        newInstance().setValue("2016/01/01");
    }
}
