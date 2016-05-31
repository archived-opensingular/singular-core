package br.net.mirante.singular.studio.core;

import br.net.mirante.singular.form.SPackage;
import br.net.mirante.singular.form.STypeSimple;
import org.apache.commons.lang3.tuple.Pair;

import java.util.ArrayList;
import java.util.List;

public class CollectionEditorConfig {

    private List<Pair<String, STypeSimple<?, ?>>> columns = new ArrayList<>();
    private Integer defaultSortColumnIndex;

    CollectionEditorConfig() {
    }

    void setColumns(List<Pair<String, STypeSimple<?, ?>>> columns) {
        this.columns = columns;
    }

    void setDefaultSortColumnIndex(Integer defaultSortColumnIndex) {
        this.defaultSortColumnIndex = defaultSortColumnIndex;
    }


    public List<Pair<String, STypeSimple<?, ?>>> getColumns() {
        return columns;
    }

    public Integer getDefaultSortColumnIndex() {
        return defaultSortColumnIndex;
    }

}
