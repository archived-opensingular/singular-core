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

package org.opensingular.form.type.country.brazil;

import org.opensingular.form.SIComposite;
import org.opensingular.form.SInfoType;
import org.opensingular.form.STypeComposite;
import org.opensingular.form.TypeBuilder;
import org.opensingular.form.type.core.STypeString;
import org.opensingular.lib.commons.util.Loggable;

@SInfoType(name = "EnderecoCompleto", spackage = SPackageCountryBrazil.class)
public class STypeAddress extends STypeComposite<SIComposite> implements Loggable {

    public STypeString  logradouro;
    public STypeString  complemento;
    public STypeString  cidade;
    public STypeCEP     cep;
    public STypeString  numero;
    public STypeUF      estado;
    public STypeString  bairro;

    @Override
    protected void onLoadType(TypeBuilder tb) {

        cep = this.addField("cep", STypeCEP.class);
        cep.asAtrBootstrap().colPreference(2);
        cep.asAtrIndex().indexed(Boolean.TRUE);

        logradouro = this.addFieldString("logradouro");
        logradouro.asAtr().label("Logradouro").asAtrBootstrap().colPreference(8);
        logradouro.asAtrIndex().indexed(Boolean.TRUE);

        numero = this.addFieldString("numero");
        numero
                .asAtr()
                .maxLength(20)
                .label("Número")
                .asAtrBootstrap()
                .colPreference(2);
        numero.asAtrIndex().indexed(Boolean.TRUE);

        complemento = this.addFieldString("complemento");
        complemento.asAtr().label("Complemento").asAtrBootstrap().colPreference(6);

        bairro = this.addFieldString("bairro");
        bairro.asAtr().label("Bairro").asAtrBootstrap().colPreference(6);

        cidade = this.addFieldString("cidade");
        cidade.asAtr().label("Cidade").asAtrBootstrap().colPreference(6);

        estado = this.addField("estado", STypeUF.class);
        estado.asAtrIndex().indexed(Boolean.TRUE);

    }
}
