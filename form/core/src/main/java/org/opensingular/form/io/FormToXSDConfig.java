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

package org.opensingular.form.io;

import org.opensingular.form.SType;

import javax.annotation.Nonnull;

/**
 * Configures details in the generation of a XSD from {@link org.opensingular.form.SType}.
 *
 * @author Daniel C. Bordin on 19/09/2017.
 * @see FormXSDUtil#toXsd(SType)
 */
public class FormToXSDConfig {

    private boolean generateCustomAttribute;

    private FormToXSDConfig() {}

    /** Create a XSD customized for human reading with extra information. */
    @Nonnull
    public static FormToXSDConfig newForUserDisplay() {
        FormToXSDConfig config = new FormToXSDConfig();
        config.setGenerateCustomAttribute(true);
        return config;
    }

    /** Create a XSD customized for generating a Web Service definition. */
    @Nonnull
    public static FormToXSDConfig newForWebServiceDefinition() {
        FormToXSDConfig config = new FormToXSDConfig();
        config.setGenerateCustomAttribute(false);
        return config;
    }

    public void setGenerateCustomAttribute(boolean generateCustomAttribute) {
        this.generateCustomAttribute = generateCustomAttribute;
    }

    public boolean isGenerateCustomAttribute() {
        return generateCustomAttribute;
    }
}
