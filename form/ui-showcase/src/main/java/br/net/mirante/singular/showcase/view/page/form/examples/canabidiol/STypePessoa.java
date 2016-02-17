package br.net.mirante.singular.showcase.view.page.form.examples.canabidiol;

import br.net.mirante.singular.form.mform.SIComposite;
import br.net.mirante.singular.form.mform.MInfoTipo;
import br.net.mirante.singular.form.mform.STypeComposite;
import br.net.mirante.singular.form.mform.TypeBuilder;
import br.net.mirante.singular.form.mform.basic.ui.AtrBasic;
import br.net.mirante.singular.form.mform.basic.ui.AtrBootstrap;
import br.net.mirante.singular.form.mform.core.AtrCore;
import br.net.mirante.singular.form.mform.core.annotation.AtrAnnotation;
import br.net.mirante.singular.form.mform.util.transformer.Value;

@MInfoTipo(nome = "MTipoPaciente", pacote = SPackagePeticaoCanabidiol.class)
public class STypePessoa extends STypeComposite<SIComposite> {

    public static final String LABEL_TIPO_DOCUMENTO = "Documento de Identificação Oficial";
    public STypeDocumentoSelect tipoDocumento;

    @Override
    protected void onLoadType(TypeBuilder tb) {
        super.onLoadType(tb);

        this
                .addCampoString("nome")
                .as(AtrCore::new)
                .obrigatorio()
                .as(AtrBasic::new)
                .label("Nome")
                .as(AtrBootstrap::new).colPreference(6);

        this
                .addCampoData("dataNascimento")
                .as(AtrCore::new)
                .obrigatorio()
                .as(AtrBasic::new)
                .label("Data de Nascimento")
                .as(AtrBootstrap::new).colPreference(3);

        //Criacção de tipos withselction fica muito truncada caso seja necessário manter a referencia ao objeto para interactions e para adicionar atributos.

        //ruim: Para  adicionar atributos não é possivel adicionar selection
        //ruim: Para manter a referencia não pode acionar atributos:
        tipoDocumento = this.addCampo("tipoDocumento", STypeDocumentoSelect.class);
        tipoDocumento
                .as(AtrCore::new)
                .obrigatorio()
                .as(AtrBasic::new)
                .label(LABEL_TIPO_DOCUMENTO)
                .as(AtrBootstrap::new)
                .colPreference(6);

        this
                .addCampoString("nomeNoDocumento")
                .as(AtrCore::new)
                .obrigatorio(ins -> "55358729".equals(Value.of(ins, tipoDocumento)))
                .as(AtrBasic::new)
                .label("Nome")
                .visivel(ins -> "55358729".equals(Value.of(ins, tipoDocumento)))
                .dependsOn(tipoDocumento)
                .as(AtrBootstrap::new)
                .colPreference(3);

        this
                .addCampoString("numeroDocumento")
                .as(AtrCore::new)
                .obrigatorio()
                .as(AtrBasic::new)
                .label("Número")
                .visivel(ins -> ins.findNearestValue(tipoDocumento).orElse(null) != null)
                .dependsOn(tipoDocumento)
                .as(AtrBootstrap::new).colPreference(2);

        this.addCampoCPF("cpf")
                .as(AtrCore::new)
                .obrigatorio()
                .as(AtrBasic::new)
                .label("CPF")
                .as(AtrBootstrap::new)
                .colPreference(3);


        this.addCampoString("passaporte")
                .as(AtrBasic::new)
                .label("Número do Passaporte")
                .as(AtrBootstrap::new)
                .colPreference(3);

        this.addCampo("endereco", STypeEndereco.class)
                .as(AtrBasic::new)
                .label("Endereço")
                .as(AtrAnnotation::new).setAnnotated();

        this.addCampo("contato", STypeContato.class)
                .as(AtrAnnotation::new).setAnnotated();
    }

}
