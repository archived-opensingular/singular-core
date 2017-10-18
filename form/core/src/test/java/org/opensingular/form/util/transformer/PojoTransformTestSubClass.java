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

package org.opensingular.form.util.transformer;

public class PojoTransformTestSubClass {
	private String nome;
	private String cpf;
	
	private Integer valorQualquer;
	
	private PojoTransformTestSuperClass pai;
	
	public PojoTransformTestSubClass() {
	}
	
	public PojoTransformTestSubClass(String nome) {
		this.nome = nome;
	}

	public String getNome() {
		return nome;
	}

	public void setNome(String nome) {
		this.nome = nome;
	}

	public String getCpf() {
		return cpf;
	}

	public void setCpf(String cpf) {
		this.cpf = cpf;
	}

	public Integer getValorQualquer() {
		return valorQualquer;
	}

	public void setValorQualquer(Integer valorQualquer) {
		this.valorQualquer = valorQualquer;
	}

	public PojoTransformTestSuperClass getPai() {
		return pai;
	}

	public void setPai(PojoTransformTestSuperClass pai) {
		this.pai = pai;
	}
	
}
