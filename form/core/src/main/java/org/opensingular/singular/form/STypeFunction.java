package org.opensingular.singular.form;


import org.opensingular.singular.commons.lambda.IFunction;
import org.opensingular.singular.form.provider.SPackageProvider;

@SInfoType(name = "STypeFunction", spackage = SPackageProvider.class)
public class STypeFunction<I extends SIFunction<T, R>, T, R> extends STypeCode<I, IFunction<T, R>> {

    @SuppressWarnings({ "unchecked", "rawtypes" })
    public STypeFunction() {
        super((Class) SIFunction.class, (Class) IFunction.class);
    }

}
