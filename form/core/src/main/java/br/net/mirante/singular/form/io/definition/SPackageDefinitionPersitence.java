package br.net.mirante.singular.form.io.definition;

import br.net.mirante.singular.form.PackageBuilder;
import br.net.mirante.singular.form.SDictionary;
import br.net.mirante.singular.form.SInfoPackage;
import br.net.mirante.singular.form.SPackage;

@SInfoPackage(name = SDictionary.SINGULAR_PACKAGES_PREFIX + "io.definition")
public class SPackageDefinitionPersitence extends SPackage {

    @Override
    protected void carregarDefinicoes(PackageBuilder pb) {
        pb.createType(STypePersistenceAttribute.class);
        pb.createType(STypePersistenceType.class);
        pb.createType(STypePersistencePackage.class);
        pb.createType(STypePersistenceArchive.class);
    }
}
