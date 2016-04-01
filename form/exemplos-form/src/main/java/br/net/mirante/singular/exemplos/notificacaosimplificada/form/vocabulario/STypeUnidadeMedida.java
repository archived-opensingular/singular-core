package br.net.mirante.singular.exemplos.notificacaosimplificada.form.vocabulario;

import br.net.mirante.singular.exemplos.notificacaosimplificada.domain.LinhaCbpf;
import br.net.mirante.singular.exemplos.notificacaosimplificada.domain.UnidadeMedida;
import br.net.mirante.singular.form.mform.SIComposite;
import br.net.mirante.singular.form.mform.SIList;
import br.net.mirante.singular.form.mform.SInfoType;
import br.net.mirante.singular.form.mform.STypeComposite;
import br.net.mirante.singular.form.mform.TypeBuilder;
import br.net.mirante.singular.form.mform.basic.view.SViewAutoComplete;
import br.net.mirante.singular.form.mform.core.STypeInteger;
import br.net.mirante.singular.form.mform.core.STypeString;

import static br.net.mirante.singular.exemplos.notificacaosimplificada.form.vocabulario.SPackageVocabularioControlado.dominioService;

@SInfoType(spackage = SPackageVocabularioControlado.class)
public class STypeUnidadeMedida extends STypeComposite<SIComposite> {

    public STypeString descricao;
    public STypeInteger id;

    @Override
    protected void onLoadType(TypeBuilder tb) {
        super.onLoadType(tb);
        id = this.addFieldInteger("id");
        descricao = this.addFieldString("descricao");
        {

            this
                    .asAtrBasic()
                    .required()
                    .label("Unidade de medida")
                    .asAtrBootstrap()
                    .colPreference(3);
            this.setView(() -> new SViewAutoComplete(SViewAutoComplete.Mode.DYNAMIC));


            this.withSelectionFromProvider(descricao, (ins, filter) -> {
                final SIList<?> list = ins.getType().newList();
                for (UnidadeMedida lc : dominioService(ins).unidadesMedida(filter)) {
                    final SIComposite c = (SIComposite) list.addNew();
                    c.setValue(id, lc.getId());
                    c.setValue(descricao, lc.getSigla() + "- " + lc.getDescricao());
                }
                return list;
            });

        }
    }


}
