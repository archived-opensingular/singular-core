package br.net.mirante.singular.exemplos.notificacaosimplificada.baixocusto;

import br.net.mirante.singular.form.mform.*;
import br.net.mirante.singular.form.mform.basic.view.SViewAutoComplete;
import br.net.mirante.singular.form.mform.core.STypeString;
import org.apache.commons.lang3.tuple.Triple;

import static br.net.mirante.singular.exemplos.notificacaosimplificada.baixocusto.SPackageNotificacaoSimplificada.dominioService;

@SInfoType(spackage = SPackageNotificacaoSimplificada.class)
public class STypeEmpresaInternacional extends STypeComposite<SIComposite> {

    @Override
    protected void onLoadType(TypeBuilder tb) {
        super.onLoadType(tb);

        final STypeString id          = addFieldString("id");
        final STypeString razaoSocial = addFieldString("razaoSocial");
        final STypeString endereco    = addFieldString("endereco");

        razaoSocial.
                asAtrBasic()
                .required()
                .label("Razão Social");

        withSelectionFromProvider(razaoSocial, (optionsInstance, lb) -> {
            for (Triple p : dominioService(optionsInstance).empresaInternacional()) {
                lb
                        .add()
                        .set(id, p.getLeft())
                        .set(razaoSocial, p.getMiddle())
                        .set(endereco, p.getRight());
            }
        }).asAtrBasic().label("Empresa internacional").getTipo().setView(SViewAutoComplete::new);

    }


}
