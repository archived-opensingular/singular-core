package org.opensingular.lib.wicket.util.model;

import static org.junit.Assert.*;

import org.junit.Test;

public class SupplierReloadableDetachableModelTest {

    @Test
    public void test() {
        boolean[] needsReload = { false };
        int[] n = { 0 };
        SupplierReloadableDetachableModel<Integer> model = new SupplierReloadableDetachableModel<Integer>(0, () -> ++n[0]) {
            @Override
            protected boolean needsReload(Integer cachedObject) {
                return super.needsReload(cachedObject) || needsReload[0];
            }
        };
        model.toString();

        assertTrue(model.isAttached());
        assertEquals(0, model.getObject().intValue());
        assertEquals(0, model.getObject().intValue());
        assertTrue(model.isAttached());

        model.detach();

        assertFalse(model.isAttached());
        assertEquals(1, model.getObject().intValue());
        assertEquals(1, model.getObject().intValue());
        assertTrue(model.isAttached());

        model.detach();

        assertFalse(model.isAttached());

        model.setObject(5);

        assertTrue(model.isAttached());
        assertEquals(5, model.getObject().intValue());

        needsReload[0] = true;
        assertEquals(2, model.getObject().intValue());
    }
}
