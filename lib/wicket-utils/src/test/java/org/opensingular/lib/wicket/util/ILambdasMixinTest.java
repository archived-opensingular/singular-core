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

package org.opensingular.lib.wicket.util;

import static java.util.stream.Collectors.*;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;

import org.opensingular.lib.wicket.util.lambda.ILambdasMixin;
import org.junit.Assert;
import org.junit.Test;

public class ILambdasMixinTest {

    private final ILambdasMixin $L = new ILambdasMixin() {};

    private A root = new A(0,
        new A(1,
            new A(2),
            new A(3,
                new A(4),
                new A(5))),
        new A(6,
            new A(7,
                new A(8,
                    new A(9)))),
        new A(10));

    @Test
    public void test_recursiveIterable() {
        Assert.assertEquals(
            Arrays.asList(0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10),
            Stream.of(root)
                .flatMap($L.recursiveIterable(it -> it.children))
                .map(it -> it.n)
                .collect(toList()));
    }

    private static class A {
        List<A> children;
        int     n;
        public A(int n, A... children) {
            this.n = n;
            this.children = Arrays.asList(children);
        }
    }
}
