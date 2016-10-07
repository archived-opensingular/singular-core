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

import org.opensingular.form.exemplos.notificacaosimplificada.service.DominioService;
import org.opensingular.form.PackageBuilder;
import org.opensingular.form.SInstance;
import org.opensingular.form.SPackage;

public class SPackageGestaoObrasServicosAquisicoes extends SPackage {

    public static final String PACOTE = "mform.peticao.opas.gestaoobrasservicosaquisicoes";

    public static DominioService dominioService(SInstance ins) {
        return ins.getDocument().lookupService(DominioService.class);
    }

    public SPackageGestaoObrasServicosAquisicoes() {
        super(PACOTE);
    }

    @Override
    protected void onLoadPackage(PackageBuilder pb) {
        super.onLoadPackage(pb);
//        pb.createType(STypeEstado.class);
//        pb.createType(STypeMunicipio.class);
//
//        pb.createType(STypeCurso.class);
//        pb.createType(STypePDI.class);
//        pb.createType(STypePDIProjetoPedagogico.class);
//        pb.createType(STypePDIDocumentos.class);
//        pb.createType(STypeMantenedora.class);
        pb.createType(STypeValorEmpenhadoObra.class);
        pb.createType(STypeObra.class);
        pb.createType(STypeAldeia.class);
        pb.createType(STypeChecklist.class);
        pb.createType(STypeProcesso.class);
        pb.createType(STypeGestaoObras.class);
    }
}
