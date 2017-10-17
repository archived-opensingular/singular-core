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

package org.opensingular.lib.commons.junit;

import net.vidageek.mirror.dsl.Mirror;
import org.junit.rules.MethodRule;
import org.junit.runners.model.FrameworkMethod;
import org.junit.runners.model.Statement;
import org.mockito.Mockito;
import org.opensingular.internal.lib.commons.injection.SingularInjector;
import org.opensingular.lib.commons.context.ServiceRegistryLocator;
import org.opensingular.lib.commons.context.SingularContextSetup;
import org.opensingular.lib.commons.util.Loggable;

import javax.annotation.Nonnull;
import javax.inject.Inject;

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
                ServiceRegistryLocator.locate().bindService(SingularInjector.class, () -> new SingularInjectorMocker());
                base.evaluate();
            }
        };
    }

    /** A injector that puts mock beans in all fields annotated with @{@link Inject} using {@link Mockito}. */
    private static class SingularInjectorMocker implements SingularInjector, Loggable {
        @Override
        public void inject(@Nonnull Object object) {
            new Mirror().on(object.getClass()).reflectAll().fields().matching(f -> f.isAnnotationPresent(Inject.class))
                    .forEach(f -> {
                        f.setAccessible(true);
                        try {
                            f.set(object, Mockito.mock(f.getType()));
                        } catch (Exception e) {
                            getLogger().error(e.getMessage(), e);
                        }
                    });
        }
    }
}
