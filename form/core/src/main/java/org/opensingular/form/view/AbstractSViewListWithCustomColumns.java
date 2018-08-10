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

package org.opensingular.form.view;

import org.opensingular.form.SInstance;
import org.opensingular.form.SType;
import org.opensingular.form.internal.freemarker.FormFreemarkerUtil;
import org.opensingular.lib.commons.lambda.IFunction;

import javax.annotation.Nullable;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Representa listagens que possuem uma tabela de apenas leitura cujas as
 * colunas a serem exibidas podem ser configuradas.
 *
 * @author Daniel C. Bordin
 */
public abstract class AbstractSViewListWithCustomColumns<SELF extends AbstractSViewListWithCustomColumns<SELF>> extends AbstractSViewListWithControls<SELF> {

    private final List<Column> columns = new ArrayList<>();

    /**
     * Adiciona uma coluna cujo conteúdo será o subcampo informado. O label da
     * coluna será o label do tipo informado. O conteúdo de cada célula será a
     * string gerada mediante {@link SInstance#toStringDisplay()}.
     */
    public final SELF col(SType<?> type) {
        return col(type, null, (IFunction<SInstance, String>) null);
    }

    /**
     * Adiciona uma coluna cujo conteúdo será o subcampo informado e com o
     * título da coluna informado. O conteúdo de cada célula será a string
     * gerada mediante {@link SInstance#toStringDisplay()}.
     */
    public final SELF col(SType<?> type, String customLabel) {
        return col(type, customLabel, (IFunction<SInstance, String>) null);
    }

    /**
     * Adiciona uma coluna cujo conteúdo será o subcampo informado e o conteudo
     * de cada célula será cálculado dinamicamente mediante a função informada.
     */
    public final SELF col(SType<?> type, IFunction<SInstance, String> displayFunction) {
        return col(type, null, displayFunction);
    }

    /**
     * Adiciona uma coluna cujo conteúdo será o subcampo informado, o titulo da
     * coluna específico e o conteudo de cada célula será cálculado
     * dinamicamente mediante o template do FreeMarker informado.
     *
     * @see FormFreemarkerUtil
     */
    public final SELF col(SType<?> type, String customLabel, String freeMarkerTemplateString) {
        return col(type, customLabel, instance -> FormFreemarkerUtil.get().merge(instance, freeMarkerTemplateString, false, true));
    }

    /**
     * Adiciona uma coluna cujo conteúdo será o subcampo informado, o titulo da
     * coluna específico e o conteudo de cada célula será cálculado
     * dinamicamente mediante a função informada.
     */
    @SuppressWarnings("unchecked")
    public final SELF col(SType<?> type, String customLabel, IFunction<SInstance, String> displayFunction) {
        return col(type, customLabel, displayFunction, true);
    }

    /**
     * Add a column with all configuration.
     *
     * @param type            The type of the column.
     * @param customLabel     A custom Label for the column.
     *                        Null for use the default of the type.
     * @param displayFunction A rule for the display.
     *                        Null if don't have a rule for diplay.
     * @param order           True for enable the order for the column, false for not.
     * @return <code>This</code>
     */
    @SuppressWarnings("unchecked")
    public final SELF col(SType<?> type, @Nullable String customLabel, @Nullable IFunction<SInstance, String> displayFunction, boolean order) {
        String nameSortableProperty = order ? type.getNameSimple() : null;
        columns.add(new Column(type.getName(), customLabel, nameSortableProperty, displayFunction));
        return (SELF) this;
    }

    /**
     * Adiciona uma coluna cujo o conteúdo será calculado dinamicamente para
     * cada instância de cada linha mediante o template do FreeMarker informado.
     * A função recebe a instância da linha inteira (o tipo da lista)
     *
     * @see FormFreemarkerUtil
     */
    public final SELF col(String customLabel, String freeMarkerTemplateString) {
        return col(customLabel, instance -> FormFreemarkerUtil.get().merge(instance, freeMarkerTemplateString, false, true));
    }

    /**
     * Adiciona uma coluna cujo o conteúdo será cálculado dinamicamente para
     * cada instância de cada linha. A função recebe a instância da linha
     * inteira (o tipo da lista).
     *
     * @param customLabel     Label da coluna
     * @param displayFunction Conversor da instância da linha (um composite) na string de
     *                        conteúdo da celula
     */
    @SuppressWarnings("unchecked")
    public final SELF col(String customLabel, IFunction<SInstance, String> displayFunction) {
        columns.add(new Column(null, customLabel, null, displayFunction));
        return (SELF) this;
    }

    public List<Column> getColumns() {
        return columns;
    }

    /**
     * PARA USO INTERNO
     */
    public static class Column implements Serializable {

        private String typeName;
        private String customLabel;
        private String columnSortName;
        private IFunction<SInstance, String> displayValueFunction;

        public Column() {
        }

        public Column(String typeName, String customLabel, String columnSortName, IFunction<SInstance, String> displayValueFunction) {
            this.typeName = typeName;
            this.customLabel = customLabel;
            this.columnSortName = columnSortName;
            this.displayValueFunction = displayValueFunction;
        }

        public String getTypeName() {
            return typeName;
        }

        public String getCustomLabel() {
            return customLabel;
        }

        public String getColumnSortName() {
            return columnSortName;
        }

        public IFunction<SInstance, String> getDisplayValueFunction() {
            return displayValueFunction;
        }
    }
}
