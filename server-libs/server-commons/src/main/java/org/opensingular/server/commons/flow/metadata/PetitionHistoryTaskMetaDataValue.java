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

public class PetitionHistoryTaskMetaDataValue {

    public static final PetitionHistoryTaskMetaDataKey KEY = new PetitionHistoryTaskMetaDataKey(PetitionHistoryTaskMetaDataKey.class.getName(), PetitionHistoryTaskMetaDataValue.class);
    public static final PetitionHistoryTaskMetaDataValue ON = new PetitionHistoryTaskMetaDataValue();

    PetitionHistoryTaskMetaDataValue() {
    }

    public static class PetitionHistoryTaskMetaDataKey extends MetaDataRef<PetitionHistoryTaskMetaDataValue> {
        private PetitionHistoryTaskMetaDataKey(String name, Class<PetitionHistoryTaskMetaDataValue> valueClass) {
            super(name, valueClass);
        }
    }

}
