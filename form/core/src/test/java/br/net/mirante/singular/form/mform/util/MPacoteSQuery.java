package br.net.mirante.singular.form.mform.util;

import br.net.mirante.singular.form.mform.MPacote;
import br.net.mirante.singular.form.mform.MTipoComposto;
import br.net.mirante.singular.form.mform.MTipoLista;
import br.net.mirante.singular.form.mform.PacoteBuilder;
import br.net.mirante.singular.form.mform.core.MTipoInteger;
import br.net.mirante.singular.form.mform.core.MTipoString;

public class MPacoteSQuery extends MPacote {

    public MTipoComposto<?>             contato;
    public MTipoLista<MTipoComposto<?>> enderecos;
    public MTipoComposto<?>             identificacao;
    public MTipoString                  nome;
    public MTipoString                  sobrenome;
    public MTipoComposto<?>             endereco;
    public MTipoString                  enderecoLogradouro;
    public MTipoInteger                 enderecoNumero;
    public MTipoString                  enderecoComplemento;
    public MTipoString                  enderecoCidade;
    public MTipoString                  enderecoEstado;
    public MTipoLista<MTipoString>      telefones;
    public MTipoLista<MTipoString>      emails;

    public MPacoteSQuery() {
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
        enderecoNumero = endereco.addCampoInteger("numero");
        enderecoComplemento = endereco.addCampoString("complemento");
        enderecoCidade = endereco.addCampoString("cidade");
        enderecoEstado = endereco.addCampoString("estado");

        telefones = contato.addCampoListaOf("telefones", MTipoString.class);
        emails = contato.addCampoListaOf("emails", MTipoString.class);
    }
}
