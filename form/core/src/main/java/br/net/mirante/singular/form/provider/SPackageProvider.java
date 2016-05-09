package br.net.mirante.singular.form.provider;

import br.net.mirante.singular.commons.lambda.IFunction;
import br.net.mirante.singular.form.*;
import br.net.mirante.singular.form.converter.SIConverter;
import br.net.mirante.singular.form.converter.SInstanceConverter;
import br.net.mirante.singular.form.converter.STypeConverter;

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
    protected void carregarDefinicoes(PackageBuilder pb) {
        pb.createType(STypeProvider.class);
        pb.createType(STypeConverter.class);
        pb.createType(STypeFunction.class);
        pb.createAttributeIntoType(SType.class, PROVIDER);
        pb.createAttributeIntoType(SType.class, CONVERTER);
        pb.createAttributeIntoType(SType.class, DISPLAY_FUNCTION);
        pb.createAttributeIntoType(SType.class, ID_FUNCTION);
    }

}