package org.opensingular.form.wicket.mapper.masterdetail;

import com.google.common.collect.Lists;
import org.apache.wicket.model.IModel;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.opensingular.form.*;
import org.opensingular.form.type.basic.SPackageBasic;
import org.opensingular.form.type.core.SIString;
import org.opensingular.form.type.core.STypeString;
import org.opensingular.lib.wicket.util.util.Shortcuts;

import java.util.Arrays;
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

        MasterDetailPanel.SIListDataProvider dataProvider = new MasterDetailPanel.SIListDataProvider(model);

        list.addElement(sistring("01"));
        list.addElement(sistring("02"));
        list.addElement(sistring("03"));
        list.addElement(sistring("04"));

        List<SInstance> page1 = Lists.newArrayList(dataProvider.iterator(0, 2, "", true));
        List<SInstance> page2 = Lists.newArrayList(dataProvider.iterator(2, 2, "", true));

        Assert.assertNotEquals(page1, page2);
        Assert.assertEquals(page1, Arrays.asList(sistring("01"), sistring("02")));
        Assert.assertEquals(page2, Arrays.asList(sistring("03"), sistring("04")));
    }
}
