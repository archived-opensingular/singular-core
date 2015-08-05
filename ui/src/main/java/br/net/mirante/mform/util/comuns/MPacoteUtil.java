package br.net.mirante.mform.util.comuns;

import br.net.mirante.mform.MPacote;
import br.net.mirante.mform.MTipoComposto;
import br.net.mirante.mform.PacoteBuilder;
import br.net.mirante.mform.basic.ui.AtrBasic;

public class MPacoteUtil extends MPacote {

    public MPacoteUtil() {
        super("mform.util.comuns");
    }

    @Override
    protected void carregarDefinicoes(PacoteBuilder pb) {

        pb.createTipo(MTipoCPF.class);
        pb.createTipo(MTipoCNPJ.class).as(AtrBasic.class).label("CNPJ").tamanhoMaximo(14);
        pb.createTipo(MTipoCEP.class);
        pb.createTipo(MTipoEMail.class);
        pb.createTipo(MTipoAnoMes.class);
        pb.createTipo(MTipoNomePessoa.class);
        pb.createTipo(MTipoTelefoneNacional.class);

        MTipoComposto<?> endereco = pb.createTipoComposto("Endereco");
        endereco.addCampoString("rua").as(AtrBasic.class).tamanhoMaximo(50);
        endereco.addCampoString("bairro");
        endereco.addCampo("cep", MTipoCEP.class);
    }
}
