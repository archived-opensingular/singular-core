package br.net.mirante.singular.showcase.custom;

import br.net.mirante.singular.showcase.CaseBase;
import br.net.mirante.singular.showcase.ResourceRef;

import java.util.Optional;

public class CaseCustomStringMapper extends CaseBase {

    public CaseCustomStringMapper() {
        super("Custom Mapper", "Material Desing Input");
        final Optional<ResourceRef> customStringMapper = ResourceRef.forSource(MaterialDesignInputMapper.class);
        if(customStringMapper.isPresent()) {
            getAditionalSources().add(customStringMapper.get());
        }
    }
}
