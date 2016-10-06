package org.opensingular.singular.exemplos.notificacaosimplificada.form.vocabulario;

import org.opensingular.singular.exemplos.notificacaosimplificada.domain.Farmacopeia;
import org.opensingular.singular.exemplos.notificacaosimplificada.domain.generic.VocabularioControlado;
import org.opensingular.singular.form.SIComposite;
import org.opensingular.singular.form.SInfoType;
import org.opensingular.singular.form.STypeComposite;
import org.opensingular.singular.form.TypeBuilder;
import org.opensingular.singular.form.converter.SInstanceConverter;
import org.opensingular.singular.form.type.core.STypeInteger;
import org.opensingular.singular.form.type.core.STypeString;
import org.opensingular.singular.form.util.transformer.Value;

import static org.opensingular.singular.exemplos.notificacaosimplificada.form.vocabulario.SPackageVocabularioControlado.dominioService;

@SInfoType(spackage = SPackageVocabularioControlado.class)
public class STypeFarmacopeia extends STypeComposite<SIComposite> {

    public STypeString descricao;
    public STypeInteger id;

    @Override
    protected void onLoadType(TypeBuilder tb) {
        super.onLoadType(tb);
        id = this.addFieldInteger("id");
        descricao = this.addFieldString("descricao");
        {

            this
                    .asAtr()
                    .required()
                    .label("Farmacopéia")
                    .asAtrBootstrap()
                    .colPreference(4);

            this.autocompleteOf(Farmacopeia.class)
                    .id(f -> f.getId().toString())
                    .display(VocabularioControlado::getDescricao)
                    .converter(new SInstanceConverter<Farmacopeia, SIComposite>() {
                        @Override
                        public void fillInstance(SIComposite ins, Farmacopeia obj) {
                            ins.setValue(id, obj.getId());
                            ins.setValue(descricao, obj.getDescricao());
                        }

                        @Override
                        public Farmacopeia toObject(SIComposite ins) {
                            return dominioService(ins).listFarmacopeias()
                                    .stream().filter(u -> Integer.valueOf(u.getId().intValue()).equals(Value.of(ins, id)))
                                    .findFirst()
                                    .orElse(null);
                        }
                    })
                    .filteredProvider((ins, query) -> dominioService(ins).listFarmacopeias());

        }
    }

}
