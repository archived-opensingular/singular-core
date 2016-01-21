package br.net.mirante.singular.showcase.component.input.core.select;

import br.net.mirante.singular.form.mform.MPacote;
import br.net.mirante.singular.form.mform.MTipoComposto;
import br.net.mirante.singular.form.mform.PacoteBuilder;
import br.net.mirante.singular.form.mform.basic.ui.AtrBasic;
import br.net.mirante.singular.form.mform.core.MTipoString;

public class CaseInputCoreSelectCompositePackage extends MPacote {

    @Override
    protected void carregarDefinicoes(PacoteBuilder pb) {
        MTipoComposto<?> tipoMyForm = pb.createTipoComposto("testForm");

        /**
         * Neste caso os campos de chave e valor utilizados serão os padrões "id" e "value".
         */
        MTipoString ingredienteQuimico = tipoMyForm.addCampoString("ingredienteQuimico");
        ingredienteQuimico.withSelection()
                .add("h2o", "Água")
                .add("h2o2", "Água Oxigenada")
                .add("o2", "Gás Oxigênio")
                .add("C12H22O11", "Açúcar");

        ingredienteQuimico.as(AtrBasic::new).label("Seleção de Componentes Químicos");

        MTipoString ingredienteQuimicoSemChave = tipoMyForm.addCampoString("ingredienteQuimicoSemChave");
        ingredienteQuimicoSemChave.withSelectionOf(
                "Água", "Água Oxigenada", "Gás Oxigênio", "Açúcar"
        );
        ingredienteQuimicoSemChave.as(AtrBasic::new).label("Seleção de Componentes Químicos - Sem chave, apenas valor");

        /**
         * Outra forma de se adicionar elementos é através do provedor padrão.
         */
        MTipoString conjuntoNumerico = tipoMyForm.addCampoString("numberSet");
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
        MTipoString sexo = tipoMyForm.addCampoString("sexo");
        sexo.withSelection()
                .add("N", "Não Informado")
                .add("M", "Masculino")
                .add("F", "Feminido");
        sexo.as(AtrBasic::new).label("Sexo");
    }
}
