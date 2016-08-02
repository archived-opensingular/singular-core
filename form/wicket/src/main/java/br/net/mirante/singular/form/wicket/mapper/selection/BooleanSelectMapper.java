/*
 * Copyright (c) 2016, Mirante and/or its affiliates. All rights reserved.
 * Mirante PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package br.net.mirante.singular.form.wicket.mapper.selection;

import br.net.mirante.singular.form.SInstance;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.LoadableDetachableModel;

import java.io.Serializable;
import java.util.Arrays;
import java.util.List;

public class BooleanSelectMapper extends SelectMapper {

    @Override
    protected LoadableDetachableModel<List<Serializable>> getChoicesDetachableModel(IModel<? extends SInstance> model) {
        return new LoadableDetachableModel<List<Serializable>>() {
            @Override
            protected List<Serializable> load() {
                return Arrays.asList(new Boolean[]{Boolean.TRUE, Boolean.FALSE});
            }
        };
    }
}
