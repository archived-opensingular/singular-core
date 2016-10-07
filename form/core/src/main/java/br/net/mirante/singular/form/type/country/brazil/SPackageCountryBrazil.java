package br.net.mirante.singular.form.type.country.brazil;

import br.net.mirante.singular.form.PackageBuilder;
import br.net.mirante.singular.form.SDictionary;
import br.net.mirante.singular.form.SInfoPackage;
import br.net.mirante.singular.form.SPackage;
import br.net.mirante.singular.form.STypeComposite;

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
