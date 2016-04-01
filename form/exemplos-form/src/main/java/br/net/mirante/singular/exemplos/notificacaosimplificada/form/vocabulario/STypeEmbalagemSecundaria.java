package br.net.mirante.singular.exemplos.notificacaosimplificada.form.vocabulario;

import br.net.mirante.singular.exemplos.notificacaosimplificada.domain.EmbalagemSecundaria;
import br.net.mirante.singular.form.mform.SIComposite;
import br.net.mirante.singular.form.mform.SIList;
import br.net.mirante.singular.form.mform.SInfoType;
import br.net.mirante.singular.form.mform.STypeComposite;
import br.net.mirante.singular.form.mform.TypeBuilder;
import br.net.mirante.singular.form.mform.basic.view.SViewAutoComplete;
import br.net.mirante.singular.form.mform.core.STypeString;

import static br.net.mirante.singular.exemplos.notificacaosimplificada.form.vocabulario.SPackageVocabularioControlado.dominioService;

@SInfoType(spackage = SPackageVocabularioControlado.class)
public class STypeEmbalagemSecundaria extends STypeComposite<SIComposite> {

    public STypeString descricaoEmbalagemSecundaria;

    @Override
    protected void onLoadType(TypeBuilder tb) {

        STypeString idEmbalagemSecundaria = addFieldString("id");
        descricaoEmbalagemSecundaria = addFieldString("descricao");

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

}
