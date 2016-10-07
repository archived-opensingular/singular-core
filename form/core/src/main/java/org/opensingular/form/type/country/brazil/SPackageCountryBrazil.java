package org.opensingular.form.type.country.brazil;

import org.opensingular.form.SDictionary;
import org.opensingular.form.SPackage;
import org.opensingular.form.PackageBuilder;
import org.opensingular.form.SInfoPackage;
import org.opensingular.form.STypeComposite;

@SInfoPackage(name = SDictionary.SINGULAR_PACKAGES_PREFIX + "country.brazil")
public class SPackageCountryBrazil extends SPackage {

    @Override
    protected void onLoadPackage(PackageBuilder pb) {
        super.onLoadPackage(pb);

        pb.createType(STypeCNPJ.class);
        pb.createType(STypeCPF.class);
        pb.createType(STypeCEP.class);
        pb.createType(STypeTelefoneNacional.class);

        STypeComposite<?> endereco = pb.createCompositeType("Endereco");
        endereco.addFieldString("rua").asAtr().maxLength(50);
        endereco.addFieldString("bairro");
        endereco.addField("cep", STypeCEP.class);
    }
}
