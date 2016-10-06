package org.opensingular.singular.exemplos.notificacaosimplificada.form;

import org.opensingular.singular.exemplos.notificacaosimplificada.domain.geral.EnderecoEmpresaInternacional;
import org.opensingular.singular.form.SIComposite;
import org.opensingular.singular.form.SInfoType;
import org.opensingular.singular.form.SInstance;
import org.opensingular.singular.form.STypeComposite;
import org.opensingular.singular.form.TypeBuilder;
import org.opensingular.singular.form.converter.SInstanceConverter;
import org.opensingular.singular.form.provider.TextQueryProvider;
import org.opensingular.singular.form.type.core.STypeInteger;
import org.opensingular.singular.form.type.core.STypeString;
import org.opensingular.singular.form.util.transformer.Value;

import static org.opensingular.singular.exemplos.notificacaosimplificada.form.vocabulario.SPackageVocabularioControlado.dominioService;

@SInfoType(spackage = SPackageNotificacaoSimplificada.class)
public class STypeEmpresaInternacional extends STypeComposite<SIComposite> {

    @Override
    protected void onLoadType(TypeBuilder tb) {
        super.onLoadType(tb);

        final STypeComposite<SIComposite> id = addFieldComposite("id");

        final STypeInteger idEmpresaInternacional = id.addFieldInteger("idEmpresaInternacional");
        final STypeInteger sequencialEndereco     = id.addFieldInteger("sequencialEndereco");

        final STypeString razaoSocial = addFieldString("razaoSocial");
        final STypeString endereco    = addFieldString("endereco");

        razaoSocial.
                asAtr()
                .required()
                .label("Razão Social");

        autocompleteOf(EnderecoEmpresaInternacional.class)
                .id(e -> String.format("%d-%d", e.getId().getEmpresaInternacional().getId(), e.getId().getSequencialEndereco()))
                .display(e -> e.getEmpresaInternacional().getRazaoSocial())
                .converter(new SInstanceConverter<EnderecoEmpresaInternacional, SIComposite>() {
                    @Override
                    public void fillInstance(SIComposite ins, EnderecoEmpresaInternacional obj) {
                        final SIComposite compositeId;
                        if (ins.getField(id.getNameSimple()) != null) {
                            compositeId = (SIComposite) ins.getField(id.getNameSimple());
                        } else {
                            compositeId = id.newInstance();
                            ins.setValue(id, compositeId);
                        }
                        compositeId.setValue(idEmpresaInternacional, obj.getId().getEmpresaInternacional().getId());
                        compositeId.setValue(sequencialEndereco, obj.getId().getSequencialEndereco());
                        ins.setValue(razaoSocial, obj.getEmpresaInternacional().getRazaoSocial());
                        ins.setValue(endereco, obj.getEnderecoCompleto());
                    }

                    @Override
                    public EnderecoEmpresaInternacional toObject(SIComposite ins) {
                        final SInstance value = (SInstance) ins.getField(id.getNameSimple());
                        if (!value.isEmptyOfData()) {
                            final Integer idEmpresa  = Value.of(value, idEmpresaInternacional);
                            final Integer sequencial = Value.of(value, sequencialEndereco);
                            return dominioService(ins).buscarEmpresaInternacional(idEmpresa.longValue(), sequencial.shortValue());
                        }
                        return null;
                    }
                })
                .filteredProvider((TextQueryProvider<EnderecoEmpresaInternacional, SIComposite>) (ins, query) -> dominioService(ins).empresaInternacional(query));

    }


}
