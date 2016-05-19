/*
 * Copyright (c) 2016, Mirante and/or its affiliates. All rights reserved.
 * Mirante PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package br.net.mirante.singular.showcase.component.form.custom;

import java.util.Optional;

import br.net.mirante.singular.showcase.component.CaseBase;
import br.net.mirante.singular.showcase.component.ResourceRef;

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
