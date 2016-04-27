package br.net.mirante.singular.exemplos.notificacaosimplificada.form;

import br.net.mirante.singular.form.mform.SIComposite;
import br.net.mirante.singular.form.mform.SInfoType;
import br.net.mirante.singular.form.mform.STypeComposite;
import br.net.mirante.singular.form.mform.TypeBuilder;
import br.net.mirante.singular.form.mform.core.STypeString;

@SInfoType(spackage = SPackageNotificacaoSimplificada.class)
public class STypeEmpresaInternacional extends STypeComposite<SIComposite> {

    @Override
    protected void onLoadType(TypeBuilder tb) {
        super.onLoadType(tb);

        final STypeString id = addFieldString("id");
        final STypeString razaoSocial = addFieldString("razaoSocial");
        final STypeString endereco = addFieldString("endereco");

        razaoSocial.
                asAtrBasic()
                .required()
                .label("Razão Social");

        //TODO DANILO
//        withSelectionFromProvider(razaoSocial, (ins, filter) -> {
//            final SIList<?> list = ins.getType().newList();
//            for (EnderecoEmpresaInternacional eei : dominioService(ins).empresaInternacional(filter)) {
//                final SIComposite c = (SIComposite) list.addNew();
//                c.setValue(id, eei.getId());
//                c.setValue(razaoSocial, eei.getEmpresaInternacional().getRazaoSocial());
//                c.setValue(endereco, eei.getEnderecoCompleto());
//            }
//            return list;
//        }).asAtrBasic().label("Empresa internacional").getTipo().setView(SViewAutoComplete::new);

    }


}
