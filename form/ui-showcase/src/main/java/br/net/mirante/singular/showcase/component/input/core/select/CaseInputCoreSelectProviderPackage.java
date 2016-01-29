package br.net.mirante.singular.showcase.component.input.core.select;

import br.net.mirante.singular.form.mform.SPackage;
import br.net.mirante.singular.form.mform.STypeComposto;
import br.net.mirante.singular.form.mform.PacoteBuilder;
import br.net.mirante.singular.form.mform.basic.ui.AtrBasic;
import br.net.mirante.singular.form.mform.core.STypeString;

public class CaseInputCoreSelectProviderPackage extends SPackage {

    @Override
    protected void carregarDefinicoes(PacoteBuilder pb) {
        STypeComposto<?> tipoMyForm = pb.createTipoComposto("testForm");

        /*
         * Neste caso será utilizado o serviço de nome filesChoiceProvider
         * cadastrado através do Document.bindLocalService
         */
        STypeString tipoArquivo = tipoMyForm.addCampoString("opcoesDeArquivo");
        tipoArquivo.withSelectionFromProvider("filesChoiceProvider");
        tipoArquivo.as(AtrBasic::new).label("Seleção de Arquivos Persistidos");

    }

}
