package br.net.mirante.singular.form.view;

import br.net.mirante.singular.commons.lambda.IFunction;
import br.net.mirante.singular.form.SInstance;
import br.net.mirante.singular.form.SType;
import br.net.mirante.singular.form.internal.freemarker.FormFreemarkerUtil;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Representa listagens que possuem uma tabela de apenas leitura cujas as
 * colunas a serem exibidas podem ser configuradas.
 * 
 * @author Daniel C. Bordin
 */
public abstract class AbstractSViewListWithCustomColuns<SELF extends AbstractSViewList> extends AbstractSViewListWithControls<SELF> {

    private List<Column> columns = new ArrayList<>();

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
        return col(type, customLabel, instance -> FormFreemarkerUtil.merge(instance, freeMarkerTemplateString));
    }

    /**
     * Adiciona uma coluna cujo conteúdo será o subcampo informado, o titulo da
     * coluna específico e o conteudo de cada célula será cálculado
     * dinamicamente mediante a função informada.
     */
    public final SELF col(SType<?> type, String customLabel, IFunction<SInstance, String> displayFunction) {
        columns.add(new Column(type.getName(), customLabel, displayFunction));
        return (SELF) this;
    }

    /**
     * Adiciona uma coluna cujo o conteúdo será calculado dinamicamente para
     * cada instância de cada linha mediante o template do FreeMarker informado.
     * A função recebe a instância da linha inteira (o tipo da lista)
     * 
     * @param customLabel
     * @param freeMarkerTemplateString
     * @see FormFreemarkerUtil
     */
    public final SELF col(String customLabel, String freeMarkerTemplateString) {
        return col(customLabel, instance -> FormFreemarkerUtil.merge(instance, freeMarkerTemplateString));
    }

    /**
     * Adiciona uma coluna cujo o conteúdo será cálculado dinamicamente para
     * cada instância de cada linha. A função recebe a instância da linha
     * inteira (o tipo da lista).
     * 
     * @param customLabel
     *            Label da coluna
     * @param displayFunction
     *            Conversor da instância da linha (um composite) na string de
     *            conteúdo da celula
     */
    public final SELF col(String customLabel, IFunction<SInstance, String> displayFunction) {
        columns.add(new Column(null, customLabel, displayFunction));
        return (SELF) this;
        // TODO (by Daniel) Me implementa, por favor.
//        throw new RuntimeException("Método não implementando");
        // return (SELF) this;
    }

    public List<Column> getColumns() {
        return columns;
    }

    /** PARA USO INTERNO */
    public static class Column implements Serializable {

        private String typeName;
        private String customLabel;
        private IFunction<SInstance, String> displayValueFunction;

        public Column() {
        }

        public Column(String typeName, String customLabel, IFunction<SInstance, String> displayValueFunction) {
            this.typeName = typeName;
            this.customLabel = customLabel;
            this.displayValueFunction = displayValueFunction;
        }

        public String getTypeName() {
            return typeName;
        }

        public String getCustomLabel() {
            return customLabel;
        }

        public IFunction<SInstance, String> getDisplayValueFunction() {
            return displayValueFunction;
        }
    }
}
