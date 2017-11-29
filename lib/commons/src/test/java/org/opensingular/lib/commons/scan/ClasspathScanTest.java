/*
 *
 *  * Copyright (C) 2016 Singular Studios (a.k.a Atom Tecnologia) - www.opensingular.com
 *  *
 *  * Licensed under the Apache License, Version 2.0 (the "License");
 *  *  you may not use this file except in compliance with the License.
 *  * You may obtain a copy of the License at
 *  *
 *  * http://www.apache.org/licenses/LICENSE-2.0
 *  *
 *  * Unless required by applicable law or agreed to in writing, software
 *  * distributed under the License is distributed on an "AS IS" BASIS,
 *  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  * See the License for the specific language governing permissions and
 *  * limitations under the License.
 *
 */

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
    public void scanClassesAnnotatedWithBar() {
        Set<Class<?>> bars = SingularClassPathScanner.get().findClassesAnnotatedWith(Bar.class);
        Assert.assertTrue(bars.contains(FooSerializable.class));
        Assert.assertTrue(bars.contains(FooDate.class));
    }

    @Test(expected = SingularException.class)
    public void testPassNonAnnotationClass() {
        SingularClassPathScanner.get().findClassesAnnotatedWith(Object.class);
    }

    @Test
    public void filterUnknownPackage() {
        Set<Class<? extends Serializable>> empty = SingularClassPathScanner.get().findSubclassesOf(Serializable.class, ClasspathScanTest.class.getPackage().getName() + ".naoexiste");
        Assert.assertTrue(empty.isEmpty());
    }

    @Test
    public void filterExistentPackage() {
        Set<Class<? extends Serializable>> notEmpty = SingularClassPathScanner.get().findSubclassesOf(Serializable.class, FooSerializable.class.getPackage().getName());
        Assert.assertTrue(notEmpty.contains(FooSerializable.class));
    }


    @Test
    public void filterAnnotationUnknownPackage() {
        Set<Class<?>> empty = SingularClassPathScanner.get().findClassesAnnotatedWith(Bar.class, ClasspathScanTest.class.getPackage().getName() + ".naoexiste");
        Assert.assertTrue(empty.isEmpty());
    }

    @Test
    public void filterAnnotationExistentPackage() {
        Set<Class<?>> notEmpty = SingularClassPathScanner.get().findClassesAnnotatedWith(Bar.class, FooSerializable.class.getPackage().getName());
        Assert.assertTrue(notEmpty.contains(FooSerializable.class));
    }


}
