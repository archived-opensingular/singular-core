package br.net.mirante.singular.form.mform;

import br.net.mirante.singular.form.mform.MPacote;
import br.net.mirante.singular.form.mform.MTipoComposto;
import br.net.mirante.singular.form.mform.MTipoLista;
import br.net.mirante.singular.form.mform.PacoteBuilder;
import br.net.mirante.singular.form.mform.core.MTipoInteger;
import br.net.mirante.singular.form.mform.core.MTipoString;
import br.net.mirante.singular.form.mform.util.comuns.MTipoTelefoneNacional;

public class MPacoteTesteContatos extends MPacote {

    public MTipoComposto<?>                  contato;
    public MTipoLista<MTipoComposto<?>>      enderecos;
    public MTipoComposto<?>                  identificacao;
    public MTipoString                       nome;
    public MTipoString                       sobrenome;
    public MTipoComposto<?>                  endereco;
    public MTipoString                       enderecoLogradouro;
    public MTipoInteger                      enderecoNumero;
    public MTipoString                       enderecoComplemento;
    public MTipoString                       enderecoCidade;
    public MTipoString                       enderecoEstado;
    public MTipoLista<MTipoTelefoneNacional> telefones;
    public MTipoLista<MTipoString>           emails;

    public MPacoteTesteContatos() {
        super("mform.exemplo.squery");
    }

    @Override
    protected void carregarDefinicoes(PacoteBuilder pb) {
        contato = pb.createTipoComposto("Contato");

        identificacao = contato.addCampoComposto("identificacao");
        nome = identificacao.addCampoString("nome", true);
        sobrenome = identificacao.addCampoString("sobrenome");

        enderecos = contato.addCampoListaOfComposto("enderecos", "endereco");
        endereco = enderecos.getTipoElementos();
        enderecoLogradouro = endereco.addCampoString("logradouro");
        enderecoComplemento = endereco.addCampoString("complemento");
        enderecoNumero = endereco.addCampoInteger("numero");
        enderecoCidade = endereco.addCampoString("cidade");
        enderecoEstado = endereco.addCampoString("estado");

        telefones = contato.addCampoListaOf("telefones", MTipoTelefoneNacional.class);
        emails = contato.addCampoListaOf("emails", MTipoString.class);
    }
}
