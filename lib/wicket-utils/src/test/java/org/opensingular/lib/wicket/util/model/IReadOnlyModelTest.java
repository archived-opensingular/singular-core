package org.opensingular.lib.wicket.util.model;

import static org.junit.Assert.*;

import org.junit.Test;

public class IReadOnlyModelTest {

    @Test
    public void test() {
        assertTrue(IReadOnlyModel.of(() -> true).getObject());
        assertFalse(IReadOnlyModel.of(() -> false).getObject());

        IReadOnlyModel.of(() -> false).detach();
    }

    @Test(expected = UnsupportedOperationException.class)
    public void testError() {
        IReadOnlyModel.of(() -> false).setObject(true);
    }

}
