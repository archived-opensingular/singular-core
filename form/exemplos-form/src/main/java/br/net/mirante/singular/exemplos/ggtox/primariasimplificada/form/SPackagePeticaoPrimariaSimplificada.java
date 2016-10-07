package br.net.mirante.singular.exemplos.ggtox.primariasimplificada.form;


import br.net.mirante.singular.form.PackageBuilder;
import br.net.mirante.singular.form.SInfoPackage;
import br.net.mirante.singular.form.SPackage;
import br.net.mirante.singular.form.persistence.SPackageFormPersistence;


@SInfoPackage(name = SPackagePeticaoPrimariaSimplificada.PACOTE)
public class SPackagePeticaoPrimariaSimplificada extends SPackage {

    public static final String PACOTE        = "mform.peticao";
    public static final String TIPO          = "PeticaoPrimariaSimplificada";
    public static final String NOME_COMPLETO = PACOTE + "." + TIPO;

    public SPackagePeticaoPrimariaSimplificada() {
        super(PACOTE);
    }

    @Override
    protected void onLoadPackage(PackageBuilder pb) {
        super.onLoadPackage(pb);
        pb.loadPackage(SPackageFormPersistence.class);
        pb.loadPackage(SPackagePPSCommon.class);
        pb.createType(STypePeticaoPrimariaSimplificada.class);
        pb.createType(STypeParecer.class);
        pb.createType(STypePublicacao.class);
        pb.createType(STypeAnaliseGerencial.class);
    }

}