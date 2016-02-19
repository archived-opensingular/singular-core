package br.net.mirante.singular.showcase.component.input.core.select;

import br.net.mirante.singular.form.mform.SPackage;
import br.net.mirante.singular.form.mform.STypeComposite;
import br.net.mirante.singular.form.mform.PackageBuilder;
import br.net.mirante.singular.form.mform.core.STypeString;

public class CaseInputCoreSelectCompositePackage extends SPackage {

    @Override
    protected void carregarDefinicoes(PackageBuilder pb) {
        STypeComposite<?> tipoMyForm = pb.createTipoComposto("testForm");

        /**
         * Neste caso os campos de chave e valor utilizados serão os padrões "id" e "value".
         */
        STypeComposite ingredienteQuimico = tipoMyForm.addCampoComposto("ingredienteQuimico");
        STypeString formulaQuimica = ingredienteQuimico.addCampoString("formulaQuimica");
        STypeString nome = ingredienteQuimico.addCampoString("nome");

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



        STypeComposite ingredienteQuimicoComplexo = tipoMyForm.addCampoComposto("ingredienteQuimicoComplexo");
        STypeString formulaQuimicaComplexa = ingredienteQuimicoComplexo.addCampoString("formulaQuimica");
        STypeString inventor = ingredienteQuimicoComplexo.addCampoString("inventorFormulaQuimica");
        STypeString nomeComplexo = ingredienteQuimicoComplexo.addCampoString("nome");

        ingredienteQuimicoComplexo.withSelectionFromProvider(nomeComplexo, (instancia, lb) -> {
            lb
                    .add()
                    .set(formulaQuimicaComplexa, "h2o")
                    .set(nomeComplexo, "Água")
                    .set(inventor, "Alan Touring")
                    .add()
                    .set(formulaQuimicaComplexa, "h2o2")
                    .set(nomeComplexo, "Água Oxigenada")
                    .set(inventor, "Santos Dumont")
                    .add()
                    .set(formulaQuimicaComplexa, "o2")
                    .set(nomeComplexo, "Gás Oxigênio")
                    .set(inventor, "Thomas Edinson")
                    .add()
                    .set(formulaQuimicaComplexa, "C12H22O11")
                    .set(nomeComplexo, "Açúcar")
                    .set(inventor, "Rogério Skylab");
        });

        ingredienteQuimicoComplexo.asAtrBasic().label("Seleção de Componentes Químicos Detalhados");

    }
}
