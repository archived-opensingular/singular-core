package br.net.mirante.singular.exemplos.notificacaosimplificada.domain.converter;

import br.net.mirante.singular.exemplos.notificacaosimplificada.domain.dto.VocabularioControladoDTO;
import br.net.mirante.singular.form.SIComposite;
import br.net.mirante.singular.form.SType;
import br.net.mirante.singular.form.converter.SInstanceConverter;
import br.net.mirante.singular.form.util.transformer.Value;

public class VocabularioControladoDTOSInstanceConverter implements SInstanceConverter<VocabularioControladoDTO, SIComposite> {

    private final String id;
    private final String descricao;

    public VocabularioControladoDTOSInstanceConverter(SType id, SType descricao) {
        this.descricao = descricao.getNameSimple();
        this.id = id.getNameSimple();
    }

    @Override
    public void fillInstance(SIComposite ins, VocabularioControladoDTO obj) {
        ins.setValue(id, obj.getId());
        ins.setValue(descricao, obj.getDescricao());
    }

    @Override
    public VocabularioControladoDTO toObject(SIComposite ins) {
        final String realId =  Value.of(ins, id);
        return new VocabularioControladoDTO(Long.valueOf(realId), Value.of(ins, descricao));
    }

}