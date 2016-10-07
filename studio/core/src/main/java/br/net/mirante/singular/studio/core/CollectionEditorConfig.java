package br.net.mirante.singular.studio.core;

import org.apache.commons.lang3.tuple.Pair;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class CollectionEditorConfig implements Serializable{

    /*
     * Lista de par de label (caption da table) e nome do tipo
     * O nome vai no lugar do SType para permitir que essa classe seja serializável
     */
    private List<Pair<String, String>> columns = new ArrayList<>();
    private Integer defaultSortColumnIndex;
    private long rowsPerPage;

    CollectionEditorConfig() {
    }

    public List<Pair<String, String>> getColumns() {
        return columns;
    }

    public Integer getDefaultSortColumnIndex() {
        return defaultSortColumnIndex;
    }

    void setDefaultSortColumnIndex(Integer defaultSortColumnIndex) {
        this.defaultSortColumnIndex = defaultSortColumnIndex;
    }

    public long getRowsPerPage() {
        return rowsPerPage;
    }

    void setRowsPerPage(long rowsPerPage) {
        this.rowsPerPage = rowsPerPage;
    }
}
