/*
 * Copyright (C) 2016 Singular Studios (a.k.a Atom Tecnologia) - www.opensingular.com
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
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

package org.opensingular.form.persistence.relational;

import static org.opensingular.form.persistence.relational.RelationalSQLCriteria.and;
import static org.opensingular.form.persistence.relational.RelationalSQLCriteria.isBetween;
import static org.opensingular.form.persistence.relational.RelationalSQLCriteria.isEqualTo;
import static org.opensingular.form.persistence.relational.RelationalSQLCriteria.isLike;
import static org.opensingular.form.persistence.relational.RelationalSQLCriteria.isNull;
import static org.opensingular.form.persistence.relational.RelationalSQLCriteria.not;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.opensingular.form.SDictionary;
import org.opensingular.form.SIComposite;
import org.opensingular.form.SInfoPackage;
import org.opensingular.form.SInfoType;
import org.opensingular.form.SPackage;
import org.opensingular.form.SType;
import org.opensingular.form.STypeComposite;
import org.opensingular.form.TestCaseForm;
import org.opensingular.form.TypeBuilder;
import org.opensingular.form.persistence.relational.RelationalSQLCriteriaTest.TestPackage.MasterEntity;
import org.opensingular.form.type.core.STypeInteger;
import org.opensingular.form.type.core.STypeString;

/**
 * @author Edmundo Andrade
 */
@RunWith(Parameterized.class)
public class RelationalSQLCriteriaTest extends TestCaseForm {
    private SDictionary dictionary;
    private MasterEntity master;
    private Map<SType<?>, String> fieldToColumnMap;

    public RelationalSQLCriteriaTest(TestFormConfig testFormConfig) {
        super(testFormConfig);
    }

    @Before
    public void setUp() {
        dictionary = createTestDictionary();
        master = dictionary.getType(MasterEntity.class);
        fieldToColumnMap = new HashMap<>();
        fieldToColumnMap.put(master.name, "T1.name");
        fieldToColumnMap.put(master.observation, "T1.obs");
        fieldToColumnMap.put(master.maxItems, "T1.max");
    }

    @Test
    public void isEqualToCriteria() {
        List<Object> params = new ArrayList<>();
        String sql = isEqualTo(master.name, "Albert").toSQL(fieldToColumnMap, params);
        assertEquals("T1.name = ?", sql);
        assertEquals(1, params.size());
        assertEquals("Albert", params.get(0));
    }

    @Test
    public void isNullCriteria() {
        List<Object> params = new ArrayList<>();
        String sql = isNull(master.observation).toSQL(fieldToColumnMap, params);
        assertEquals("T1.obs IS NULL", sql);
        assertEquals(0, params.size());
    }

    @Test
    public void isBetweenCriteria() {
        List<Object> params = new ArrayList<>();
        String sql = isBetween(master.maxItems, 1, 9).toSQL(fieldToColumnMap, params);
        assertEquals("T1.max BETWEEN ? AND ?", sql);
        assertEquals(2, params.size());
        assertEquals(1, params.get(0));
        assertEquals(9, params.get(1));
    }

    @Test
    public void multpleCriteria() {
        List<Object> params = new ArrayList<>();
        String sql = and(isLike(master.name, "Antony%"), not(isBetween(master.maxItems, 7, 15))).toSQL(fieldToColumnMap,
                params);
        assertEquals("T1.name LIKE ? AND NOT (T1.max BETWEEN ? AND ?)", sql);
        assertEquals(3, params.size());
        assertEquals("Antony%", params.get(0));
        assertEquals(7, params.get(1));
        assertEquals(15, params.get(2));
    }

    @SInfoPackage(name = "testPackage")
    public static final class TestPackage extends SPackage {
        @SInfoType(name = "MasterEntity", spackage = TestPackage.class)
        public static final class MasterEntity extends STypeComposite<SIComposite> {
            public STypeString name;
            public STypeString observation;
            public STypeInteger maxItems;

            @Override
            protected void onLoadType(TypeBuilder tb) {
                asAtr().label("Master entity");
                name = addFieldString("name");
                observation = addFieldString("observation");
                maxItems = addFieldInteger("maxItems");
                // relational mapping
                asSQL().table().tablePK("id");
                name.asSQL().column();
                observation.asSQL().column("obs");
                maxItems.asSQL().column("max");
            }
        }
    }
}