package org.opensingular.lib.wicket.util.model;

import static org.junit.Assert.*;
import static org.opensingular.lib.wicket.util.util.Shortcuts.*;

import java.io.Serializable;

import org.apache.wicket.model.IWrapModel;
import org.junit.Test;

public class IMappingModelTest {

    @Test
    public void test() {
        IMappingModel<TestTO> model = IMappingModel.of($m.ofValue(new TestTO(1, "A")));

        IMappingModel<Integer> id = model.map(it -> it.id);
        IMappingModel<String> name = model.map(it -> it.name);

        assertEquals(1, id.getObject().intValue());
        assertEquals("A", name.getObject());
        assertNull(model.filter(it -> it.id == 2).getObject());

        IMappingModel<String> nameRW = model.map(it -> it.name, (it, v) -> {
            it.name = v;
        });
        nameRW.setObject("X");
        assertEquals("X", nameRW.getObject());

        model.setObject(new TestTO(2, "B"));
        assertEquals("B", nameRW.getObject());

        id.detach();
        name.detach();
        nameRW.detach();
    }

    @Test
    public void wrap() {
        ValueModel<TestTO> inner = $m.ofValue(new TestTO(1, "A"));
        IWrapModel<TestTO> wrap = IMappingModel.of(inner);
        assertSame(inner, wrap.getWrappedModel());
    }

    @Test(expected = UnsupportedOperationException.class)
    public void setObject() {
        IMappingModel.of($m.ofValue(new TestTO(1, "A")))
            .map(it -> it.name)
            .setObject("B");
    }

    private static class TestTO implements Serializable {
        public int    id;
        public String name;
        public TestTO(int id, String name) {
            this.id = id;
            this.name = name;
        }
    }
}
