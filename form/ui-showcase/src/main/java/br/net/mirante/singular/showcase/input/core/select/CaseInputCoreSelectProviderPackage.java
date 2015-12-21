package br.net.mirante.singular.showcase.input.core.select;

import br.net.mirante.singular.form.mform.MPacote;
import br.net.mirante.singular.form.mform.MTipoComposto;
import br.net.mirante.singular.form.mform.PacoteBuilder;
import br.net.mirante.singular.form.mform.basic.ui.AtrBasic;
import br.net.mirante.singular.form.mform.core.MTipoString;
import br.net.mirante.singular.form.mform.options.MTipoSelectItem;

public class CaseInputCoreSelectProviderPackage extends MPacote {

    @Override
    protected void carregarDefinicoes(PacoteBuilder pb) {
        MTipoComposto<?> tipoMyForm = pb.createTipoComposto("testForm");

        /*
         * Neste caso será utilizado o serviço de nome filesChoiceProvider
         * cadastrado através do Document.bindLocalService
         */
        MTipoString tipoArquivo = tipoMyForm.addCampoString("opcoesDeArquivo");
        tipoArquivo.withSelectionFromProvider("filesChoiceProvider");
        tipoArquivo.as(AtrBasic::new).label("Seleção de Arquivos Persistidos");

    }

}
