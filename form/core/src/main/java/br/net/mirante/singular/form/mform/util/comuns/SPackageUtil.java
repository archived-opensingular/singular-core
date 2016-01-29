package br.net.mirante.singular.form.mform.util.comuns;

import br.net.mirante.singular.form.mform.SPackage;
import br.net.mirante.singular.form.mform.STypeComposto;
import br.net.mirante.singular.form.mform.PacoteBuilder;
import br.net.mirante.singular.form.mform.basic.ui.AtrBasic;
import br.net.mirante.singular.form.mform.basic.ui.SPackageBasic;

public class SPackageUtil extends SPackage {

    public SPackageUtil() {
        super("mform.util.comuns");
    }

    @Override
    protected void carregarDefinicoes(PacoteBuilder pb) {
        pb.createTipo(STypeCPF.class);
        pb.createTipo(STypeCNPJ.class);
        pb.createTipo(STypeCEP.class);
        pb.createTipo(STypeEMail.class);
        pb.createTipo(STypeAnoMes.class);
        pb.createTipo(STypeNomePessoa.class);
        pb.createTipo(STypeTelefoneNacional.class);

        pb.addAtributo(STypeAnoMes.class, SPackageBasic.ATR_TAMANHO_EDICAO, 7);

        STypeComposto<?> endereco = pb.createTipoComposto("Endereco");
        endereco.addCampoString("rua").as(AtrBasic.class).tamanhoMaximo(50);
        endereco.addCampoString("bairro");
        endereco.addCampo("cep", STypeCEP.class);
    }
}
