package org.opensingular.lib.wicket.util.model;

import static org.junit.Assert.*;

import java.util.Arrays;
import java.util.HashSet;

import org.junit.Test;

public class ValueModelTest {

    @Test
    public void test() {
        ValueModel<Integer> model = new ValueModel<>(1);

        assertEquals(1, model.getObject().intValue());
        assertEquals(Integer.class, model.getObjectClass());

        assertEquals(
            new HashSet<>(Arrays.asList(new ValueModel<>(1), new ValueModel<>(2))),
            new HashSet<>(Arrays.asList(new ValueModel<>(1), new ValueModel<>(2), new ValueModel<>(1))));

        assertNotNull(model.toString());
    }

    @Test
    public void detach() {
        SupplierReloadableDetachableModel<Integer> reloadable = new SupplierReloadableDetachableModel<>(1, () -> 1);

        assertTrue(reloadable.isAttached());
        new ValueModel<>(reloadable).detach();
        assertFalse(reloadable.isAttached());
    }

}
