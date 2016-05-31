package br.net.mirante.singular.studio.core.example;

import br.net.mirante.singular.form.PackageBuilder;
import br.net.mirante.singular.form.SInfoPackage;
import br.net.mirante.singular.form.SPackage;

@SInfoPackage(name = "test.order")
public class SPackageOrder extends SPackage {
    @Override
    protected void carregarDefinicoes(PackageBuilder pb) {
        pb.createType(STypeItem.class);
        pb.createType(STypeOrder.class);
    }
}
