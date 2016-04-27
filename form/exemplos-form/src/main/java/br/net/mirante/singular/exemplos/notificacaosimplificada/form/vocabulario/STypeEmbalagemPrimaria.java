package br.net.mirante.singular.exemplos.notificacaosimplificada.form.vocabulario;

import br.net.mirante.singular.exemplos.notificacaosimplificada.domain.EmbalagemPrimariaBasica;
import br.net.mirante.singular.exemplos.notificacaosimplificada.domain.generic.VocabularioControlado;
import br.net.mirante.singular.form.mform.*;
import br.net.mirante.singular.form.mform.converter.SInstanceConverter;
import br.net.mirante.singular.form.mform.core.STypeInteger;
import br.net.mirante.singular.form.mform.core.STypeString;
import br.net.mirante.singular.form.mform.provider.FilteredProvider;
import br.net.mirante.singular.form.mform.util.transformer.Value;

import static br.net.mirante.singular.exemplos.notificacaosimplificada.form.vocabulario.SPackageVocabularioControlado.dominioService;

@SInfoType(spackage = SPackageVocabularioControlado.class)
public class STypeEmbalagemPrimaria extends STypeComposite<SIComposite> {

    public STypeString  descricao;
    public STypeInteger id;

    @Override
    protected void onLoadType(TypeBuilder tb) {
        super.onLoadType(tb);
        id = this.addFieldInteger("id");
        descricao = this.addFieldString("descricao");
        {
            this
                    .asAtrBootstrap()
                    .colPreference(6)
                    .asAtrBasic()
                    .label("Embalagem primária")
                    .required();
            this.selectionOf(EmbalagemPrimariaBasica.class)
                    .id("${id}")
                    .display(VocabularioControlado::getDescricao)
                    .converter(new SInstanceConverter<EmbalagemPrimariaBasica>() {
                        @Override
                        public void fillInstance(SInstance ins, EmbalagemPrimariaBasica obj) {
                            ((SIComposite) ins).setValue(id, obj.getId());
                            ((SIComposite) ins).setValue(descricao, obj.getDescricao());
                        }

                        @Override
                        public EmbalagemPrimariaBasica toObject(SInstance ins) {
                            return dominioService(ins).findEmbalagensBasicas(null)
                                    .stream().filter(u -> Integer.valueOf(u.getId().intValue()).equals(Value.of(ins, id)))
                                    .findFirst()
                                    .orElse(null);
                        }
                    })
                    .provider((FilteredProvider) (ins, query) -> dominioService(ins).findEmbalagensBasicas(query));
        }
    }


}
