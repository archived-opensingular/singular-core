package br.net.mirante.singular.showcase.component.input.core.select;

import br.net.mirante.singular.form.mform.MPacote;
import br.net.mirante.singular.form.mform.MTipoComposto;
import br.net.mirante.singular.form.mform.PacoteBuilder;
import br.net.mirante.singular.form.mform.basic.ui.AtrBasic;

public class CaseInputCoreSelectCompositePackage extends MPacote {

    @Override
    protected void carregarDefinicoes(PacoteBuilder pb) {
        MTipoComposto<?> tipoMyForm = pb.createTipoComposto("testForm");

        /**
         * Neste caso os campos de chave e valor utilizados serão os padrões "id" e "value".
         */
        MTipoComposto<?> ingredienteQuimico = tipoMyForm.addCampoComposto("ingredienteQuimico");
        ingredienteQuimico.withSelectionOf(
                ingredienteQuimico.create("h2o", "Água"),
                ingredienteQuimico.create("h2o2", "Água Oxigenada"),
                ingredienteQuimico.create("o2", "Gás Oxigênio"),
                ingredienteQuimico.create("C12H22O11", "Açúcar")
        );
        ingredienteQuimico.as(AtrBasic::new).label("Seleção de Componentes Químicos");

        /**
         * Outra forma de se adicionar elementos é através do provedor padrão.
         */
        MTipoComposto<?> conjuntoNumerico = tipoMyForm.addCampoComposto("numberSet");
        conjuntoNumerico.withSelection()
                .add("N", "Naturais")
                .add("Z", "Inteiros")
                .add("Q", "Racionais")
                .add("AR", "Racionais Algébricos")
                .add("I", "Imaginários")
                .add("A", "Algébricos")
                .add("C", "Complexos");
        conjuntoNumerico.as(AtrBasic::new).label("Conjunto Numérico");


        /**
         * Neste caso os campos de chave e valor utilizados serão os definidos por
         * "abreviado" e "descricao".
         */
        MTipoComposto<?> sexo = tipoMyForm.addCampoComposto("sexo");
        sexo.withKeyValueField("abreviado","descricao");
        sexo.withSelectionOf(
                sexo.create("N", "Não Informado"),
                sexo.create("M", "Masculino"),
                sexo.create("F", "Feminido")
        );
        sexo.as(AtrBasic::new).label("Sexo");
    }
}
