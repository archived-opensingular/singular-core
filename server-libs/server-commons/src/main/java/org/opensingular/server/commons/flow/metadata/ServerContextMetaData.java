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

public class ServerContextMetaData extends MetaDataRef<PetServerContextMetaDataValue> {

    public static final ServerContextMetaData KEY = new ServerContextMetaData(ServerContextMetaData.class.getName(), PetServerContextMetaDataValue.class);

    private ServerContextMetaData(String name, Class<PetServerContextMetaDataValue> valueClass) {
        super(name, valueClass);
    }

    public static PetServerContextMetaDataValue enable(){
        return new PetServerContextMetaDataValue();
    }

}
