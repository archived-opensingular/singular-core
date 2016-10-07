/*
 * Copyright (C) 2016 Singular Studios (a.k.a Atom Tecnologia) - www.opensingular.com
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.opensingular.server.commons.flow.metadata;

import org.opensingular.flow.core.property.MetaDataRef;
import org.opensingular.server.commons.config.IServerContext;

import java.util.ArrayList;
import java.util.List;

public class PetServerContextMetaDataValue {

    public static final PetServerMetaDataKey KEY = new PetServerMetaDataKey(PetServerMetaDataKey.class.getName(), PetServerContextMetaDataValue.class);

    private List<IServerContext> contexts = new ArrayList<>(2);

    PetServerContextMetaDataValue() {

    }

    public PetServerContextMetaDataValue enableOn(IServerContext context) {
        contexts.add(context);
        return this;
    }


    public boolean isEnabledOn(IServerContext context) {
        return contexts.contains(context);
    }


    public static class PetServerMetaDataKey extends MetaDataRef<PetServerContextMetaDataValue> {


        private PetServerMetaDataKey(String name, Class<PetServerContextMetaDataValue> valueClass) {
            super(name, valueClass);
        }
    }


}
