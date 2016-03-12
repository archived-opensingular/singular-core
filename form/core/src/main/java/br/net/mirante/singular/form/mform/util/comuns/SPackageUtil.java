package br.net.mirante.singular.form.mform.util.comuns;

import br.net.mirante.singular.form.mform.SPackage;
import br.net.mirante.singular.form.mform.STypeComposite;
import br.net.mirante.singular.form.mform.PackageBuilder;
import br.net.mirante.singular.form.mform.basic.ui.AtrBasic;
import br.net.mirante.singular.form.mform.basic.ui.SPackageBasic;
import br.net.mirante.singular.form.mform.util.brasil.STypeCEP;
import br.net.mirante.singular.form.mform.util.brasil.STypeCNPJ;
import br.net.mirante.singular.form.mform.util.brasil.STypeCPF;
import br.net.mirante.singular.form.mform.util.brasil.STypeTelefoneNacional;

public class SPackageUtil extends SPackage {

    public SPackageUtil() {
        super("mform.util.comuns");
    }

    @Override
    protected void carregarDefinicoes(PackageBuilder pb) {
        pb.createType(STypeCPF.class);
        pb.createType(STypeCNPJ.class);
        pb.createType(STypeCEP.class);
        pb.createType(STypeEMail.class);
        pb.createType(STypeYearMonth.class);
        pb.createType(STypePersonName.class);
        pb.createType(STypeTelefoneNacional.class);

        pb.addAttribute(STypeYearMonth.class, SPackageBasic.ATR_TAMANHO_EDICAO, 7);

        STypeComposite<?> endereco = pb.createCompositeType("Endereco");
        endereco.addFieldString("rua").as(AtrBasic.class).tamanhoMaximo(50);
        endereco.addFieldString("bairro");
        endereco.addField("cep", STypeCEP.class);
    }
}
