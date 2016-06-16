package br.net.mirante.singular.form;

import br.net.mirante.singular.commons.lambda.IConsumer;
import br.net.mirante.singular.form.type.basic.SPackageBasic;

import java.util.function.Consumer;

@SInfoType(name = "STypeConsumer", spackage = SPackageBasic.class)
public class STypeConsumer<V> extends STypeCode<SIConsumer<V>, IConsumer<V>> {

    @SuppressWarnings({ "unchecked", "rawtypes" })
    public STypeConsumer() {
        super((Class) SIConsumer.class, (Class) IConsumer.class);
    }

}