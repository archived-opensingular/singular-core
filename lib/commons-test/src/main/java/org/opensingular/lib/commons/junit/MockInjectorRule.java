/*
 * Copyright (C) 2016 Singular Studios (a.k.a Atom Tecnologia) - www.opensingular.com
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
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

package org.opensingular.lib.commons.junit;

import net.vidageek.mirror.dsl.Mirror;
import net.vidageek.mirror.list.dsl.Matcher;
import org.apache.poi.ss.formula.functions.Mirr;
import org.junit.rules.MethodRule;
import org.junit.runners.model.FrameworkMethod;
import org.junit.runners.model.Statement;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.opensingular.internal.lib.commons.injection.SingularInjector;
import org.opensingular.lib.commons.context.ServiceRegistryLocator;
import org.opensingular.lib.commons.context.SingularContextSetup;
import org.opensingular.lib.commons.util.Loggable;

import javax.annotation.Nonnull;
import javax.inject.Inject;
import java.lang.reflect.Field;
import java.util.List;
import java.util.Optional;

/**
 * Register a injector that puts mock beans in all fields annotated with @{@link Inject} using {@link Mockito}.
 *
 * @author Danilo Mesquita
 * @author Daniel C. Bordin
 */
public class MockInjectorRule implements MethodRule {

    @Override
    public Statement apply(Statement base, FrameworkMethod method, Object target) {
        return new Statement() {
            @Override
            public void evaluate() throws Throwable {
                SingularContextSetup.reset();
                ServiceRegistryLocator.locate().bindService(SingularInjector.class, () -> new SingularInjectorMocker(target));
                base.evaluate();
            }
        };
    }

    /**
     * A injector that puts mock beans in all fields annotated with @{@link Inject} using {@link Mockito}.
     */
    private static class SingularInjectorMocker implements SingularInjector, Loggable {

        private Object      target;
        private List<Field> mockFields;

        public SingularInjectorMocker(Object target) {
            this.target = target;
             mockFields = new Mirror().on(target.getClass()).reflectAll().fields().matching(element -> element.isAnnotationPresent(Mock.class));
        }

        @Override
        public void inject(@Nonnull Object object) {
            new Mirror().on(object.getClass()).reflectAll().fields().matching(f -> f.isAnnotationPresent(Inject.class))
                    .forEach(f -> {
                        f.setAccessible(true);
                        try {
                            f.set(object, getMock(f.getType()));
                        } catch (Exception e) {
                            getLogger().error(e.getMessage(), e);
                        }
                    });
        }

        private Object getMock(Class<?> clazz) {
            Optional<Field> f = mockFields.stream().filter(f1 -> f1.getType().equals(clazz)).findFirst();
            if (!f.isPresent()) {
                f = mockFields.stream().filter(f1 -> f1.getType().isAssignableFrom(clazz)).findFirst();
            }
            if (f.isPresent()){
                return new Mirror().on(target).get().field(f.get());
            } else {
                return Mockito.mock(clazz);
            }
        }
    }
}
