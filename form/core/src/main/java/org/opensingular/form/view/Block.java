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

package org.opensingular.form.view;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.opensingular.form.SIComposite;
import org.opensingular.form.SInstance;

/**
 * Created by Daniel on 08/06/2016.
 */
public class Block implements Serializable {

    private String       name;
    private List<String> types = new ArrayList<>();

    public Block() {}

    public Block(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<String> getTypes() {
        return types;
    }

    public void setTypes(List<String> types) {
        this.types = types;
    }

    public Optional<SInstance> getSingleType(SInstance baseInstance) {
        if ((baseInstance instanceof SIComposite) && isSingleType()) {
            return Optional.of(((SIComposite) baseInstance).getField(this.getTypes().get(0)));
        }
        return Optional.empty();
    }

    public boolean isSingleType() {
        return this.getTypes().size() == 1;
    }
}
