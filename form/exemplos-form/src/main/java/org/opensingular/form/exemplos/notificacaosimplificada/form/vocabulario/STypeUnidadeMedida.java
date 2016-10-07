package org.opensingular.form.exemplos.notificacaosimplificada.form.vocabulario;

import org.opensingular.form.exemplos.notificacaosimplificada.domain.UnidadeMedida;
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
public class STypeUnidadeMedida extends STypeComposite<SIComposite> {

    public STypeString sigla;
    public STypeString descricao;
    public STypeInteger id;

    @Override
    protected void onLoadType(TypeBuilder tb) {
        super.onLoadType(tb);
        id = this.addFieldInteger("id");
        sigla = this.addFieldString("sigla");
        descricao = this.addFieldString("descricao");
        {

            this
                    .asAtr()
                    .required()
                    .label("Unidade de medida")
                    .asAtrBootstrap()
                    .colPreference(4);
            this.setView(SViewAutoComplete::new);

            this.selectionOf(UnidadeMedida.class)
                    .id("${id}")
                    .display("${sigla} - ${descricao}")
                    .converter(new SInstanceConverter<UnidadeMedida, SIComposite>() {
                        @Override
                        public void fillInstance(SIComposite ins, UnidadeMedida obj) {
                            ins.setValue(id, obj.getId());
                            ins.setValue(sigla, obj.getSigla());
                            ins.setValue(descricao, obj.getDescricao());
                        }

                        @Override
                        public UnidadeMedida toObject(SIComposite ins) {
                            return SPackageVocabularioControlado.dominioService(ins).unidadesMedida(null)
                                    .stream().filter(u -> Integer.valueOf(u.getId().intValue()).equals(Value.of(ins, id)))
                                    .findFirst()
                                    .orElse(null);
                        }
                    }).simpleProvider((ins) -> SPackageVocabularioControlado.dominioService(ins).unidadesMedida(null));

        }
    }


}
