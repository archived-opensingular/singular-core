package br.net.mirante.singular.exemplos.notificacaosimplificada.baixocusto;

import br.net.mirante.singular.exemplos.notificacaosimplificada.domain.EmbalagemPrimariaBasica;
import br.net.mirante.singular.form.mform.*;
import br.net.mirante.singular.form.mform.basic.view.SViewAutoComplete;
import br.net.mirante.singular.form.mform.core.STypeString;

import static br.net.mirante.singular.exemplos.notificacaosimplificada.baixocusto.SPackageNotificacaoSimplificada.dominioService;

@SInfoType(spackage = SPackageNotificacaoSimplificada.class)
public class STypeEmbalagemPrimaria extends STypeComposite<SIComposite> {

    @Override
    protected void onLoadType(TypeBuilder tb) {
        super.onLoadType(tb);
        STypeString idEmbalagemPrimaria        = this.addFieldString("id");
        STypeString descricaoEmbalagemPrimaria = this.addFieldString("descricao");
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

    STypeString getDescricaoEmbalagemPrimaria() {
        return (STypeString) getField("descricao");
    }

}