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
import org.opensingular.form.type.core.STypeInteger;
import org.opensingular.form.type.core.STypeLong;
import org.opensingular.form.type.core.STypeString;
import org.opensingular.lib.commons.util.Loggable;

@SInfoType(name = "Conta", spackage = SPackageCountryBrazil.class)
public class STypeAccount extends STypeComposite<SIComposite> implements Loggable {

	private STypeLong agencia;
	private STypeLong conta;
	
	@Override
	protected void onLoadType(TypeBuilder tb) {

		agencia = this.addField("agencia", STypeLong.class);
		agencia.asAtr().maxLength(12);
		agencia.asAtr().label("Agência").asAtrBootstrap().colPreference(2);

		conta = this.addField("conta", STypeLong.class);
		conta.asAtr().maxLength(12).label("Conta").asAtrBootstrap().colPreference(2);

	}
	
	public static void main(String[] args) {
		System.out.println(Integer.MAX_VALUE);
	}
}
