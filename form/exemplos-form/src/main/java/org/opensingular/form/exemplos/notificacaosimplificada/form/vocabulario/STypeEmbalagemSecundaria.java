package org.opensingular.form.exemplos.notificacaosimplificada.form.vocabulario;

import org.opensingular.form.exemplos.notificacaosimplificada.domain.EmbalagemSecundaria;
import org.opensingular.form.SIComposite;
import org.opensingular.form.SInfoType;
import org.opensingular.form.STypeComposite;
import org.opensingular.form.TypeBuilder;
import org.opensingular.form.converter.SInstanceConverter;
import org.opensingular.form.type.core.STypeInteger;
import org.opensingular.form.type.core.STypeString;
import org.opensingular.form.util.transformer.Value;
import org.opensingular.form.view.SViewAutoComplete;

@SInfoType(spackage = SPackageVocabularioControlado.class)
public class STypeEmbalagemSecundaria extends STypeComposite<SIComposite> {

    public STypeString  descricao;
    public STypeInteger id;

    @Override
    protected void onLoadType(TypeBuilder tb) {

        id = addFieldInteger("id");
        descricao = addFieldString("descricao");

        asAtrBootstrap()
                .colPreference(6)
                .asAtr()
                .label("Embalagem secundária");
        this.setView(SViewAutoComplete::new);

        this.autocompleteOf(EmbalagemSecundaria.class)
                .id("${id}")
                .display("${descricao}")
                .converter(new SInstanceConverter<EmbalagemSecundaria, SIComposite>() {
                    @Override
                    public void fillInstance(SIComposite ins, EmbalagemSecundaria obj) {
                        ins.setValue(id, obj.getId());
                        ins.setValue(descricao, obj.getDescricao());
                    }
                    @Override
                    public EmbalagemSecundaria toObject(SIComposite ins) {
                        final EmbalagemSecundaria es = new EmbalagemSecundaria();
                        es.setId(Value.of(ins, id).longValue());
                        es.setDescricao(Value.of(ins, descricao));
                        return es;
                    }
                })
                .simpleProvider((ins) -> SPackageVocabularioControlado.dominioService(ins).embalagensSecundarias(null));


    }


}
