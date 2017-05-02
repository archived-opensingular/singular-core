package org.opensingular.lib.wicket.util.model;

import static org.junit.Assert.*;
import static org.opensingular.lib.wicket.util.util.Shortcuts.*;

import org.apache.wicket.model.IModel;
import org.junit.Test;

public class FallbackReadOnlyModelTest {

    @Test
    public void test() {
        IModel<Integer> a = $m.ofValue();
        IModel<Integer> b = $m.ofValue();

        FallbackReadOnlyModel<Integer> model = new FallbackReadOnlyModel<>(a, b);

        assertNull(model.getObject());

        a.setObject(1);
        b.setObject(2);
        assertEquals(1, model.getObject().intValue());

        a.setObject(null);
        assertEquals(2, model.getObject().intValue());

        model.detach();
    }

}
