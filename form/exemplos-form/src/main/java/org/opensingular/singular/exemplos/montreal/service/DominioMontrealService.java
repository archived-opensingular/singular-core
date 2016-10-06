/*
 * Copyright (c) 2016, Mirante and/or its affiliates. All rights reserved.
 * Mirante PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package org.opensingular.singular.exemplos.montreal.service;

import java.util.Arrays;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import org.opensingular.singular.exemplos.montreal.domain.Titulo;

@Service
@Transactional(readOnly = true)
public class DominioMontrealService {

    public List<Titulo> titulos() {
        return Arrays.asList(
                new Titulo(1L, "167.981"),
                new Titulo(2L, "167.982"),
                new Titulo(3L, "167.983"),
                new Titulo(4L, "167.984"),
                new Titulo(5L, "167.985")
        );
    }

    public Titulo buscarTitulo() {
        return new Titulo();
    }
}
