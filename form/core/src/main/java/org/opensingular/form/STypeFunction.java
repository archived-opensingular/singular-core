package org.opensingular.form;


import org.opensingular.form.provider.SPackageProvider;
import org.opensingular.lib.commons.lambda.IFunction;

@SInfoType(name = "STypeFunction", spackage = SPackageProvider.class)
public class STypeFunction<I extends SIFunction<T, R>, T, R> extends STypeCode<I, IFunction<T, R>> {

    @SuppressWarnings({ "unchecked", "rawtypes" })
    public STypeFunction() {
        super((Class) SIFunction.class, (Class) IFunction.class);
    }

}
