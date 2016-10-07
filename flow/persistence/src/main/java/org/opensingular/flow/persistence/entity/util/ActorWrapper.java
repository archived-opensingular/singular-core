/*
 * Copyright (c) 2016, Singular and/or its affiliates. All rights reserved.
 * Singular PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package org.opensingular.flow.persistence.entity.util;

import org.opensingular.flow.core.MUser;
import org.opensingular.flow.persistence.entity.Actor;

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
