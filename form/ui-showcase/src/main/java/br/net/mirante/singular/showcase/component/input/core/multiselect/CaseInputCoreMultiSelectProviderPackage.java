package br.net.mirante.singular.showcase.component.input.core.multiselect;

import br.net.mirante.singular.form.mform.MPacote;
import br.net.mirante.singular.form.mform.MTipoComposto;
import br.net.mirante.singular.form.mform.MTipoLista;
import br.net.mirante.singular.form.mform.PacoteBuilder;
import br.net.mirante.singular.form.mform.basic.ui.AtrBasic;
import br.net.mirante.singular.form.mform.core.MIString;
import br.net.mirante.singular.form.mform.core.MTipoString;

public class CaseInputCoreMultiSelectProviderPackage extends MPacote {
    @Override
    protected void carregarDefinicoes(PacoteBuilder pb) {
        MTipoComposto<?> tipoMyForm = pb.createTipoComposto("testForm");


        /*
         * Neste caso será utilizado o serviço de nome filesChoiceProvider
         * cadastrado através do Document.bindLocalService
         */

        MTipoString tipoArquivo = pb.createTipo("opcoesDeArquivo", MTipoString.class);
        tipoArquivo.withSelectionFromProvider("filesChoiceProvider");
        tipoArquivo.as(AtrBasic::new).label("Seleção de Arquivos Persistidos");


        MTipoLista<MTipoString, MIString> arquivosSelecionados =
                tipoMyForm.addCampoListaOf("arquivos", tipoArquivo);
        arquivosSelecionados.as(AtrBasic::new).label("Seleção de Arquivos Persistidos");

    }
}
