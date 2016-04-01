package br.net.mirante.singular.exemplos.notificacaosimplificada.form.vocabulario;

import br.net.mirante.singular.exemplos.notificacaosimplificada.domain.CategoriaRegulatoriaMedicamento;
import br.net.mirante.singular.form.mform.SIComposite;
import br.net.mirante.singular.form.mform.SIList;
import br.net.mirante.singular.form.mform.SInfoType;
import br.net.mirante.singular.form.mform.STypeComposite;
import br.net.mirante.singular.form.mform.TypeBuilder;
import br.net.mirante.singular.form.mform.basic.view.SViewAutoComplete;
import br.net.mirante.singular.form.mform.basic.view.SViewSelectionBySelect;
import br.net.mirante.singular.form.mform.core.STypeInteger;
import br.net.mirante.singular.form.mform.core.STypeString;

import static br.net.mirante.singular.exemplos.notificacaosimplificada.form.vocabulario.SPackageVocabularioControlado.dominioService;

@SInfoType(spackage = SPackageVocabularioControlado.class)
public class STypeCategoriaRegulatoria extends STypeComposite<SIComposite> {

    public STypeString descricao;
    public STypeInteger id;

    @Override
    protected void onLoadType(TypeBuilder tb) {
        super.onLoadType(tb);
        id = this.addFieldInteger("id");
        descricao = this.addFieldString("descricao");
        {
            this
                    .asAtrBootstrap()
                    .colPreference(4)
                    .asAtrBasic()
                    .label("Classe")
                    .required();
            this.setView(() -> new SViewAutoComplete(SViewAutoComplete.Mode.DYNAMIC));

            this.withSelectionFromProvider(descricao, (ins, filter) -> {
                final SIList<?> list = ins.getType().newList();
                for (CategoriaRegulatoriaMedicamento cat : dominioService(ins).listCategoriasRegulatorias()) {
                    final SIComposite c = (SIComposite) list.addNew();
                    c.setValue(id, cat.getId());
                    c.setValue(descricao, cat.getDescricao());
                }
                return list;
            });
        }
    }


}
