/*
 * Copyright (C) 2016 Singular Studios (a.k.a Atom Tecnologia) - www.opensingular.com
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.opensingular.form.script;

import com.google.common.base.Stopwatch;
import jdk.nashorn.api.scripting.JSObject;
import org.fest.assertions.api.Assertions;
import org.fest.assertions.api.ObjectAssert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.opensingular.form.*;

import javax.script.*;
import java.math.BigDecimal;
import java.util.Calendar;
import java.util.Collection;
import java.util.Set;

/**
 * @author Daniel Bordin
 */

@RunWith(Parameterized.class)
public class TestFormJavascriptUtil extends TestCaseForm {

    private STypeComposite<? extends SIComposite> curriculumType;
    private STypeComposite<SIComposite> infoType;
    private STypeList<STypeComposite<SIComposite>, SIComposite> certificationsType;

    public TestFormJavascriptUtil(TestFormConfig testFormConfig) {
        super(testFormConfig);
    }

    @Before
    public void setup() {
        PackageBuilder pkt = createTestDictionary().createNewPackage("pkt");
        curriculumType = pkt.createCompositeType("curriculum");

        infoType = curriculumType.addFieldComposite("info");
        infoType.addFieldString("name");
        infoType.addFieldInteger("age");
        infoType.addFieldInteger("scoreA");
        infoType.addFieldDecimal("scoreB");

        certificationsType = curriculumType.addFieldListOfComposite("certifications", "certification");
        STypeComposite<SIComposite> certificationType = this.certificationsType.getElementsType();
        certificationType.addFieldString("name");
        certificationType.addFieldDate("date");
    }

    @Test
    public void testSimpleCase() {
        SIComposite curriculum = curriculumType.newInstance();
        curriculum.setValue("info.name", "Daniel");
        curriculum.setValue("info.age", 41);
        curriculum.setValue("info.scoreA", 10);
        curriculum.setValue("info.scoreB", 20.5);
        SIComposite c1 = curriculum.getField(certificationsType).addNew();
        c1.setValue("name", "Java");
        c1.setValue("date", new Calendar.Builder().setDate(2014, Calendar.APRIL, 8).build());
        SIComposite c2 = curriculum.getField(certificationsType).addNew();
        c2.setValue("name", "Oracle");
        c2.setValue("date", new Calendar.Builder().setDate(2013, Calendar.MARCH, 20).build());

        assertScript(curriculum, "1+1").isEqualTo((Integer) 2);
        assertScript(curriculum, "info", "name").isEqualTo("Daniel");
        assertScript(curriculum, "info", "age").isEqualTo(41);
        assertScript(curriculum, "info", "scoreA").isEqualTo(10);
        assertScript(curriculum, "info", "age+scoreA").isEqualTo(51.0);
        assertScript(curriculum, "info", "scoreB").isEqualTo(new BigDecimal(20.5));
        assertScript(curriculum, "info", "scoreB+scoreB").isEqualTo(41.0);
        assertScript(curriculum, "info").isEqualTo(curriculum.getField("info"));
        assertScript(curriculum, "info.name").isEqualTo("Daniel");
        assertScript(curriculum, "info.age").isEqualTo(41);
        assertScript(curriculum, "info.name+info.age").isEqualTo("Daniel41");
        assertScript(curriculum, "info.scoreA+info.scoreB").isEqualTo(30.5);
        assertScript(curriculum, "info.fieldX").isEqualTo(null);
        assertScript(curriculum, "var t = 0; for(i = 0; i < 10; i++) {t += info.age}; t;").isEqualTo(410.0);
        assertScript(curriculum, "certifications").isEqualTo(curriculum.getField("certifications"));
        assertScript(curriculum, "certifications[0]").isEqualTo(curriculum.getField("certifications[0]"));
        assertScript(curriculum, "certifications[0]").isNotEqualTo(curriculum.getField("certifications[1]"));
        assertScript(curriculum, "certifications[0].name").isEqualTo("Java");
        assertScript(curriculum, "certifications[0].name+certifications[1].name").isEqualTo("JavaOracle");

        assertScript(curriculum, "info.name = 'Paulo'").isEqualTo("Paulo");
        assertInstance(curriculum).isValueEquals("info.name", "Paulo");

        assertScript(curriculum, "certifications[0].name = 'Java' + 2").isEqualTo("Java2");
        assertInstance(curriculum).isValueEquals("certifications[0].name", "Java2");

        assertScript(curriculum, "info.age = info.age + info.scoreA").isEqualTo(51.0);
        assertInstance(curriculum).isValueEquals("info.age", 51);

        assertScript(curriculum, "info.age = info.scoreB");
        assertInstance(curriculum).isValueEquals("info.age", 20);
    }

    private static ObjectAssert<Object> assertScript(SInstance instance, String script) {
        return assertScript(instance, null, script);
    }

    private static ObjectAssert<Object> assertScript(SInstance instance, String path, String script) {
        if (path != null) {
            instance = ((ICompositeInstance) instance).getField(path);
        }
        Object value = (Object) FormJavascriptUtil.compileAndEval(instance, script);
        return Assertions.assertThat(value);
    }
}
