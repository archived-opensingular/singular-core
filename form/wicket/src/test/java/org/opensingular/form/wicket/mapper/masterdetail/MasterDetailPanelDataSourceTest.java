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

package org.opensingular.form.wicket.mapper.masterdetail;

import com.google.common.collect.Lists;
import org.apache.wicket.model.IModel;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.opensingular.form.PackageBuilder;
import org.opensingular.form.SDictionary;
import org.opensingular.form.SIList;
import org.opensingular.form.SInstance;
import org.opensingular.form.STypeList;
import org.opensingular.form.helpers.AssertionsSInstance;
import org.opensingular.form.type.core.SIString;
import org.opensingular.form.type.core.STypeString;
import org.opensingular.lib.wicket.util.util.Shortcuts;

import java.util.List;

public class MasterDetailPanelDataSourceTest {
    private SDictionary dict = SDictionary.create();
    private PackageBuilder pkg = dict.createNewPackage("test");
    private STypeString stString;
    private STypeList<STypeString, SIString> stStringList;

    @Before
    public void setUp() {
        stString = pkg.getType(STypeString.class);
        stStringList = pkg.createListTypeOf("stringList", STypeString.class);
    }

    private SIString sistring(String s) {
        SIString si = stString.newInstance();
        si.setValue(s);
        return si;
    }

    @Test
    public void dataProvider() {
        IModel<SIList<SInstance>> model = Shortcuts.$m.loadable(() -> stStringList.newInstance(SInstance.class));
        SIList<SInstance> list = model.getObject();

        MasterDetailDataProvider dataProvider = new MasterDetailDataProvider(model, null);

        list.addElement(sistring("01"));
        list.addElement(sistring("02"));
        list.addElement(sistring("03"));
        list.addElement(sistring("04"));

        List<SInstance> page1 = Lists.newArrayList(dataProvider.iterator(0, 2, "", true));
        List<SInstance> page2 = Lists.newArrayList(dataProvider.iterator(2, 2, "", true));

        Assert.assertEquals(2, page1.size());
        new AssertionsSInstance(page1.get(0)).isInstanceOf(SIString.class).isValueEquals("01");
        new AssertionsSInstance(page1.get(1)).isInstanceOf(SIString.class).isValueEquals("02");
        Assert.assertEquals(2, page2.size());
        new AssertionsSInstance(page2.get(0)).isInstanceOf(SIString.class).isValueEquals("03");
        new AssertionsSInstance(page2.get(1)).isInstanceOf(SIString.class).isValueEquals("04");
    }
}
