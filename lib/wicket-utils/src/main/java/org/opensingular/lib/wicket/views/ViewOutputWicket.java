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

package org.opensingular.lib.wicket.views;

import org.apache.wicket.Component;
import org.opensingular.lib.commons.base.SingularException;
import org.opensingular.lib.commons.views.ViewOutput;
import org.opensingular.lib.commons.views.ViewOutputFormat;

/**
 * @author Daniel C. Bordin on 24/07/2017.
 */
public class ViewOutputWicket implements ViewOutput<Component> {

    public static final ViewOutputFormat WICKET = new ViewOutputFormat("WICKET", "Wicket");

    @Override
    public Component getOutput() {
        throw new SingularException("Método não suportado");
    }

    @Override
    public ViewOutputFormat getFormat() {
        return WICKET;
    }
}
