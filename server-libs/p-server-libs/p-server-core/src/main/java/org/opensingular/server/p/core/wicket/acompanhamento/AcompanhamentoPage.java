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

package org.opensingular.server.p.core.wicket.acompanhamento;

import org.opensingular.server.commons.wicket.view.template.Content;
import org.opensingular.server.core.wicket.template.ServerTemplate;
import org.wicketstuff.annotation.mount.MountPath;

import static org.opensingular.server.commons.util.Parameters.MENU_PARAM_NAME;
import static org.opensingular.server.commons.util.Parameters.PROCESS_GROUP_PARAM_NAME;

@MountPath("acompanhamento")
public class AcompanhamentoPage extends ServerTemplate {

    @Override
    protected Content getContent(String id) {
        return new AcompanhamentoContent(id,
                getPageParameters().get(PROCESS_GROUP_PARAM_NAME).toString(),
                getPageParameters().get(MENU_PARAM_NAME).toString()
        );
    }

}
