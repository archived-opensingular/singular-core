package org.opensingular.lib.wicket.util.util;

import static org.opensingular.lib.wicket.util.util.Shortcuts.*;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.IModel;
import org.junit.Assert;
import org.junit.Test;
import org.opensingular.lib.commons.lambda.IFunction;
import org.opensingular.lib.commons.lambda.ISupplier;

public class IModelsMixinTest {

    @Test
    public void compound() {
        CompoundPropertyModel<TestTO> model = $m.compound($m.ofValue(new TestTO()));
        Assert.assertEquals("A", model.bind("a").getObject());
        Assert.assertEquals("B", model.bind("b").getObject());
        Assert.assertEquals("C", model.bind("c").getObject());
    }

    @Test
    public void compoundOf() {
        CompoundPropertyModel<TestTO> model = $m.compoundOf(new TestTO());
        Assert.assertEquals("A", model.bind("a").getObject());
        Assert.assertEquals("B", model.bind("b").getObject());
        Assert.assertEquals("C", model.bind("c").getObject());
    }

    @Test
    public void conditional() {
        IModel<Boolean> condition = $m.ofValue(true);
        IModel<Integer> model = $m.conditional(condition, $m.ofValue(1), $m.ofValue(0));
        condition.setObject(true);
        Assert.assertEquals(1, model.getObject().intValue());
        condition.setObject(false);
        Assert.assertEquals(0, model.getObject().intValue());
        model.detach();
    }

    @Test
    public void get() {
        TestTO to = new TestTO();
        IModel<String> model = $m.get(() -> to.a);
        Assert.assertEquals("A", model.getObject());
        to.a = "AA";
        Assert.assertEquals("AA", model.getObject());
    }

    @Test
    public void getSet() {
        TestTO to = new TestTO();
        IModel<String> model = $m.getSet(() -> to.a, v -> to.a = v);
        Assert.assertEquals("A", model.getObject());
        model.setObject("AA");
        Assert.assertEquals("AA", model.getObject());
        Assert.assertEquals("AA", to.a);
    }

    @Test
    public void isGt() {
        IModel<Integer> m1 = $m.ofValue(1);
        IModel<Integer> m2 = $m.ofValue(2);
        IModel<Integer> mn = $m.ofValue();

        Assert.assertFalse($m.isGt(m1, m2).getObject());
        Assert.assertTrue($m.isGt(m2, m1).getObject());
        Assert.assertFalse($m.isGt(mn, m1).getObject());
        Assert.assertTrue($m.isGt(m1, mn).getObject());
        $m.isGt(m1, m2).detach();
    }

    @Test
    public void isNot() {
        Assert.assertFalse($m.isNot($m.ofValue(true)).getObject());
        Assert.assertTrue($m.isNot($m.ofValue(false)).getObject());
        $m.isNot($m.ofValue(false)).detach();
    }

    @Test
    public void isNotNullOrEmpty() {
        Assert.assertFalse($m.isNotNullOrEmpty($m.ofValue()).getObject());
        Assert.assertFalse($m.isNotNullOrEmpty(null).getObject());
        Assert.assertTrue($m.isNotNullOrEmpty($m.ofValue(1)).getObject());
        Assert.assertTrue($m.isNotNullOrEmpty(1).getObject());
    }

    @Test
    public void isNullOrEmpty() {
        Assert.assertTrue($m.isNullOrEmpty($m.ofValue()).getObject());
        Assert.assertTrue($m.isNullOrEmpty(null).getObject());
        Assert.assertFalse($m.isNullOrEmpty($m.ofValue(1)).getObject());
        Assert.assertFalse($m.isNullOrEmpty(1).getObject());
    }

    @Test
    public void loadable() {
        List<Integer> list = new ArrayList<>();
        IFunction<List<Integer>, List<Integer>> populator = it -> {
            it.addAll(Arrays.asList(1, 2));
            return it;
        };
        ISupplier<List<Integer>> supplier = () -> populator.apply(list);
        IModel<List<Integer>> model = $m.loadable(supplier);
        Assert.assertEquals(2, model.getObject().size());
        Assert.assertEquals(2, model.getObject().size());
        Assert.assertEquals(4, supplier.get().size());
        Assert.assertEquals(6, supplier.get().size());
        Assert.assertEquals(6, model.getObject().size());
    }

    @Test
    public void loadableInitial() {
        IModel<List<Integer>> model = $m.loadable(Arrays.asList(1, 2), () -> Arrays.asList(3));
        Assert.assertEquals(2, model.getObject().size());
        model.detach();
        Assert.assertEquals(1, model.getObject().size());
    }

    @Test
    public void map() {
        IModel<String> model = $m.map($m.ofValue(new TestTO("S")), it -> it.a);
        Assert.assertEquals("S", model.getObject());
        model.detach();
    }

    @Test
    public void ofValue() {
        Assert.assertNull($m.ofValue().getObject());
        Assert.assertEquals(1, $m.ofValue(1).getObject().intValue());
        Assert.assertEquals(
            $m.ofValue(new TestTO("A", "B", "C"), it -> it.a),
            $m.ofValue(new TestTO("A", "X", "Y"), it -> it.a));
    }

    @Test
    public void property() {
        Assert.assertEquals("X", $m.property(new TestTO("X"), "a").getObject());
        Assert.assertEquals("Y", $m.property(new TestTO("Y"), "a", String.class).getObject());
    }
    
    @Test
    public void wrapValue() {
        Assert.assertEquals("X", $m.wrapValue("X").getObject());
        Assert.assertEquals("X", $m.wrapValue($m.ofValue("X")).getObject());
    }

    static class TestTO implements Serializable {
        private String a, b, c;

        public TestTO(String s) {
            this(s, s, s);
        }
        public TestTO() {
            this("A", "B", "C");
        }
        public TestTO(String a, String b, String c) {
            this.a = a;
            this.b = b;
            this.c = c;
        }
        //@formatter:off
        public String getA() { return a; } 
        public String getB() { return b; } 
        public String getC() { return c; } 
        public TestTO setA(String a) { this.a = a; return this; } 
        public TestTO setB(String b) { this.b = b; return this; } 
        public TestTO setC(String c) { this.c = c; return this; }
        //@formatter:on
    }
}
