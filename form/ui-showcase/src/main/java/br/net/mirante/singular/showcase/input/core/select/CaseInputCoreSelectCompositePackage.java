package br.net.mirante.singular.showcase.input.core.select;

import br.net.mirante.singular.form.mform.MPacote;
import br.net.mirante.singular.form.mform.MTipoComposto;
import br.net.mirante.singular.form.mform.PacoteBuilder;
import br.net.mirante.singular.form.mform.basic.ui.AtrBasic;
import br.net.mirante.singular.form.mform.options.MTipoSelectItem;

public class CaseInputCoreSelectCompositePackage extends MPacote {

    @Override
    protected void carregarDefinicoes(PacoteBuilder pb) {
        MTipoComposto<?> tipoMyForm = pb.createTipoComposto("testForm");

        /**
         * Neste caso os campos de chave e valor utilizados serão os padrões "id" e "value".
         */
        MTipoSelectItem ingredienteQuimico = tipoMyForm.addCampo("ingredienteQuimico",
                MTipoSelectItem.class);
        ingredienteQuimico.withSelectionOf(
                ingredienteQuimico.create("h2o", "Água"),
                ingredienteQuimico.create("h2o2", "Água Oxigenada"),
                ingredienteQuimico.create("o2", "Gás Oxigênio"),
                ingredienteQuimico.create("C12H22O11", "Açúcar")
        );
        ingredienteQuimico.as(AtrBasic::new).label("Seleção de Componentes Químicos");

        /**
         * Neste caso os campos de chave e valor utilizados serão os definidos por
         * "abreviado" e "descricao".
         */
        MTipoSelectItem sexo = tipoMyForm.addCampo("sexo",MTipoSelectItem.class);
        sexo.withKeyValueField("abreviado","descricao");
        sexo.withSelectionOf(
                sexo.create("N", "Não Informado"),
                sexo.create("M", "Masculino"),
                sexo.create("F", "Feminido")
        );
        sexo.as(AtrBasic::new).label("Sexo");
    }
}
