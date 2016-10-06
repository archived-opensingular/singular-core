package org.opensingular.singular.form;

import org.opensingular.singular.commons.lambda.IConsumer;
import org.opensingular.singular.form.type.basic.SPackageBasic;

@SInfoType(name = "STypeConsumer", spackage = SPackageBasic.class)
public class STypeConsumer<V> extends STypeCode<SIConsumer<V>, IConsumer<V>> {

    @SuppressWarnings({ "unchecked", "rawtypes" })
    public STypeConsumer() {
        super((Class) SIConsumer.class, (Class) IConsumer.class);
    }

}