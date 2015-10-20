package br.net.mirante.singular.form.mform.util.comuns;

import br.net.mirante.singular.form.mform.MPacote;
import br.net.mirante.singular.form.mform.MTipoComposto;
import br.net.mirante.singular.form.mform.PacoteBuilder;
import br.net.mirante.singular.form.mform.basic.ui.AtrBasic;
import br.net.mirante.singular.form.mform.basic.ui.MPacoteBasic;

public class MPacoteUtil extends MPacote {

    public MPacoteUtil() {
        super("mform.util.comuns");
    }

    @Override
    protected void carregarDefinicoes(PacoteBuilder pb) {
        pb.createTipo(MTipoCPF.class);
        pb.createTipo(MTipoCNPJ.class);
        pb.createTipo(MTipoCEP.class);
        pb.createTipo(MTipoEMail.class);
        pb.createTipo(MTipoAnoMes.class);
        pb.createTipo(MTipoNomePessoa.class);
        pb.createTipo(MTipoTelefoneNacional.class);

        pb.addAtributo(MTipoAnoMes.class, MPacoteBasic.ATR_TAMANHO_EDICAO, 7);

        MTipoComposto<?> endereco = pb.createTipoComposto("Endereco");
        endereco.addCampoString("rua").as(AtrBasic.class).tamanhoMaximo(50);
        endereco.addCampoString("bairro");
        endereco.addCampo("cep", MTipoCEP.class);
    }
}
