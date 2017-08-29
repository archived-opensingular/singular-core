package org.opensingular.form.wicket.model;

import static org.junit.Assert.*;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.junit.Before;
import org.junit.Test;
import org.opensingular.form.PackageBuilder;
import org.opensingular.form.SDictionary;
import org.opensingular.form.SIComposite;
import org.opensingular.form.STypeComposite;
import org.opensingular.form.document.RefType;
import org.opensingular.form.document.SDocumentFactory;
import org.opensingular.form.type.core.SIDate;
import org.opensingular.form.type.core.SITime;
import org.opensingular.form.wicket.model.SIDateTimeModel.DateModel;
import org.opensingular.form.wicket.model.SIDateTimeModel.TimeModel;
import org.opensingular.lib.commons.lambda.IConsumer;

public class SIDateTimeModelTest {

    private String                          dateString;
    private Date                            dateObj;
    private String                          timeString;
    private Date                            timeObj;
    private SIComposite                     form;
    private SInstanceRootModel<SIComposite> mForm;
    private SInstanceFieldModel<SIDate>     mDateField;
    private SInstanceValueModel<Date>       mDateValue;
    private SInstanceFieldModel<SITime>     mTimeField;
    private SInstanceValueModel<Date>       mTimeValue;

    @Before
    public void setUp() throws ParseException {
        dateString = "01/01/2000";
        dateObj = new SimpleDateFormat("dd/MM/yyyy").parse(dateString);
        timeString = "14:35";
        timeObj = new SimpleDateFormat("HH:mm").parse(timeString);
        form = newInstance(it -> {
            it.addFieldDate("date");
            it.addFieldTime("time");
        });
        mForm = new SInstanceRootModel<>(form);
        mDateField = new SInstanceFieldModel<>(mForm, "date");
        mDateValue = new SInstanceValueModel<>(mDateField);
        mTimeField = new SInstanceFieldModel<>(mForm, "time");
        mTimeValue = new SInstanceValueModel<>(mTimeField);
    }

    private static SIComposite newInstance(IConsumer<STypeComposite<SIComposite>> typeBuilder) {
        RefType ref = RefType.of(() -> {
            SDictionary dic = SDictionary.create();
            PackageBuilder pkg = dic.createNewPackage("dummy");
            STypeComposite<SIComposite> form = pkg.createCompositeType("form");
            typeBuilder.accept(form);
            return form;
        });
        return (SIComposite) SDocumentFactory.empty().createInstance(ref);
    }

    @Test
    public void testDate() throws ParseException {
        DateModel mDate = new SIDateTimeModel.DateModel(mDateValue);
        assertEquals("dummy.form.date", mDate.getSInstance().getType().getName());
        assertNull(mDate.getObject());

        mDate.setObject(dateString);
        assertEquals(dateString, mDate.getObject());
        assertEquals(dateObj, mDateField.getObject().getValue());

        mDate.detach();
    }

    @Test
    public void testDateTwice() throws ParseException {
        DateModel mDate = new SIDateTimeModel.DateModel(mDateValue);
        mDate.setObject(dateString);
        assertNotNull(mDate.getObject());
        mDate.setObject(dateString);
        assertNotNull(mDate.getObject());
    }

    @Test
    public void testDateNull() throws ParseException {
        DateModel mDate = new SIDateTimeModel.DateModel(mDateValue);
        mDate.setObject(dateString);
        assertNotNull(mDate.getObject());
        mDate.setObject(null);
        assertNull(mDate.getObject());
    }

    @Test
    public void testDateInvalid() throws ParseException {
        DateModel mDate = new SIDateTimeModel.DateModel(mDateValue);
        mDate.setObject(dateString);
        assertNotNull(mDate.getObject());
        mDate.setObject("xxx");
        assertNull(mDate.getObject());
    }

    @Test
    public void testTime() throws ParseException {

        TimeModel mTime = new SIDateTimeModel.TimeModel(mTimeValue);
        assertEquals("dummy.form.time", mTime.getSInstance().getType().getName());
        assertNull(mTime.getObject());

        mTime.setObject(timeString);
        assertEquals(timeString, mTime.getObject());
        assertEquals(timeObj, mTimeField.getObject().getValue());

        mTime.setObject(null);
        assertNull(mTime.getObject());

        mTime.setObject(timeString);
        assertEquals(timeString, mTime.getObject());
        assertEquals(timeObj, mTimeField.getObject().getValue());

        mTime.setObject("INVALID VALUE");
        assertNull(mTime.getObject());

        mTime.detach();
    }

    @Test
    public void testTimeTwice() throws ParseException {
        TimeModel mTime = new SIDateTimeModel.TimeModel(mTimeValue);
        mTime.setObject(timeString);
        assertEquals(timeString, mTime.getObject());
        mTime.setObject(timeString);
        assertEquals(timeString, mTime.getObject());
    }

    @Test
    public void testTimeNull() throws ParseException {
        TimeModel mTime = new SIDateTimeModel.TimeModel(mTimeValue);
        mTime.setObject(timeString);
        assertEquals(timeString, mTime.getObject());
        mTime.setObject(null);
        assertNull(mTime.getObject());
    }

    @Test
    public void testTimeInvalid() throws ParseException {
        TimeModel mTime = new SIDateTimeModel.TimeModel(mTimeValue);
        mTime.setObject(timeString);
        assertEquals(timeString, mTime.getObject());
        mTime.setObject("INVALID VALUE");
        assertNull(mTime.getObject());
    }
}
