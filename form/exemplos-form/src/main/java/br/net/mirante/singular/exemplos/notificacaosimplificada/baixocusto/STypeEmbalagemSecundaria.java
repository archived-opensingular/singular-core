package br.net.mirante.singular.exemplos.notificacaosimplificada.baixocusto;

import br.net.mirante.singular.exemplos.notificacaosimplificada.domain.EmbalagemSecundaria;
import br.net.mirante.singular.form.mform.*;
import br.net.mirante.singular.form.mform.basic.view.SViewAutoComplete;
import br.net.mirante.singular.form.mform.core.STypeString;

import static br.net.mirante.singular.exemplos.notificacaosimplificada.baixocusto.SPackageNotificacaoSimplificada.dominioService;

@SInfoType(spackage = SPackageNotificacaoSimplificada.class)
public class STypeEmbalagemSecundaria extends STypeComposite<SIComposite> {

    @Override
    protected void onLoadType(TypeBuilder tb) {

        STypeString idEmbalagemSecundaria        = addFieldString("id");
        STypeString descricaoEmbalagemSecundaria = addFieldString("descricao");

        asAtrBootstrap()
                .colPreference(6)
                .asAtrBasic()
                .label("Embalagem secundária")
                .getTipo().setView(SViewAutoComplete::new);
        withSelectionFromProvider(descricaoEmbalagemSecundaria, (ins, filter) -> {
            final SIList<?> list = ins.getType().newList();
            for (EmbalagemSecundaria es : dominioService(ins).embalagensSecundarias(filter)) {
                final SIComposite c = (SIComposite) list.addNew();
                c.setValue(idEmbalagemSecundaria, es.getId());
                c.setValue(descricaoEmbalagemSecundaria, es.getDescricao());
            }
            return list;
        });

    }

    public STypeString getDescricaoEmbalagemSecundaria() {
        return (STypeString) getField("descricao");
    }


}
