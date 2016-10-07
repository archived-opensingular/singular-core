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

package org.opensingular.form.wicket;

import org.opensingular.form.SType;
import org.opensingular.form.SingularFormException;
import org.opensingular.form.context.SingularFormConfigImpl;

import java.util.Map;


public class SingularFormConfigWicketImpl extends SingularFormConfigImpl implements SingularFormConfigWicket {

    private UIBuilderWicket buildContext = new UIBuilderWicket();

    public void setCustomMappers(Map<Class<? extends SType>, Class<IWicketComponentMapper>> customMappers) {
        if (customMappers != null) {
            for (Map.Entry<Class<? extends SType>, Class<IWicketComponentMapper>> entry : customMappers.entrySet()) {
                Class<IWicketComponentMapper> c = entry.getValue();
                buildContext.getViewMapperRegistry().register(entry.getKey(), () -> {
                    try {
                        return c.newInstance();
                    } catch (Exception e) {
                        throw new SingularFormException("Não é possível instanciar o mapper: " + entry.getValue(), e);
                    }
                });
            }
        }
    }

    @Override
    public SingularFormContextWicket createContext() {
        return new SingularFormContextWicketImpl(this);
    }
}
