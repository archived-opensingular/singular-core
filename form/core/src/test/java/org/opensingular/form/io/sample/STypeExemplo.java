/*
 *
 *  * Copyright (C) 2016 Singular Studios (a.k.a Atom Tecnologia) - www.opensingular.com
 *  *
 *  * Licensed under the Apache License, Version 2.0 (the "License");
 *  *  you may not use this file except in compliance with the License.
 *  * You may obtain a copy of the License at
 *  *
 *  * http://www.apache.org/licenses/LICENSE-2.0
 *  *
 *  * Unless required by applicable law or agreed to in writing, software
 *  * distributed under the License is distributed on an "AS IS" BASIS,
 *  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  * See the License for the specific language governing permissions and
 *  * limitations under the License.
 *
 */

package org.opensingular.form.io.sample;

import javax.annotation.Nonnull;

import org.opensingular.form.SIComposite;
import org.opensingular.form.SInfoType;
import org.opensingular.form.STypeAttachmentList;
import org.opensingular.form.STypeComposite;
import org.opensingular.form.STypeList;
import org.opensingular.form.TypeBuilder;
import org.opensingular.form.type.core.SIString;
import org.opensingular.form.type.core.STypeString;
import org.opensingular.form.type.country.brazil.STypeAddress;
import org.opensingular.form.type.country.brazil.STypeBankAccount;
import org.opensingular.form.type.country.brazil.STypeCEP;
import org.opensingular.form.type.country.brazil.STypeUF;

@SInfoType(spackage = SPackageExemplo.class)
public class STypeExemplo extends STypeComposite<SIComposite> {

	public STypeAddress endereco;
//    public STypeList<STypeBankAccount, SIComposite> teste;
	public STypeAttachmentList fotos;
    public STypeDadosPessoais dadosPessoais;

    @Override
    protected void onLoadType(@Nonnull TypeBuilder tb) {
        endereco = this.addField("endereco", STypeAddress.class);
        dadosPessoais = this.addField("dadosPessoais", STypeDadosPessoais.class);
        
//        teste = this.addFieldListOf("teste", STypeBankAccount.class);
        fotos = this.addFieldListOfAttachment("fotos", "foto");
        fotos
                .withMaximumSizeOf(6)
                .asAtr()
                .label("Fotos do ponto de atracação")
                .asAtrBootstrap()
                .colPreference(12)
                .asAtrAnnotation()
                .setAnnotated();
    }
}
