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

package org.opensingular.singular.form.showcase.component.form.core.search;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.opensingular.form.SInstance;
import org.opensingular.form.util.transformer.Value;

public class FuncionarioRepository {

    private static final List<Funcionario> FUNCIONARIOS;

    static {
        FUNCIONARIOS = new ArrayList<>();
        FUNCIONARIOS.add(new Funcionario("João Vitor Mota", "Analista de Sistemas", 30));
        FUNCIONARIOS.add(new Funcionario("Alexandre Guimarães", "Analista de Requisitos", 27));
        FUNCIONARIOS.add(new Funcionario("Romario De Paula", "Analista de Testes", 22));
        FUNCIONARIOS.add(new Funcionario("Alexandra Leite", "Gerente de Projetos", 40));
        FUNCIONARIOS.add(new Funcionario("Alexandra Paes", "Analista de Sistemas", 31));
        FUNCIONARIOS.add(new Funcionario("Sandy Souza", "Analista de Testes", 23));
        FUNCIONARIOS.add(new Funcionario("Julia Camara Guimarães", "Gerente de Projetos", 49));
        FUNCIONARIOS.add(new Funcionario("Fabia Rocha", "Analista de Sistemas", 25));
        FUNCIONARIOS.add(new Funcionario("Lucio Palmeira Machado", "Analista de Requisitos", 23));
        FUNCIONARIOS.add(new Funcionario("Carlos Chaves Cordeiro", "Analista de Suporte Técnico", 19));
        FUNCIONARIOS.add(new Funcionario("Arnaldo Palmeira", "Gerente de Projetos", 35));
        FUNCIONARIOS.add(new Funcionario("Andre Klein Oliveira", "Analista de Sistemas", 33));
        FUNCIONARIOS.add(new Funcionario("Cecilia Moreira Fonseca", "Analista de Suporte Técnico", 32));
        FUNCIONARIOS.add(new Funcionario("Joao Franco", "Gerente de Projetos", 41));
        FUNCIONARIOS.add(new Funcionario("Lucio Krause", "Analista de Sistemas", 40));
        FUNCIONARIOS.add(new Funcionario("Thales Lima Mello", "Analista de Testes", 27));
        FUNCIONARIOS.add(new Funcionario("Joao Vitor Da Silva Mattos", "Analista de Requisitos", 25));
    }

    public List<Funcionario> get(SInstance filter) {

        String  nome   = Value.of(filter, "nome");
        String  funcao = Value.of(filter, "funcao");
        Integer idade  = Value.of(filter, "idade");

        return FUNCIONARIOS.stream().filter((func) -> {
            boolean contains = true;
            if (nome != null) {
                contains = func.getNome().toUpperCase().contains(nome.toUpperCase());
            }
            if (funcao != null) {
                contains = func.getFuncao().toUpperCase().contains(funcao.toUpperCase());
            }
            if (idade != null) {
                contains = func.getIdade().equals(idade);
            }
            return contains;
        }).collect(Collectors.toList());
    }

}
