package br.net.mirante.singular.showcase.view.page.form.examples;

import br.net.mirante.singular.form.mform.MInstancia;
import br.net.mirante.singular.form.mform.MPacote;
import br.net.mirante.singular.form.mform.MTipoComposto;
import br.net.mirante.singular.form.mform.PacoteBuilder;
import br.net.mirante.singular.form.mform.basic.ui.AtrBasic;
import br.net.mirante.singular.form.mform.core.MTipoString;
import br.net.mirante.singular.form.wicket.AtrBootstrap;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

public class MPacotePeticaoCanabidiol extends MPacote {

    public static final String PACOTE = "mform.peticao.canabidiol";
    public static final String TIPO = "PeticionamentoCanabidiol";
    public static final String NOME_COMPLETO = PACOTE + "." + TIPO;

    public MPacotePeticaoCanabidiol() {
        super(PACOTE);
    }

    @Override
    protected void carregarDefinicoes(PacoteBuilder pb) {

        final MTipoComposto<?> canabis = pb.createTipoComposto(TIPO);

        final MTipoComposto<?> paciente = canabis.addCampoComposto("paciente");

        paciente
                .as(AtrBasic::new)
                .label("Dados do Paciente");

        paciente
                .addCampoString("nome")
                .as(AtrBasic::new)
                .label("Nome")
                .as(AtrBootstrap::new).colPreference(8);


        paciente
                .addCampoData("dataNascimento")
                .as(AtrBasic::new)
                .label("Data de nascimento")
                .as(AtrBootstrap::new).colPreference(2);


        //Criacção de tipos withselction fica muito truncada caso seja necessário manter a referencia ao objeto para interactions e para adicionar atributos.


        //ruim: Para manter a referencia não pode acionar atributos:
        final MTipoComposto<?> tipoDocumento = paciente
                .addCampoComposto("tipoDocumento");

        //ruim: Para  adicionar atributos não é possivel adicionar selection
        tipoDocumento
                .as(AtrBasic::new)
                .label("Documento de identificação oficial")
                .as(AtrBootstrap::new)
                .colPreference(8);

        //ruim: Para adicionar selection não é possível adicionar atributos
        //ruim: esse metodo deveria estar disponivel apenas para tipo composto.
        tipoDocumento.withSelection()
                .add("55358721", "carteira de identidade (RG) expedida pela Secretaria de Segurança Pública de um dos estados da Federação ou Distrito Federal")
                .add("55358722", "cartão de identidade expedido por ministério ou órgão subordinado à Presidência da República, incluindo o Ministério da Defesa e os Comandos da Aeronáutica, da Marinha e do Exército")
                .add("55358723", "cartão de identidade expedido pelo poder judiciário ou legislativo, no nível federal ou estadual")
                .add("55358724", "carteira nacional de habilitação (modelo com fotografia)")
                .add("55358725", "carteira de trabalho")
                .add("55358726", "carteira de identidade emitida por conselho ou federação de categoria profissional, com fotografia e fé pública em todo território nacional")
                .add("55358727", "certidão de nascimento")
                .add("55358728", "passaporte nacional")
                .add("55358729", "outro documento de identificação com fotografia e fé pública");

        paciente
                .addCampoString("nomeNoDocumento")
                .as(AtrBasic::new)
                .label("Nome")
                .as(AtrBootstrap::new)
                .colPreference(6)
                .as(AtrBasic::new)
                .visivel(ins -> {
                    //Recuperar o value de uma selection é praticamente impossível.
                    List<MInstancia> lista = (List<MInstancia>) ins.findNearestValue(tipoDocumento).orElse(Collections.EMPTY_LIST);
                    if (!CollectionUtils.isEmpty(lista)) {
                        return "55358729".equals(lista.get(0).getValorWithDefault());
                    }
                    return false;
                })
                .dependsOn(tipoDocumento);

        paciente
                .addCampoString("numeroDocumento")
                .as(AtrBasic::new)
                .label("Número")
                .as(AtrBootstrap::new).colPreference(6)
                .as(AtrBasic::new)
                .visivel(ins -> ins.findNearestValue(tipoDocumento).orElse(null) != null)
                .dependsOn(tipoDocumento);


    }

}

