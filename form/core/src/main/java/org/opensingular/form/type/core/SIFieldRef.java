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

package org.opensingular.form.type.core;

import static org.opensingular.form.type.core.STypeFieldRef.*;

import java.io.Serializable;
import java.util.Optional;

import org.opensingular.form.SIComposite;
import org.opensingular.form.SInstance;
import org.opensingular.form.converter.SInstanceConverter;
import org.opensingular.lib.commons.base.SingularUtil;

public class SIFieldRef<SOURCE extends SInstance> extends SIComposite {

    public static class Option implements Serializable {
        public final Integer refId;
        public final String  description;
        public Option(Integer refId, String description) {
            this.refId = refId;
            this.description = description;
        }
        public Integer getRefId() {
            return refId;
        }
        public String getDescription() {
            return description;
        }
        @Override
        public int hashCode() {
            return refId;
        }
        @Override
        public boolean equals(Object obj) {
            return SingularUtil.areEqual(this, obj,
                it -> it.refId,
                it -> it.description);
        }
    }

    public static final SInstanceConverter<Option, SIFieldRef<?>> DEFAULT_CONVERTER = new SInstanceConverter<SIFieldRef.Option, SIFieldRef<?>>() {
        @Override
        public Option toObject(SIFieldRef<?> ins) {
            return ins.toOption();
        }
        @Override
        public void fillInstance(SIFieldRef<?> ins, Option obj) {
            ins.setOption(obj);
        }
    };

    //@formatter:off
    public Integer getRefId()       { return getValueInteger(FIELD_REF_ID     ); }
    public String  getDescription() { return getValueString (FIELD_DESCRIPTION); }
    public SIFieldRef<SOURCE> setRefId      (Integer      refId) { setValue(FIELD_REF_ID,            refId); return this; }
    public SIFieldRef<SOURCE> setDescription(String description) { setValue(FIELD_DESCRIPTION, description); return this; }
    //@formatter:on

    public Option toOption() {
        return new Option(getRefId(), getDescription());
    }
    public SIFieldRef<SOURCE> setOption(Option option) {
        return this
            .setRefId(option.refId)
            .setDescription(option.description);
    }

    @SuppressWarnings("unchecked")
    public Optional<SOURCE> findSourceInstance() {
        return Optional.ofNullable(getRefId())
            .flatMap(it -> getDocument().findInstanceById(it))
            .map(it -> (SOURCE) it);
    }
}
