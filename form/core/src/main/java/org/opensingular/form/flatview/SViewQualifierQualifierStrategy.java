package org.opensingular.form.flatview;

import org.opensingular.form.SType;
import org.opensingular.form.aspect.QualifierStrategyByEquals;
import org.opensingular.form.view.SView;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class SViewQualifierQualifierStrategy extends QualifierStrategyByEquals<Class<? extends SView>> {
    @Nullable
    @Override
    protected Class<? extends SView> extractQualifier(@Nonnull SType<?> type) {
        SView view = type.getView();
        if (view != null) {
            return view.getClass();
        }
        return null;
    }
}