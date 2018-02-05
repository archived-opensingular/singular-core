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

package org.opensingular.form.wicket.mapper.datetime;

import org.apache.commons.lang3.tuple.Pair;
import org.apache.wicket.Component;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.util.tester.FormTester;
import org.apache.wicket.util.visit.IVisit;
import org.apache.wicket.util.visit.IVisitor;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.opensingular.form.SIComposite;
import org.opensingular.form.STypeComposite;
import org.opensingular.form.type.core.STypeDateTime;
import org.opensingular.form.view.SViewDateTime;
import org.opensingular.form.wicket.helpers.SingularFormDummyPageTester;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DateTimeContainerTest implements Serializable {


    private transient FormTester                  formTester;
    private transient SingularFormDummyPageTester tester;

    @Before
    public void newTestPage() {
        tester = new SingularFormDummyPageTester();
        tester.getDummyPage().setTypeBuilder(this::buildSType);

        tester.getDummyPage().setAsEditView();
        tester.startDummyPage();

        formTester = tester.newFormTester();
    }

    private SIComposite getRootInstance() {
        return (SIComposite) tester.getDummyPage().getSingularFormPanel().getInstance();
    }

    public void buildSType(STypeComposite<SIComposite> composite) {
        STypeDateTime dateTime = composite.addFieldDateTime("teste");
        dateTime.withView(SViewDateTime::new);
    }

    /**
     * Par nome do campo, valor do campo em string
     *
     * @param
     */
    private void setValues(Pair<String, String> value1, Pair<String, String> value2) {
        Map<String, Component> components =new HashMap<>();
        formTester.getForm().visitChildren(TextField.class, new IVisitor<TextField, Object>() {
            @Override
            public void component(TextField object, IVisit<Object> visit) {
                components.put(object.getId(), object);
            }
        });
        formTester.setValue(components.get(value1.getKey()), value1.getValue());
        formTester.setValue(components.get(value2.getKey()), value2.getValue());

        formTester.submit();
    }


    @Test
    public void setDdateThenTime() {
        setValues(Pair.of("date", "25/11/2012"), Pair.of("time", "8:15"));

        Calendar calendar = GregorianCalendar.getInstance();
        calendar.clear();
        calendar.set(Calendar.YEAR, 2012);
        calendar.set(Calendar.MONTH, Calendar.NOVEMBER);
        calendar.set(Calendar.DAY_OF_MONTH, 25);
        calendar.set(Calendar.HOUR, 8);
        calendar.set(Calendar.MINUTE, 15);

        Date expectedDate = calendar.getTime();

        Assert.assertEquals(expectedDate, getRootInstance().getField("teste").getValue());
    }

    @Test
    public void setTimeThenDate() {
        setValues(Pair.of("time", "8:15"), Pair.of("date", "25/11/2012"));

        Calendar calendar = GregorianCalendar.getInstance();
        calendar.clear();
        calendar.set(Calendar.YEAR, 2012);
        calendar.set(Calendar.MONTH, Calendar.NOVEMBER);
        calendar.set(Calendar.DAY_OF_MONTH, 25);
        calendar.set(Calendar.HOUR, 8);
        calendar.set(Calendar.MINUTE, 15);

        Date expectedDate = calendar.getTime();

        Assert.assertEquals(expectedDate, getRootInstance().getField("teste").getValue());
    }


}
