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

package org.opensingular.server.commons.persistence.filter;

import java.io.Serializable;
import java.util.Arrays;
import java.util.List;

public class QuickFilter implements Serializable {

    private String       filter;
    private boolean      rascunho;
    private String       idPessoa;
    private String       idUsuarioLogado;
    private int          first;
    private int          count;
    private String       sortProperty;
    private boolean      ascending;
    private Boolean      endedTasks;
    private List<String> tasks;
    private List<String> processesAbbreviation;
    private List<String> typesNames;

    public String getFilter() {
        return filter;
    }

    public QuickFilter withFilter(String filter) {
        this.filter = filter;
        return this;
    }

    public String getIdUsuarioLogado() {
        return idUsuarioLogado;
    }

    public QuickFilter withIdUsuarioLogado(String idUsuarioLogado) {
        this.idUsuarioLogado = idUsuarioLogado;
        return this;
    }

    public String getIdPessoa() {
        return idPessoa;
    }

    public QuickFilter withIdPessoa(String idPessoa) {
        this.idPessoa = idPessoa;
        return this;
    }

    public boolean isRascunho() {
        return rascunho;
    }

    public QuickFilter withRascunho(boolean rascunho) {
        this.rascunho = rascunho;
        return this;
    }

    public int getFirst() {
        return first;
    }

    public QuickFilter withFirst(int first) {
        this.first = first;
        return this;
    }

    public int getCount() {
        return count;
    }

    public QuickFilter withCount(int count) {
        this.count = count;
        return this;
    }

    public String getSortProperty() {
        return sortProperty;
    }

    public QuickFilter withSortProperty(String sortProperty) {
        this.sortProperty = sortProperty;
        return this;
    }

    public boolean isAscending() {
        return ascending;
    }

    public QuickFilter withAscending(boolean ascending) {
        this.ascending = ascending;
        return this;
    }

    public QuickFilter sortAscending() {
        this.ascending = true;
        return this;
    }

    public QuickFilter sortDescending() {
        this.ascending = false;
        return this;
    }

    public boolean hasFilter() {
        return filter != null
                && !filter.isEmpty();
    }

    public QuickFilter forTasks(String... tasks) {
        this.tasks = Arrays.asList(tasks);
        return this;
    }

    public List<String> getTasks() {
        return tasks;
    }

    public List<String> getProcessesAbbreviation() {
        return processesAbbreviation;
    }

    public QuickFilter withProcessesAbbreviation(List<String> processesAbbreviation) {
        this.processesAbbreviation = processesAbbreviation;
        return this;
    }

    public List<String> getTypesNames() {
        return typesNames;
    }

    public QuickFilter withTypesNames(List<String> typesNames) {
        this.typesNames = typesNames;
        return this;
    }

    public Boolean getEndedTasks() {
        return endedTasks;
    }

    public QuickFilter withEndedTasks(Boolean endedTasks) {
        this.endedTasks = endedTasks;
        return this;
    }
}
