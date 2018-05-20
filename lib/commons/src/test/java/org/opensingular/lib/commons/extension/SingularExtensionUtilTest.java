/*
 * Copyright (C) 2016 Singular Studios (a.k.a Atom Tecnologia) - www.opensingular.com
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.opensingular.lib.commons.extension;

import org.assertj.core.api.Assertions;
import org.assertj.core.api.ThrowableAssert;
import org.junit.Test;
import org.opensingular.lib.commons.base.SingularException;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertTrue;

/**
 * @author Daniel C. Bordin
 * @since 2018-05-19
 */
public class SingularExtensionUtilTest {

    private interface NoImplementationExtension extends SingularExtension {
    }

    @Test
    public void lookingForNoExistingImplementation() {
        assertThat(extensionUtil().findExtensions(NoImplementationExtension.class)).isEmpty();
        assertThat(extensionUtil().findExtension(NoImplementationExtension.class).orElse(null)).isNull();
        Assertions.assertThatThrownBy(() -> extensionUtil().findExtensionOrException(NoImplementationExtension.class))
                .isExactlyInstanceOf(SingularException.class).hasMessageContaining("No registered implementation for");
    }

    private interface InvalidNameExt extends SingularExtension {
    }

    @Test
    public void detectInvalidExtensionName() {
        assertInvalidNameException(() -> extensionUtil().findExtension(InvalidNameExt.class));
        assertInvalidNameException(() -> extensionUtil().findExtensionOrException(InvalidNameExt.class));
        assertInvalidNameException(() -> extensionUtil().findExtensions(InvalidNameExt.class));
    }

    private void assertInvalidNameException(ThrowableAssert.ThrowingCallable code) {
        Assertions.assertThatThrownBy(code).isExactlyInstanceOf(SingularException.class).hasMessageContaining(
                "It must ends with the sufix 'Extension'");
    }

    public interface SinglePointExtension extends SingularExtension {
    }

    public static class SinglePointA implements SinglePointExtension {
    }

    @Test
    public void lookingForSingleRegistration() {
        assertThat(extensionUtil().findExtensions(SinglePointExtension.class)).hasSize(1).element(0)
                .isExactlyInstanceOf(SinglePointA.class);
        assertThat(extensionUtil().findExtension(SinglePointExtension.class).orElse(null)).isExactlyInstanceOf(
                SinglePointA.class);
        assertThat(extensionUtil().findExtensionOrException(SinglePointExtension.class)).isExactlyInstanceOf(
                SinglePointA.class);
    }

    public interface MultiExtension extends SingularExtension {
    }

    public static class MultiImplA implements MultiExtension {
        @Override
        public int getExtensionPriority() { return 100; }
    }

    public static class MultiImplB implements MultiExtension {
        @Override
        public int getExtensionPriority() { return 10; }
    }

    public static class MultiImplC implements MultiExtension {
        @Override
        public int getExtensionPriority() { return 100; }
    }

    @Test
    public void lookingForMultiRegistration() {
        List<MultiExtension> list = extensionUtil().findExtensions(MultiExtension.class);
        assertThat(list).hasSize(3).allMatch(e -> e instanceof MultiExtension);
        if (list.get(0) instanceof MultiImplA) {
            assertThat(list.get(1)).isInstanceOf(MultiImplC.class);
        } else {
            assertThat(list.get(0)).isInstanceOf(MultiImplC.class);
            assertThat(list.get(1)).isInstanceOf(MultiImplA.class);
        }
        assertThat(list.get(2)).isInstanceOf(MultiImplB.class);

        MultiExtension first = extensionUtil().findExtensionOrException(MultiExtension.class);
        assertThat(extensionUtil().findExtension(MultiExtension.class).orElse(null)).isExactlyInstanceOf(
                first.getClass());
        assertTrue(first instanceof MultiImplA || first instanceof MultiImplC);
    }

    public interface MultiWithQualifierExtension extends SingularExtension {
    }

    @ExtensionQualifier("A")
    public static class MultiQImplA implements MultiWithQualifierExtension {
        @Override
        public int getExtensionPriority() { return 100; }
    }

    @ExtensionQualifier("B")
    public static class MultiQImplB implements MultiWithQualifierExtension {
        @Override
        public int getExtensionPriority() { return 10; }
    }

    public static class MultiQImplC implements MultiWithQualifierExtension {
        @Override
        public int getExtensionPriority() { return 200; }
    }

    @ExtensionQualifier("A")
    @ExtensionQualifier("B")
    public static class MultiQImplD implements MultiWithQualifierExtension {
        @Override
        public int getExtensionPriority() { return 20; }
    }

    @Test
    public void lookingForMultiWithQualifier() {
        assertLookingUpResult(MultiWithQualifierExtension.class, null, MultiQImplC.class, MultiQImplA.class,
                MultiQImplD.class, MultiQImplB.class);
        assertLookingUpResult(MultiWithQualifierExtension.class, "A", MultiQImplA.class, MultiQImplD.class);
        assertLookingUpResult(MultiWithQualifierExtension.class, "B", MultiQImplD.class, MultiQImplB.class);
        assertLookingUpResult(MultiWithQualifierExtension.class, "C");
    }

    private <T extends SingularExtension> void assertLookingUpResult(@Nonnull Class<T> extensionTarget,
            @Nullable String qualifier, Class<?>... expectedResult) {
        List<T> list = qualifier == null ? extensionUtil().findExtensions(extensionTarget) :
                extensionUtil().findExtensions(extensionTarget, qualifier);

        assertThat(list).hasSize(expectedResult.length);
        for (int i = 0; i < expectedResult.length; i++) {
            assertThat(list).element(i).isExactlyInstanceOf(expectedResult[i]);
        }

        if (expectedResult.length == 0 && qualifier == null) {
            assertThat(extensionUtil().findExtension(extensionTarget).orElse(null)).isNull();
            Assertions.assertThatThrownBy(() -> extensionUtil().findExtensionOrException(extensionTarget))
                    .isExactlyInstanceOf(SingularException.class).hasMessageContaining(
                    "No registered implementation for");
        } else if (qualifier == null) {
            assertThat(extensionUtil().findExtension(extensionTarget).orElse(null)).isExactlyInstanceOf(
                    expectedResult[0]);
            assertThat(extensionUtil().findExtensionOrException(extensionTarget)).isExactlyInstanceOf(
                    expectedResult[0]);
        }
    }

    private static SingularExtensionUtil extensionUtil() {
        return SingularExtensionUtil.get();
    }

    public interface ButtonExtension extends SingularExtension {
        //Some methods
    }

    @ExtensionQualifier("MainPageButtonBar")
    public static class ButtonA implements ButtonExtension {
        @Override
        public int getExtensionPriority() { return 100; }

        //Some other methods
    }

    @ExtensionQualifier("WorklistButtonBar")
    public static class ButtonB implements ButtonExtension {
        @Override
        public int getExtensionPriority() { return 10; }
        //Some other methods
    }

    @ExtensionQualifier("MainPageButtonBar")
    @ExtensionQualifier("WorklistButtonBar")
    public static class ButtonD implements ButtonExtension {
        @Override
        public int getExtensionPriority() { return 20; }
        //Some other methods
    }

}