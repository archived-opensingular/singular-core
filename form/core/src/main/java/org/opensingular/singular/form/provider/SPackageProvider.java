package org.opensingular.singular.form.provider;

import br.net.mirante.singular.commons.lambda.IFunction;
import br.net.mirante.singular.form.*;
import org.opensingular.singular.form.AtrRef;
import org.opensingular.singular.form.PackageBuilder;
import org.opensingular.singular.form.SIFunction;
import org.opensingular.singular.form.SPackage;
import org.opensingular.singular.form.STypeFunction;
import org.opensingular.singular.form.converter.SIConverter;
import org.opensingular.singular.form.converter.SInstanceConverter;
import org.opensingular.singular.form.converter.STypeConverter;
import org.opensingular.singular.form.SType;

public class SPackageProvider extends SPackage {

    private static final String NAME = "singular.form.provider";

    public static final AtrRef<STypeProvider, SIProvider, Provider>             PROVIDER         = new AtrRef<>(SPackageProvider.class, "provider", STypeProvider.class, SIProvider.class, Provider.class);
    public static final AtrRef<STypeConverter, SIConverter, SInstanceConverter> CONVERTER        = new AtrRef<>(SPackageProvider.class, "converter", STypeConverter.class, SIConverter.class, SInstanceConverter.class);
    public static final AtrRef<STypeFunction, SIFunction, IFunction>            DISPLAY_FUNCTION = new AtrRef<>(SPackageProvider.class, "displayFunction", STypeFunction.class, SIFunction.class, IFunction.class);
    public static final AtrRef<STypeFunction, SIFunction, IFunction>            ID_FUNCTION      = new AtrRef<>(SPackageProvider.class, "idFunction", STypeFunction.class, SIFunction.class, IFunction.class);

    public SPackageProvider() {
        super(NAME);
    }

    @Override
    protected void onLoadPackage(PackageBuilder pb) {
        pb.createType(STypeProvider.class);
        pb.createType(STypeConverter.class);
        pb.createType(STypeFunction.class);
        pb.createAttributeIntoType(SType.class, PROVIDER);
        pb.createAttributeIntoType(SType.class, CONVERTER);
        pb.createAttributeIntoType(SType.class, DISPLAY_FUNCTION);
        pb.createAttributeIntoType(SType.class, ID_FUNCTION);
    }

}