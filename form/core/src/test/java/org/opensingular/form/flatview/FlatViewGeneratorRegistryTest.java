package org.opensingular.form.flatview;

import org.hamcrest.Matchers;
import org.junit.Before;
import org.junit.Test;
import org.opensingular.form.PackageBuilder;
import org.opensingular.form.SDictionary;
import org.opensingular.form.SIComposite;
import org.opensingular.form.STypeComposite;
import org.opensingular.form.flatview.mapper.BlockFlatViewGenerator;
import org.opensingular.form.flatview.mapper.SelectionFlatViewGenerator;
import org.opensingular.form.flatview.mapper.TabFlatViewGenerator;
import org.opensingular.form.view.SViewByBlock;
import org.opensingular.form.view.SViewSelectionBySelect;
import org.opensingular.form.view.SViewTab;

import static org.junit.Assert.*;

public class FlatViewGeneratorRegistryTest {
    PackageBuilder fooBarpackage;

    @Before
    public void setUp() throws Exception {
        fooBarpackage = SDictionary.create().createNewPackage("foo.bar");
    }

    @Test
    public void testRetrieveByBlock() throws Exception {
        STypeComposite<SIComposite> foobar = fooBarpackage.createCompositeType("foobar");
        foobar.withView(new SViewByBlock());
        assertThat(foobar.getAspect(FlatViewGenerator.ASPECT_FLAT_VIEW_GENERATOR).orElse(null), Matchers.instanceOf(BlockFlatViewGenerator.class));
    }

    @Test
    public void testRetrieveByTab() throws Exception {
        STypeComposite<SIComposite> foobar = fooBarpackage.createCompositeType("foobar");
        foobar.withView(new SViewTab());
        assertThat(foobar.getAspect(FlatViewGenerator.ASPECT_FLAT_VIEW_GENERATOR).orElse(null), Matchers.instanceOf(TabFlatViewGenerator.class));
    }

    @Test
    public void testRetrieveBySelection() throws Exception {
        STypeComposite<SIComposite> foobar = fooBarpackage.createCompositeType("foobar");
        foobar.withView(new SViewSelectionBySelect());
        assertThat(foobar.getAspect(FlatViewGenerator.ASPECT_FLAT_VIEW_GENERATOR).orElse(null), Matchers.instanceOf(SelectionFlatViewGenerator.class));
    }
}