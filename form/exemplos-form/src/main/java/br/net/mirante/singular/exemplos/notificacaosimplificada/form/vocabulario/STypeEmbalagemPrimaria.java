package br.net.mirante.singular.exemplos.notificacaosimplificada.form.vocabulario;

import br.net.mirante.singular.exemplos.notificacaosimplificada.domain.EmbalagemPrimariaBasica;
import br.net.mirante.singular.form.mform.SIComposite;
import br.net.mirante.singular.form.mform.SIList;
import br.net.mirante.singular.form.mform.SInfoType;
import br.net.mirante.singular.form.mform.STypeComposite;
import br.net.mirante.singular.form.mform.TypeBuilder;
import br.net.mirante.singular.form.mform.basic.view.SViewAutoComplete;
import br.net.mirante.singular.form.mform.core.STypeString;

import static br.net.mirante.singular.exemplos.notificacaosimplificada.form.vocabulario.SPackageVocabularioControlado.dominioService;

@SInfoType(spackage = SPackageVocabularioControlado.class)
public class STypeEmbalagemPrimaria extends STypeComposite<SIComposite> {

    public STypeString descricaoEmbalagemPrimaria;

    @Override
    protected void onLoadType(TypeBuilder tb) {
        super.onLoadType(tb);
        STypeString idEmbalagemPrimaria        = this.addFieldString("id");
        descricaoEmbalagemPrimaria = this.addFieldString("descricao");
        {
            this
                    .asAtrBootstrap()
                    .colPreference(6)
                    .asAtrBasic()
                    .label("Embalagem primária")
                    .required()
                    .getTipo().setView(SViewAutoComplete::new);

            this.withSelectionFromProvider(descricaoEmbalagemPrimaria, (ins, filter) -> {
                final SIList<?> list = ins.getType().newList();
                for (EmbalagemPrimariaBasica emb : dominioService(ins).findEmbalagensBasicas(filter)) {
                    final SIComposite c = (SIComposite) list.addNew();
                    c.setValue(idEmbalagemPrimaria, emb.getId());
                    c.setValue(descricaoEmbalagemPrimaria, emb.getDescricao());
                }
                return list;
            });
        }
    }


}
