package org.opensingular.form.type.ref;

import org.opensingular.form.*;
import org.opensingular.form.persistence.FormRespository;
import org.opensingular.form.provider.SSimpleProvider;
import org.opensingular.form.type.core.STypeString;

import javax.annotation.Nonnull;
import java.util.List;


public abstract class STypeRef<T extends SType<I>, I extends SInstance> extends STypeComposite<SIComposite> {
    public STypeString key;
    public STypeString display;

    @Override
    protected void onLoadType(@Nonnull TypeBuilder tb) {
        key = addField("key", STypeString.class);
        display = addField("display", STypeString.class);
        selection()
                .id(key)
                .display(display)
                .simpleProvider(simpleProvider());
    }

    protected SSimpleProvider simpleProvider() {
        return (SSimpleProvider) builder -> {
            List<I> values = getRepository().loadAll();
            for (I val : values) {
                builder.add()
                        .set(key, getKeyValue(val))
                        .set(display, getDisplayValue(val));
            }
        };
    }

    protected abstract String getKeyValue(I instance);

    protected abstract String getDisplayValue(I instance);

    protected abstract FormRespository<T, I> getRepository();
}