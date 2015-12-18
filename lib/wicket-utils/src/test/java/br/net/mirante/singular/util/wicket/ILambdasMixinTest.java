package br.net.mirante.singular.util.wicket;

import static java.util.stream.Collectors.*;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;

import br.net.mirante.singular.util.wicket.lambda.ILambdasMixin;
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
