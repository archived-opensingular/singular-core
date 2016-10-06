/*
 * Copyright (c) 2016, Mirante and/or its affiliates. All rights reserved.
 * Mirante PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package org.opensingular.singular.persistence.entity.util;

import org.opensingular.singular.flow.core.MUser;
import org.opensingular.singular.persistence.entity.Actor;

public class ActorWrapper {

    public static MUser wrap(final Actor actor) {
        return new MUser() {

            @Override
            public Integer getCod() {
                return actor.getCod();
            }

            @Override
            public String getSimpleName() {
                return null;
            }

            @Override
            public String getEmail() {
                return null;
            }

            @Override
            public String getCodUsuario() {
                return actor.getCodUsuario();
            }
        };
    }
}
