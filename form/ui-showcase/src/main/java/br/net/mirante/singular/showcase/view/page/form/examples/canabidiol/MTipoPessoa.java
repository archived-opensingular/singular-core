package br.net.mirante.singular.showcase.view.page.form.examples.canabidiol;

import br.net.mirante.singular.form.mform.MIComposto;
import br.net.mirante.singular.form.mform.MInfoTipo;
import br.net.mirante.singular.form.mform.MTipoComposto;
import br.net.mirante.singular.form.mform.TipoBuilder;
import br.net.mirante.singular.form.mform.basic.ui.AtrBasic;
import br.net.mirante.singular.form.wicket.AtrBootstrap;
import org.apache.commons.lang3.StringUtils;

@MInfoTipo(nome = "MTipoPaciente", pacote = MPacotePeticaoCanabidiol.class)
public class MTipoPessoa extends MTipoComposto<MIComposto> implements CanabidiolUtil {

    public static final String LABEL_TIPO_DOCUMENTO = "Documento de Identificação Oficial";
    private MTipoDocumentoSelect tipoDocumento;

    @Override
    protected void onCargaTipo(TipoBuilder tb) {
        super.onCargaTipo(tb);

        this
                .addCampoString("nome")
                .as(AtrBasic::new)
                .label("Nome")
                .as(AtrBootstrap::new).colPreference(6);

        this
                .addCampoData("dataNascimento")
                .as(AtrBasic::new)
                .label("Data de Nascimento")
                .as(AtrBootstrap::new).colPreference(3);

        //Criacção de tipos withselction fica muito truncada caso seja necessário manter a referencia ao objeto para interactions e para adicionar atributos.

        //ruim: Para  adicionar atributos não é possivel adicionar selection
        //ruim: Para manter a referencia não pode acionar atributos:
        tipoDocumento = this.addCampo("tipoDocumento", MTipoDocumentoSelect.class);
        tipoDocumento.as(AtrBasic::new)
                .label(LABEL_TIPO_DOCUMENTO)
                .as(AtrBootstrap::new)
                .colPreference(6);

        this
                .addCampoString("nomeNoDocumento")
                .as(AtrBasic::new)
                .label("Nome")
                .visivel(ins -> "55358729".equals(getValue(ins, tipoDocumento)))
                .dependsOn(tipoDocumento)
                .as(AtrBootstrap::new)
                .colPreference(3);

        this
                .addCampoString("numeroDocumento")
                .as(AtrBasic::new)
                .label("Número")
                .visivel(ins -> ins.findNearestValue(tipoDocumento).orElse(null) != null)
                .dependsOn(tipoDocumento)
                .as(AtrBootstrap::new).colPreference(2);

        this.addCampoCPF("cpf")
                .as(AtrBasic::new)
                .label("CPF")
                .as(AtrBootstrap::new)
                .colPreference(3);


        this.addCampoString("passaporte")
                .as(AtrBasic::new)
                .label("Número do Passaporte")
                .as(AtrBootstrap::new)
                .colPreference(3);

        this.addCampo("endereco", MTipoEndereco.class)
                .as(AtrBasic::new)
                .label("Endereço");

        this.addCampo("contato", MTipoContato.class);
    }

    public MTipoDocumentoSelect getTipoDocumento() {
        return tipoDocumento;
    }
}
