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

package org.opensingular.form.exemplos.notificacaosimplificada.form.vocabulario;

import org.opensingular.form.exemplos.notificacaosimplificada.service.DominioService;
import org.opensingular.form.PackageBuilder;
import org.opensingular.form.SInfoPackage;
import org.opensingular.form.SInstance;
import org.opensingular.form.SPackage;

@SInfoPackage(name = SPackageVocabularioControlado.PACOTE)
public class SPackageVocabularioControlado extends SPackage {

    public static final String PACOTE        = "mform.peticao.anvisa.dominio";
    public static final String TIPO          = "VocabularioControlado";
    public static final String NOME_COMPLETO = PACOTE + "." + TIPO;

    public static DominioService dominioService(SInstance ins) {
        return ins.getDocument().lookupService(DominioService.class);
    }

    public SPackageVocabularioControlado() {
        super(PACOTE);
    }


    @Override
    protected void onLoadPackage(PackageBuilder pb) {
        pb.createType(STypeEmbalagemPrimaria.class);
        pb.createType(STypeCategoriaRegulatoria.class);
        pb.createType(STypeEmbalagemSecundaria.class);
        pb.createType(STypeLinhaProducao.class);
        pb.createType(STypeFormaFarmaceutica.class);
        pb.createType(STypeUnidadeMedida.class);
        pb.createType(STypeFarmacopeia.class);
    }

}

