package org.opensingular.form.decorator.action;

import java.io.Serializable;
import java.util.List;
import java.util.Optional;
import java.util.function.Supplier;

import org.opensingular.form.SInstance;
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
    public void showMessage(String title, Serializable msg, String forcedFormat) {
        _showMessageCount++;
    }
    @Override
    public void openForm(Out<FormDelegate> formDelegate, String title, ISupplier<SInstance> instanceSupplier, List<SInstanceAction> actions) {
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