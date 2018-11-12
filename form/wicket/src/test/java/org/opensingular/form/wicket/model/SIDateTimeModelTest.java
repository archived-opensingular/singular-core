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

package org.opensingular.form.wicket.model;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.opensingular.form.PackageBuilder;
import org.opensingular.form.SIComposite;
import org.opensingular.form.STypeComposite;
import org.opensingular.form.document.RefDictionary;
import org.opensingular.form.document.RefType;
import org.opensingular.form.document.SDocumentFactory;
import org.opensingular.form.type.core.SIDate;
import org.opensingular.form.type.core.SITime;
import org.opensingular.form.wicket.model.SIDateTimeModel.DateModel;
import org.opensingular.form.wicket.model.SIDateTimeModel.TimeModel;
import org.opensingular.lib.commons.lambda.IConsumer;

import javax.annotation.Nonnull;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.Date;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

public class SIDateTimeModelTest {

    private String                      dateString;
    private Date                        dateObj;
    private String                      timeString;
    private Date                        timeObj;
    private SInstanceFieldModel<SIDate> mDateField;
    private SInstanceValueModel<Date>   mDateValue;
    private SInstanceFieldModel<SITime> mTimeField;
    private SInstanceValueModel<Date>   mTimeValue;

    @Before
    public void setUp() throws ParseException {
        dateString = "01/01/2000";
        dateObj = new SimpleDateFormat("dd/MM/yyyy").parse(dateString);
        timeString = "14:35";
        timeObj = new SimpleDateFormat("HH:mm").parse(timeString);

        SIComposite form = newInstance(it -> {
            it.addFieldDate("date");
            it.addFieldTime("time");
        });
        SInstanceRootModel<SIComposite> mForm = new SInstanceRootModel<>(form);

        mDateField = new SInstanceFieldModel<>(mForm, "date");
        mDateValue = new SInstanceValueModel<>(mDateField);
        mTimeField = new SInstanceFieldModel<>(mForm, "time");
        mTimeValue = new SInstanceValueModel<>(mTimeField);
    }

    @Nonnull
    private static SIComposite newInstance(IConsumer<STypeComposite<SIComposite>> typeBuilder) {
        RefType ref = RefDictionary.newBlank().refType((dic) -> {
            PackageBuilder pkg = dic.get().createNewPackage("dummy");
            STypeComposite<SIComposite> form = pkg.createCompositeType("form");
            typeBuilder.accept(form);
            return form;
        });
        return (SIComposite) SDocumentFactory.empty().createInstance(ref);
    }

    @Test
    public void testDate() {
        DateModel mDate = new SIDateTimeModel.DateModel(mDateValue);
        assertEquals("dummy.form.date", mDate.getSInstance().getType().getName());
        assertNull(mDate.getObject());

        mDate.setObject(dateString);
        assertEquals(dateString, mDate.getObject());
        assertEquals(dateObj, mDateField.getObject().getValue());

        mDate.detach();
    }

    @Test
    public void testDateConverionToLocalDate() {
        TimeModel mTime = new SIDateTimeModel.TimeModel(mTimeValue);
        mTime.setObject("08:00");
        LocalTime hora = mTime.getDate().toInstant().atZone(ZoneId.systemDefault()).toLocalTime();
        Assert.assertEquals(8, hora.getHour());
        Assert.assertEquals(0, hora.getMinute());
        System.out.println(hora);
    }



    @Test
    public void testDateTwice() {
        DateModel mDate = new SIDateTimeModel.DateModel(mDateValue);
        mDate.setObject(dateString);
        assertNotNull(mDate.getObject());
        mDate.setObject(dateString);
        assertNotNull(mDate.getObject());
    }

    @Test
    public void testDateNull() {
        DateModel mDate = new SIDateTimeModel.DateModel(mDateValue);
        mDate.setObject(dateString);
        assertNotNull(mDate.getObject());
        mDate.setObject(null);
        assertNull(mDate.getObject());
    }

    @Test
    public void testDateInvalid() {
        DateModel mDate = new SIDateTimeModel.DateModel(mDateValue);
        mDate.setObject(dateString);
        assertNotNull(mDate.getObject());
        mDate.setObject("INVALID VALUE");
        assertNull(mDate.getObject());
    }

    @Test
    public void testTime() {
        TimeModel mTime = new SIDateTimeModel.TimeModel(mTimeValue);
        assertEquals("dummy.form.time", mTime.getSInstance().getType().getName());
        assertNull(mTime.getObject());

        mTime.setObject(timeString);
        assertEquals(timeString, mTime.getObject());
        assertEquals(timeObj, mTimeField.getObject().getValue());

        mTime.detach();
    }

    @Test
    public void testTimeTwice() {
        TimeModel mTime = new SIDateTimeModel.TimeModel(mTimeValue);
        mTime.setObject(timeString);
        assertEquals(timeString, mTime.getObject());
        mTime.setObject(timeString);
        assertEquals(timeString, mTime.getObject());
    }

    @Test
    public void testTimeNull() {
        TimeModel mTime = new SIDateTimeModel.TimeModel(mTimeValue);
        mTime.setObject(timeString);
        assertEquals(timeString, mTime.getObject());
        mTime.setObject(null);
        assertNull(mTime.getObject());
    }

    @Test
    public void testTimeInvalid() {
        TimeModel mTime = new SIDateTimeModel.TimeModel(mTimeValue);
        mTime.setObject(timeString);
        assertEquals(timeString, mTime.getObject());
        mTime.setObject("INVALID VALUE");
        assertNull(mTime.getObject());
    }
}
