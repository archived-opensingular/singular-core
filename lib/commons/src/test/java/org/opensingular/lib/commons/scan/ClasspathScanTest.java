package org.opensingular.lib.commons.scan;

import org.junit.Assert;
import org.junit.Test;
import org.opensingular.lib.commons.base.SingularException;

import java.io.Serializable;
import java.util.Date;
import java.util.Set;

public class ClasspathScanTest {

    @Test
    public void scanSubclassesOfDATE() {
        Set<Class<? extends Date>> dates = SingularClassPathScanner.get().findSubclassesOf(Date.class);
        Assert.assertTrue(dates.contains(FooDate.class));
    }

    @Test
    public void scanClassesImplementingSerializable() {
        Set<Class<? extends Serializable>> serializables = SingularClassPathScanner.get().findSubclassesOf(Serializable.class);
        Assert.assertTrue(serializables.contains(FooSerializable.class));
    }

    @Test
    public void scanClassesAnnotatedWithBar(){
        Set<Class<?>> bars = SingularClassPathScanner.get().findClassesAnnotatedWith(Bar.class);
        Assert.assertTrue(bars.contains(FooSerializable.class));
        Assert.assertTrue(bars.contains(FooDate.class));
    }

    @Test(expected = SingularException.class)
    public void testPassNonAnnotationClass(){
        SingularClassPathScanner.get().findClassesAnnotatedWith(Object.class);
    }


}
