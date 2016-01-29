package br.net.mirante.singular.form.mform;

import br.net.mirante.singular.form.mform.core.SIString;
import br.net.mirante.singular.form.mform.core.STypeInteger;
import br.net.mirante.singular.form.mform.core.STypeString;
import br.net.mirante.singular.form.mform.util.comuns.STypeTelefoneNacional;

public class SPackageTesteContatos extends SPackage {

    public STypeComposite<?> contato;
    public STypeLista<STypeComposite<SIComposite>, SIComposite> enderecos;
    public STypeComposite<?> identificacao;
    public STypeString nome;
    public STypeString sobrenome;
    public STypeComposite<?> endereco;
    public STypeString enderecoLogradouro;
    public STypeInteger enderecoNumero;
    public STypeString enderecoComplemento;
    public STypeString enderecoCidade;
    public STypeString enderecoEstado;
    public STypeLista<STypeTelefoneNacional, SIString> telefones;
    public STypeTelefoneNacional telefone;
    public STypeLista<STypeString, SIString> emails;
    public STypeString email;

    public SPackageTesteContatos() {
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

        telefones = contato.addCampoListaOf("telefones", STypeTelefoneNacional.class);
        telefone = telefones.getTipoElementos();
        emails = contato.addCampoListaOf("emails", STypeString.class);
        email = emails.getTipoElementos();
    }
}
