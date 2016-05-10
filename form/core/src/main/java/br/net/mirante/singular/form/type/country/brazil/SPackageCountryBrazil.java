package br.net.mirante.singular.form.type.country.brazil;

import br.net.mirante.singular.form.*;
import br.net.mirante.singular.form.type.basic.AtrBasic;

@SInfoPackage(name = SDictionary.SINGULAR_PACKAGES_PREFIX + "country.brazil")
public class SPackageCountryBrazil extends SPackage {

    @Override
    protected void carregarDefinicoes(PackageBuilder pb) {
        super.carregarDefinicoes(pb);

        pb.createType(STypeCNPJ.class);
        pb.createType(STypeCPF.class);
        pb.createType(STypeCEP.class);
        pb.createType(STypeTelefoneNacional.class);

        STypeComposite<?> endereco = pb.createCompositeType("Endereco");
        endereco.addFieldString("rua").as(AtrBasic.class).tamanhoMaximo(50);
        endereco.addFieldString("bairro");
        endereco.addField("cep", STypeCEP.class);
    }
}
