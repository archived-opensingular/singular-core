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
import org.joda.time.DateTimeZone;
import org.joda.time.format.DateTimeFormat;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.opensingular.form.AbstractTestOneType;
import org.opensingular.form.SDictionary;

import java.util.Date;

import static org.assertj.core.api.Assertions.*;

@RunWith(Parameterized.class)
public class STypeDateTimeTest extends AbstractTestOneType<STypeDateTime, SIDateTime> {
    public STypeDateTimeTest(TestFormConfig testFormConfig) {
        super(testFormConfig, STypeDateTime.class);
    }

    @Test
    public void storesDateInISOFormat() {
        SIDateTime d         = newInstance();
        DateTime   reference = DateTime.parse("2016-01-01T05:21:33.000-02:00");
        d.setValue(reference.toDate());
        assertThat(d.toStringPersistence()).isEqualTo("2016-01-01T07:21:33.000+00:00");
    }

    @Test
    public void displaysDateInLatinFormat() {
        SIDateTime d         = newInstance();
        DateTime   reference = DateTime.parse("2016-01-01T05:21:33.000");
        d.setValue(reference.toDate());
        assertThat(d.toStringDisplayDefault()).isEqualTo("01/01/2016 05:21");
    }

    @Test
    public void selectLabelIsInLatinFormat() {
        SIDateTime d         = newInstance();
        DateTime   reference = DateTime.parse("2016-01-01T05:21:33.000");
        d.setValue(reference.toDate());
        assertThat(d.toStringDisplayDefault()).isEqualTo("01/01/2016 05:21");
    }

    @Test
    public void convertsFromISOForrmat() {
        SIDateTime d         = newInstance();
        Date       reference = DateTimeFormat.forPattern("yyyy-MM-dd'T'HH:mm:ss.SSSZZ").withZone(DateTimeZone.UTC).parseLocalDateTime("2016-01-01T05:21:33.000-02:00").toDate();
        d.setValue("2016-01-01T05:21:33.000-02:00");
        assertThat(d.getValue()).isEqualTo(reference);
    }

    @Test
    public void convertsLatinForrmat() {
        SIDateTime d         = newInstance();
        DateTime   reference = DateTime.parse("2016-01-01T05:21:00.000");
        d.setValue("01/01/2016 05:21");
        assertThat(d.getValue()).isEqualTo(reference.toDate());
    }

    @Test
    public void testPersistAndLoad() {
        STypeDateTime dateTime = SDictionary.create().getType(STypeDateTime.class);
        Date          d1       = new Date();
        String        value    = dateTime.toStringPersistence(d1);
        Date          d2       = dateTime.fromStringPersistence(value);

        Assert.assertEquals(d1, d2);

    }
}