package br.net.mirante.singular.showcase.component.input.core.select;

import br.net.mirante.singular.form.mform.MPacote;
import br.net.mirante.singular.form.mform.MTipoComposto;
import br.net.mirante.singular.form.mform.PacoteBuilder;
import br.net.mirante.singular.form.mform.core.MTipoString;

public class CaseInputCoreSelectCompositePackage extends MPacote {

    @Override
    protected void carregarDefinicoes(PacoteBuilder pb) {
        MTipoComposto<?> tipoMyForm = pb.createTipoComposto("testForm");

        /**
         * Neste caso os campos de chave e valor utilizados serão os padrões "id" e "value".
         */
        MTipoComposto ingredienteQuimico = tipoMyForm.addCampoComposto("ingredienteQuimico");
        MTipoString formulaQuimica = ingredienteQuimico.addCampoString("formulaQuimica");
        MTipoString nome = ingredienteQuimico.addCampoString("nome");

        ingredienteQuimico.withSelectionFromProvider(nome, (instancia, lb) -> {
            lb
                    .add()
                    .set(formulaQuimica, "h2o")
                    .set(nome, "Água")
                    .add()
                    .set(formulaQuimica, "h2o2")
                    .set(nome, "Água Oxigenada")
                    .add()
                    .set(formulaQuimica, "o2")
                    .set(nome, "Gás Oxigênio")
                    .add()
                    .set(formulaQuimica, "C12H22O11")
                    .set(nome, "Açúcar");
        });

        ingredienteQuimico.asAtrBasic().label("Seleção de Componentes Químicos");

    }
}
