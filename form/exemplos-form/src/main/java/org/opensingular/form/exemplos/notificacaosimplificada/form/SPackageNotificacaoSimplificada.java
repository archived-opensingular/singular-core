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

package org.opensingular.form.exemplos.notificacaosimplificada.form;

import org.opensingular.form.exemplos.notificacaosimplificada.form.gas.STypeAcondicionamentoGAS;
import org.opensingular.form.exemplos.notificacaosimplificada.form.vocabulario.SPackageVocabularioControlado;
import org.opensingular.form.exemplos.notificacaosimplificada.form.vegetal.STypeEnsaioControleQualidade;
import org.opensingular.form.exemplos.notificacaosimplificada.service.DominioService;
import org.opensingular.form.PackageBuilder;
import org.opensingular.form.SInfoPackage;
import org.opensingular.form.SInstance;
import org.opensingular.form.SPackage;

@SInfoPackage(name = SPackageNotificacaoSimplificada.PACOTE)
public class SPackageNotificacaoSimplificada extends SPackage {

    public static final String PACOTE        = "mform.peticao.notificacaosimplificada";
    public static final String TIPO          = "NotificacaoSimplificada";
    public static final String NOME_COMPLETO = PACOTE + "." + TIPO;

    static DominioService dominioService(SInstance ins) {
        return ins.getDocument().lookupService(DominioService.class);
    }

    public SPackageNotificacaoSimplificada() {
        super(PACOTE);
    }


    @Override
    protected void onLoadPackage(PackageBuilder pb) {
        pb.loadPackage(SPackageVocabularioControlado.class);
        pb.createType(STypeEmpresaPropria.class);
        pb.createType(STypeEmpresaInternacional.class);
        pb.createType(STypeEmpresaTerceirizada.class);
        pb.createType(STypeLocalFabricacao.class);
        pb.createType(STypeAcondicionamentoGAS.class);
        pb.createType(STypeAcondicionamento.class);
        pb.createType(STypeFarmacopeiaReferencia.class);
        pb.createType(STypeEnsaioControleQualidade.class);
    }

}

