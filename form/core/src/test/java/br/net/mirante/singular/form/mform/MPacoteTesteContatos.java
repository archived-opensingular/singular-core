package br.net.mirante.singular.form.mform;

import static org.apache.commons.lang3.StringUtils.*;

import br.net.mirante.singular.form.mform.basic.ui.MPacoteBasic;
import br.net.mirante.singular.form.mform.core.MIString;
import br.net.mirante.singular.form.mform.core.MTipoInteger;
import br.net.mirante.singular.form.mform.core.MTipoString;
import br.net.mirante.singular.form.mform.util.comuns.MTipoTelefoneNacional;

public class MPacoteTesteContatos extends MPacote {

    public MTipoComposto<?>                                  contato;
    public MTipoLista<MTipoComposto<MIComposto>, MIComposto> enderecos;
    public MTipoComposto<?>                                  identificacao;
    public MTipoString                                       nome;
    public MTipoString                                       sobrenome;
    public MTipoComposto<?>                                  endereco;
    public MTipoString                                       enderecoLogradouro;
    public MTipoInteger                                      enderecoNumero;
    public MTipoString                                       enderecoComplemento;
    public MTipoString                                       enderecoCidade;
    public MTipoString                                       enderecoEstado;
    public MTipoLista<MTipoTelefoneNacional, MIString>       telefones;
    public MTipoTelefoneNacional                             telefone;
    public MTipoLista<MTipoString, MIString>                 emails;
    public MTipoString                                       email;

    public MPacoteTesteContatos() {
        super("mform.exemplo.squery");
    }

    @Override
    protected void carregarDefinicoes(PacoteBuilder pb) {
        contato = pb.createTipoComposto("Contato");

        identificacao = contato.addCampoComposto("identificacao");
        nome = identificacao.addCampoString("nome", true);
        sobrenome = identificacao.addCampoString("sobrenome");
//        sobrenome.as(MPacoteBasic.aspect())
//            .visivel(i -> {
//                boolean visible = i.findAncestor(contato).get().findDescendant(nome).get().getValor() == null;
//                System.out.println(">>> " + visible);
//                return visible;
//            })
//            .enabled(i -> defaultString(i.findAncestor(contato).get().findDescendant(nome).get().getValor()).length() > 3);

        enderecos = contato.addCampoListaOfComposto("enderecos", "endereco");
        endereco = enderecos.getTipoElementos();
        enderecoLogradouro = endereco.addCampoString("logradouro");
        enderecoComplemento = endereco.addCampoString("complemento");
        enderecoNumero = endereco.addCampoInteger("numero");
        enderecoCidade = endereco.addCampoString("cidade");
        enderecoEstado = endereco.addCampoString("estado");

        telefones = contato.addCampoListaOf("telefones", MTipoTelefoneNacional.class);
        telefone = telefones.getTipoElementos();
        emails = contato.addCampoListaOf("emails", MTipoString.class);
        email = emails.getTipoElementos();
    }
}
