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

import java.util.regex.Pattern;

import org.opensingular.form.SIComposite;
import org.opensingular.form.SInfoType;
import org.opensingular.form.STypeComposite;
import org.opensingular.form.TypeBuilder;
import org.opensingular.form.type.core.STypeInteger;
import org.opensingular.form.type.core.STypeString;
import org.opensingular.lib.commons.util.Loggable;

@SInfoType(name = "EnderecoCompleto", spackage = SPackageCountryBrazil.class)
public class STypeAddress extends STypeComposite<SIComposite> implements Loggable {

	private static final Pattern NOT_NUMBER_PATTERN = Pattern.compile("[^\\d]");
	private static final Pattern CEP_PATTERN = Pattern.compile("(\\d{2})(\\d{3})(\\d{3})");
	private STypeString endereco;
	private STypeString complemento;
	private STypeString cidade;
	private STypeCEP cep;
	private STypeInteger numero;
	private STypeComposite<SIComposite> estado;

	@Override
	protected void onLoadType(TypeBuilder tb) {
		// addInstanceValidator(ValidationErrorLevel.ERROR,
		// InstanceValidators.cep());
		
		this.asAtr().label("Endereço:");

		cep = this.addField("cep", STypeCEP.class);
		cep.asAtrBootstrap().colPreference(2);
		
		endereco = this.addFieldString("endereco");
		endereco.asAtr().label("Rua/Avenida").asAtrBootstrap().colPreference(12);

		complemento = this.addFieldString("complemento");
		complemento.asAtr().label("Endereço");

		numero = this.addFieldInteger("numero");
		numero.asAtr().label("Numero").asAtrBootstrap().colPreference(2);

		cidade = this.addFieldString("cidade");
		cidade.asAtr().label("Cidade").asAtrBootstrap().colPreference(3);

		estado = this.addFieldComposite("estado");
		estado.asAtr().label("Estado").asAtrBootstrap().colPreference(4);

		final STypeString sigla = estado.addFieldString("sigla");
		final STypeString nome = estado.addFieldString("nome");
 
        estado.selection()
                .id(sigla)
                .display("${nome} - ${sigla}")
                .simpleProvider(listaBuilder -> {
                	listaBuilder.add().set(nome, "Acre").set(sigla, "AC");	 
                	listaBuilder.add().set(nome, "Alagoas").set(sigla, "AL");	 
                	listaBuilder.add().set(nome, "Amapá").set(sigla, "AP");	 
                	listaBuilder.add().set(nome, "Amazonas").set(sigla, "AM");	 
                	listaBuilder.add().set(nome, "Bahia").set(sigla, "BA");	 
                	listaBuilder.add().set(nome, "Ceará").set(sigla, "CE");	 
                	listaBuilder.add().set(nome, "Distrito Federal").set(sigla, "DF");	 
                	listaBuilder.add().set(nome, "Espírito Santo").set(sigla, "ES");	 
                	listaBuilder.add().set(nome, "Goiás").set(sigla, "GO");	 
                	listaBuilder.add().set(nome, "Maranhão").set(sigla, "MA");	 
                	listaBuilder.add().set(nome, "Mato Grosso").set(sigla, "MT");	 
                	listaBuilder.add().set(nome, "Mato Grosso do Sul").set(sigla, "MS");	 
                	listaBuilder.add().set(nome, "Minas Gerais").set(sigla, "MG");	 
                	listaBuilder.add().set(nome, "Pará").set(sigla, "PA");	 
                	listaBuilder.add().set(nome, "Paraíba").set(sigla, "PB");	 
                	listaBuilder.add().set(nome, "Paraná").set(sigla, "PR");	 
                	listaBuilder.add().set(nome, "Pernambuco").set(sigla, "PE");	 
                	listaBuilder.add().set(nome, "Piauí").set(sigla, "PI");	 
                	listaBuilder.add().set(nome, "Rio de Janeiro").set(sigla, "RJ");	 
                	listaBuilder.add().set(nome, "Rio Grande do Norte").set(sigla, "RN");	 
                	listaBuilder.add().set(nome, "Rio Grande do Sul").set(sigla, "RS");	 
                	listaBuilder.add().set(nome, "Rondônia").set(sigla, "RO");	 
                	listaBuilder.add().set(nome, "Roraima").set(sigla, "RR");	 
                	listaBuilder.add().set(nome, "Santa Catarina").set(sigla, "SC");	 
                	listaBuilder.add().set(nome, "São Paulo").set(sigla, "SP");	 
                	listaBuilder.add().set(nome, "Sergipe").set(sigla, "SE");	 
                	listaBuilder.add().set(nome, "Tocantins").set(sigla, "TO");
                });
	}

	// @Override
	// public String convert(Object valor) {
	// try {
	// return format(super.convert(valor));
	// } catch (Exception e) {
	// getLogger().trace(e.getMessage(), e);
	// return String.valueOf(valor);
	// }
	// }

	// private Matcher getCepMatcher(String cep) {
	// String unformated;
	// if (cep == null) {
	// return null;
	// }
	// unformated = unformat(cep);
	// return CEP_PATTERN.matcher(unformated);
	// }

	// public String format(String cep) {
	// Matcher cepMatcher = getCepMatcher(cep);
	// if (cepMatcher != null && cepMatcher.matches()) {
	// return cepMatcher.group(1) + "." + cepMatcher.group(2) + "-" +
	// cepMatcher.group(3);
	// }
	// return cep;
	// }

	// String unformat(String formated) {
	// if (formated == null) {
	// return null;
	// }
	// return NOT_NUMBER_PATTERN.matcher(formated).replaceAll("");
	// }

}
