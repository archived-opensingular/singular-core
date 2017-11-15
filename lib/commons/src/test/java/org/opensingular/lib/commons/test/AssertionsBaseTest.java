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

package org.opensingular.lib.commons.test;

import org.junit.Assert;
import org.junit.Test;

import javax.annotation.Nonnull;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

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
        Assert.assertNotNull(assertion.getTarget());
    }

    @Test(expected = AssertionError.class)
    public void testGetTargetException(){
        AssertionsTest assertion = new AssertionsTest("raiz");
        assertion.getTarget(Integer.class);
    }

    @Test(expected = AssertionError.class)
    public void testGetTargetOrException(){
        AssertionsTest assertion = new AssertionsTest((String) null);
        assertion.getTarget();
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
        assertion.isInstanceOf(String.class);
    }

    @Test(expected = AssertionError.class)
    public void testIsException(){
        AssertionsTest assertion = new AssertionsTest("raiz");
        assertion.isInstanceOf(Integer.class);
    }

    @Test
    public void testIsSameAs(){
        String root = new String("valor");
        AssertionsTest assertion = new AssertionsTest(root);
        assertion.isSameAs(root);
    }

    @Test(expected = AssertionError.class)
    public void testIsSameAsException(){
        String root = new String("valor");
        String root2 = new String("valor");
        AssertionsTest assertion = new AssertionsTest(root);
        assertion.isSameAs(root2);
    }

    @Test
    public void testIsNotSameAs(){
        String root = new String("valor");
        String root2 = new String("valor");
        AssertionsTest assertion = new AssertionsTest(root);
        assertion.isNotSameAs(root2);
    }

    @Test
    public void testIsNotSameAsException(){
        String root = "valor";
        AssertionsTest assertion = new AssertionsTest(root);
        assertThatThrownBy(() -> assertion.isNotSameAs(root)).isInstanceOf(AssertionError.class);
    }

    @Test
    public void testDescription(){
        assertThatThrownBy(() -> new AssertionsTest("hello").isNull()).isInstanceOf(AssertionError.class)
                .hasMessageContaining("Text hello");
    }


    private class AssertionsTest extends AssertionsBase<AssertionsTest, String>{

        public AssertionsTest(String target) {
            super(target);
        }

        public AssertionsTest(Optional<String> target) {
            super(target);
        }

        @Override
        protected  Optional<String> generateDescriptionForCurrentTarget(@Nonnull Optional<String> current) {
            return current.map(t -> "Text " + t);
        }
    }
}
