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

package org.opensingular.form.decorator.action;

import java.io.Serializable;
import java.util.Optional;
import java.util.function.Supplier;

import org.opensingular.form.SInstance;
import org.opensingular.form.decorator.action.SInstanceAction.ActionsFactory;
import org.opensingular.form.decorator.action.SInstanceAction.Delegate;
import org.opensingular.form.decorator.action.SInstanceAction.FormDelegate;
import org.opensingular.lib.commons.lambda.ISupplier;
import org.opensingular.lib.commons.ref.Out;

public final class MockSInstanceActionDelegate implements Delegate {
    public int _showMessageCount             = 0;
    public int _openFormCount                = 0;
    public int _closeFormCount               = 0;
    public int _refreshFieldForInstanceCount = 0;
    public int _getInternalContextCount      = 0;
    public int _getInstanceRefCount          = 0;

    @Override
    public void showMessage(String title, Serializable msg, String forcedFormat, ActionsFactory actionsFactory) {
        _showMessageCount++;
    }
    @Override
    public void openForm(Out<FormDelegate> formDelegate, String title, Serializable text, ISupplier<SInstance> instanceSupplier, ActionsFactory actionsFactory) {
        _openFormCount++;
        formDelegate.set(new FormDelegate() {
            @Override
            public void close() {
                _closeFormCount++;
            }
            @Override
            public SInstance getFormInstance() {
                return instanceSupplier.get();
            }
        });
    }
    @Override
    public void refreshFieldForInstance(SInstance instance) {
        _refreshFieldForInstanceCount++;
    }
    @Override
    public <T> Optional<T> getInternalContext(Class<T> clazz) {
        _getInternalContextCount++;
        return null;
    }
    @Override
    public Supplier<SInstance> getInstanceRef() {
        _getInstanceRefCount++;
        return () -> null;
    }
}