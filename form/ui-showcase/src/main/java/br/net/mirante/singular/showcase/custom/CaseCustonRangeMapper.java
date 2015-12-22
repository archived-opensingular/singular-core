package br.net.mirante.singular.showcase.custom;

import br.net.mirante.singular.showcase.CaseBase;
import br.net.mirante.singular.showcase.ResourceRef;
import java.util.Optional;

public class CaseCustonRangeMapper extends CaseBase {

    public CaseCustonRangeMapper() {
        super("Custom Mapper", "Range Slider");
        final Optional<ResourceRef> java = ResourceRef.forSource(RangeSliderMapper.class);
        if (java.isPresent()) {
            getAditionalSources().add(java.get());

        }
        final Optional<ResourceRef> js = ResourceRef.forClassWithExtension(RangeSliderMapper.class, "js");
        if (js.isPresent()) {
            getAditionalSources().add(js.get());
        }
    }

}
