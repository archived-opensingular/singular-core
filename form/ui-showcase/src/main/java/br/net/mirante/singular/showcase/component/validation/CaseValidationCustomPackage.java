package br.net.mirante.singular.showcase.component.validation;

import br.net.mirante.singular.exemplos.canabidiol.custom.AceitoTudoMapper;
import br.net.mirante.singular.form.mform.SPackage;
import br.net.mirante.singular.form.mform.STypeComposite;
import br.net.mirante.singular.form.mform.PackageBuilder;
import br.net.mirante.singular.form.mform.basic.ui.AtrBasic;
import br.net.mirante.singular.form.mform.core.AtrCore;
import br.net.mirante.singular.form.mform.core.SIBoolean;
import br.net.mirante.singular.form.mform.core.STypeBoolean;
import br.net.mirante.singular.form.mform.core.STypeInteger;

public class CaseValidationCustomPackage extends SPackage {

    @Override
    protected void carregarDefinicoes(PackageBuilder pb) {

        STypeComposite<?> tipoMyForm = pb.createTipoComposto("testForm");
        STypeInteger mTipoInteger = tipoMyForm.addCampoInteger("qtd");
        mTipoInteger.as(AtrBasic::new).label("Quantidade");
        mTipoInteger.as(AtrCore::new).obrigatorio();
        mTipoInteger.addInstanceValidator(validatable -> {
            if(validatable.getInstance().getInteger() > 1000){
                validatable.error("O Campo deve ser menor que 1000");
            }
        });

        STypeBoolean aceitoTudo = tipoMyForm.addCampoBoolean("aceitoTudo");

        aceitoTudo
                .withCustomMapper(AceitoTudoMapper::new)
                .addInstanceValidator(validatable -> {
                    SIBoolean instance = validatable.getInstance();
                    if (instance.getValue() != Boolean.TRUE) {
                        validatable.error("O Campo deve ser menor que 1000");
//                            validatable.error(new ValidationError(instance, ValidationErrorLevel.ERROR, "Campo obrigatório"));
                    }
                })
                .as(AtrBasic::new)
                .label("Eu, paciente/responsável legal, informo que estou ciente que:\n\n" +
                        "1- Este produto não possui registro no Brasil, portanto não possui a sua segurança e eficácia avaliada e comprovada pela Anvisa, podendo causar reações adversas inesperadas ao paciente.\n" +
                        "2- Este produto é de uso estritamente pessoal e intransferível, sendo proibida a sua entrega a terceiros, doação, venda ou qualquer outra utilização diferente da indicada.\n" +
                        "3- Que a cópia do Ofício emitido pela Anvisa deve ser mantida junto ao PRODUTO, sempre que em trânsito, dentro ou fora do Brasil. ");

    }
}
