package br.net.mirante.singular.exemplos.notificacaosimplificada.form.vocabulario;

import br.net.mirante.singular.form.mform.SIComposite;
import br.net.mirante.singular.form.mform.SInfoType;
import br.net.mirante.singular.form.mform.STypeComposite;
import br.net.mirante.singular.form.mform.TypeBuilder;
import br.net.mirante.singular.form.mform.basic.view.SViewAutoComplete;
import br.net.mirante.singular.form.mform.core.STypeInteger;
import br.net.mirante.singular.form.mform.core.STypeString;

@SInfoType(spackage = SPackageVocabularioControlado.class)
public class STypeEmbalagemSecundaria extends STypeComposite<SIComposite> {

    public STypeString descricao;
    public STypeInteger id;

    @Override
    protected void onLoadType(TypeBuilder tb) {

        id = addFieldInteger("id");
        descricao = addFieldString("descricao");

        asAtrBootstrap()
                .colPreference(6)
                .asAtrBasic()
                .label("Embalagem secundária");
        this.setView(SViewAutoComplete::new);
        //TODO DANILO
//        withSelectionFromProvider(descricao, (ins, filter) -> {
//            final SIList<?> list = ins.getType().newList();
//            for (EmbalagemSecundaria es : dominioService(ins).embalagensSecundarias(filter)) {
//                final SIComposite c = (SIComposite) list.addNew();
//                c.setValue(id, es.getId());
//                c.setValue(descricao, es.getDescricao());
//            }
//            return list;
//        });

    }

}
