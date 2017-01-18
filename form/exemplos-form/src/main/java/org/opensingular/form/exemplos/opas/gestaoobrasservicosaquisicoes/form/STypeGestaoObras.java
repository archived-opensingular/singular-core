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
package org.opensingular.form.exemplos.opas.gestaoobrasservicosaquisicoes.form;

import org.opensingular.form.SIComposite;
import org.opensingular.form.SInfoType;
import org.opensingular.form.STypeComposite;
import org.opensingular.form.TypeBuilder;
import org.opensingular.form.view.SViewTab;

@SInfoType(spackage = SPackageGestaoObrasServicosAquisicoes.class)
public class STypeGestaoObras extends STypeComposite<SIComposite>{

    @Override
    protected void onLoadType(TypeBuilder tb) {
        this.asAtr().label("Gestão de Obras");
        
        
        SViewTab tabbed = this.setView(SViewTab::new);
        tabbed.addTab(addField("checklist", STypeChecklist.class), "Checklist");
        tabbed.addTab(addField("processo", STypeProcesso.class), "Processo");
        
        // configuração do tamanho da coluna de navegação das abas
        tabbed.navColPreference(1).navColMd(2).navColSm(2).navColXs(3);
        
    }
}
