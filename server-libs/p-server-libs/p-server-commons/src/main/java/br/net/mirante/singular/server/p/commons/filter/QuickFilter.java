package br.net.mirante.singular.server.p.commons.filter;

public class QuickFilter {

    private String filter;
    private boolean rascunho;

    private String idPessoaRepresentada;
    private int first;
    private int count;
    private String sortProperty;
    private boolean ascending;
    private String[] tasks;

    public String getFilter() {
        return filter;
    }

    public QuickFilter withFilter(String filter) {
        this.filter = filter;
        return this;
    }

    public String getIdPessoaRepresentada(){
        return idPessoaRepresentada;
    }

    public QuickFilter withIdPessoaRepresentada(String idPessoaRepresentada){
        this.idPessoaRepresentada = idPessoaRepresentada;
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

    public QuickFilter forTasks(String... tasks){
        this.tasks = tasks;
        return this;
    }

    public String[] getTasks() {
        return tasks;
    }
}
