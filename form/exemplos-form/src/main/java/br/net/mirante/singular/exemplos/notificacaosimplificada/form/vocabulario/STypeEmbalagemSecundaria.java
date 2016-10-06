package br.net.mirante.singular.exemplos.notificacaosimplificada.form.vocabulario;

import br.net.mirante.singular.exemplos.notificacaosimplificada.domain.EmbalagemSecundaria;
import org.opensingular.singular.form.SIComposite;
import org.opensingular.singular.form.SInfoType;
import org.opensingular.singular.form.STypeComposite;
import org.opensingular.singular.form.TypeBuilder;
import org.opensingular.singular.form.converter.SInstanceConverter;
import org.opensingular.singular.form.type.core.STypeInteger;
import org.opensingular.singular.form.type.core.STypeString;
import org.opensingular.singular.form.util.transformer.Value;
import org.opensingular.singular.form.view.SViewAutoComplete;

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
