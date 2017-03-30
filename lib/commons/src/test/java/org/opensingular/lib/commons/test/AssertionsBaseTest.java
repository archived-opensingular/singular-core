package org.opensingular.lib.commons.test;

import org.junit.Assert;
import org.junit.Test;

import java.util.Optional;

public class AssertionsBaseTest {
    @Test
    public void testConstructor(){
        Assert.assertNotNull(new AssertionsTest("raiz"));
        Assert.assertNotNull(new AssertionsTest(Optional.of("raizOptional")));
    }

    @Test
    public void testGetClass(){
        AssertionsTest assertion = new AssertionsTest("raiz");

        Assert.assertNotNull(assertion.getTarget());
        Assert.assertTrue(assertion.getTarget() instanceof String);

        Assert.assertNotNull(assertion.getTarget(String.class));
        Assert.assertNotNull(assertion.getTargetOrException());
    }

    @Test(expected = AssertionError.class)
    public void testGetTargetException(){
        AssertionsTest assertion = new AssertionsTest("raiz");
        assertion.getTarget(Integer.class);
    }

    @Test(expected = AssertionError.class)
    public void testGetTargetOrException(){
        AssertionsTest assertion = new AssertionsTest((String) null);
        assertion.getTargetOrException();
    }

    @Test
    public void testIsNull(){
        AssertionsTest assertion = new AssertionsTest((String) null);
        assertion.isNull();
    }

    @Test(expected = AssertionError.class)
    public void testIsNullException(){
        AssertionsTest assertion = new AssertionsTest("raiz");
        assertion.isNull();
    }

    @Test
    public void testIs(){
        AssertionsTest assertion = new AssertionsTest("raiz");
        assertion.is(String.class);
    }

    @Test(expected = AssertionError.class)
    public void testIsException(){
        AssertionsTest assertion = new AssertionsTest("raiz");
        assertion.is(Integer.class);
    }

    @Test
    public void testIsSameAs(){
        String raiz = new String("valor");
        AssertionsTest assertion = new AssertionsTest(raiz);
        assertion.isSameAs(raiz);
    }

    @Test(expected = AssertionError.class)
    public void testIsSameAsException(){
        String raiz = new String("valor");
        String raiz2 = new String("valor");
        AssertionsTest assertion = new AssertionsTest(raiz);
        assertion.isSameAs(raiz2);
    }

    @Test
    public void testIsNotSameAs(){
        String raiz = new String("valor");
        String raiz2 = new String("valor");
        AssertionsTest assertion = new AssertionsTest(raiz);
        assertion.isNotSameAs(raiz2);
    }

    @Test(expected = AssertionError.class)
    public void testIsNotSameAsException(){
        String raiz = new String("valor");
        AssertionsTest assertion = new AssertionsTest(raiz);
        assertion.isNotSameAs(raiz);
    }


    private class AssertionsTest extends AssertionsBase<String, AssertionsTest>{

        public AssertionsTest(String target) {
            super(target);
        }

        public AssertionsTest(Optional<String> target) {
            super(target);
        }

        @Override
        protected String errorMsg(String msg) {
            return getTarget() == null ? msg : "No elemento " + getTarget() + ": " + msg;
        }
    }
}
