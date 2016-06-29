package br.net.mirante.singular.exemplos.notificacaosimplificada.form.vocabulario;

import br.net.mirante.singular.exemplos.notificacaosimplificada.domain.EmbalagemSecundaria;
import br.net.mirante.singular.form.SIComposite;
import br.net.mirante.singular.form.SInfoType;
import br.net.mirante.singular.form.STypeComposite;
import br.net.mirante.singular.form.TypeBuilder;
import br.net.mirante.singular.form.converter.SInstanceConverter;
import br.net.mirante.singular.form.type.core.STypeInteger;
import br.net.mirante.singular.form.type.core.STypeString;
import br.net.mirante.singular.form.util.transformer.Value;
import br.net.mirante.singular.form.view.SViewAutoComplete;

import static br.net.mirante.singular.exemplos.notificacaosimplificada.form.vocabulario.SPackageVocabularioControlado.dominioService;

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
                .simpleProvider((ins) -> dominioService(ins).embalagensSecundarias(null));


    }


}
