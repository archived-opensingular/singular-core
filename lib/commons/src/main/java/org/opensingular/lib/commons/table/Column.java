/*
 * Copyright (C) 2016 Singular Studios (a.k.a Atom Tecnologia) - www.opensingular.com
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
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

package org.opensingular.lib.commons.table;

import com.google.common.base.MoreObjects;
import org.opensingular.lib.commons.base.SingularException;

import javax.annotation.Nonnull;
import java.io.Serializable;
import java.util.Objects;

public class Column implements Serializable {

    public enum Alignment {
        Left, Center, Right
    }

    private String id;

    private ColumnType type;

    private ColumnTypeProcessor processor;

    private int index;

    private String superTitle;

    private String titulo_;

    private Alignment alinhamento_;

    private String width_;

    private boolean small_;

    private boolean strong_;

    private boolean visible = true;

    private Integer qtdDigitos_;

    private boolean showZero;

    private boolean calcularPercentualPai_;

    private Number valorReferenciaPercentual_;

    private boolean totalizar = true;

    private Double total;

    private boolean possuiSeparador;

    private int nivelDados = 0;

    private Decorator decoratorTitleAndValue = new Decorator();

    private Decorator decoratorTitle = decoratorTitleAndValue.newDerivedDecorator();

    private Decorator decoratorValues = decoratorTitleAndValue.newDerivedDecorator();

    public Column(ColumnType tipo) {
        setTipo(tipo);
    }

    public ColumnType getTipo() {
        return type;
    }

    @Nonnull
    public Decorator getDecoratorTitleAndValue() {
        return decoratorTitleAndValue;
    }

    @Nonnull
    public Decorator getDecoratorTitle() {
        return decoratorTitle;
    }

    @Nonnull
    public Decorator getDecoratorValues() {
        return decoratorValues;
    }

    public boolean isTotalizar() {
        return totalizar;
    }

    public Column setTotalizar(boolean totalizar) {
        this.totalizar = totalizar;
        return this;
    }

    public void addTotal(Number number) {
        if (number == null) {
            number = 0.0;
        }
        if (total == null) {
            total = number.doubleValue();
        } else {
            total += number.doubleValue();
        }
    }

    public void setTotal(Double total) {
        this.total = total;
    }

    public Double getTotal() {
        return total;
    }

    public void setTitle(String titulo) {
        titulo_ = titulo;
    }

    public String getTitle() {
        return titulo_;
    }

    public boolean isVisivel() {
        return visible;
    }

    public Column setAlinhamento(Alignment alignment) {
        alinhamento_ = alignment;
        return this;
    }

    public boolean isPossuiSeparador() {
        return possuiSeparador;
    }

    public Column setPossuiSeparador(boolean possuiSeparador) {
        this.possuiSeparador = possuiSeparador;
        return this;
    }

    public Column setAlignmentLeft() {
        alinhamento_ = Alignment.Left;
        return this;
    }

    public Column setAlignmentCenter() {
        alinhamento_ = Alignment.Center;
        return this;
    }

    public Column setAlignmentRight() {
        alinhamento_ = Alignment.Right;
        return this;
    }

    public Column setWidth(String w) {
        width_ = w;
        return this;
    }

    public String getWidth() {
        return width_;
    }

    public Column setSmall(boolean v) {
        small_ = v;
        return this;
    }

    public boolean isSmall() {
        return small_;
    }

    public Column setStrong(boolean strong) {
        strong_ = strong;
        return this;
    }

    public boolean isStrong() {
        return strong_;
    }

    public Column setQtdDigitos(Integer qtd) {
        qtdDigitos_ = qtd;
        return this;
    }

    public Integer getQtdDigitos() {
        return qtdDigitos_;
    }

    int getQtdDigitos(int defaultNumberOfDigits) {
        if (qtdDigitos_ != null) {
            return qtdDigitos_;
        }
        return defaultNumberOfDigits;
    }

    public Alignment getAlignment() {
        if (alinhamento_ != null) {
            return alinhamento_;
        }
        switch (type) {
            case Date:
            case Day:
            case Periodo:
            case DateHourShort:
            case Boolean:
            case DateHour:
                return Alignment.Center;
            case Hour:
            case Integer:
            case Number:
            case Money:
            case Percent:
                return Alignment.Right;
            default:
                return Alignment.Left;
        }
    }

    public boolean isTipoAcao() {
        return ColumnType.Action == type;
    }

    public void setSuperTitle(String superTitle) {
        this.superTitle = superTitle;
    }

    public String getSuperTitle() {
        return superTitle;
    }

    public Column setVisible(boolean v) {
        visible = v;
        return this;
    }

    public String getId() {
        return id != null ? id : "c" + getIndex();
    }

    public Column setId(String id) {
        this.id = id;
        return this;
    }

    /**
     * Indica se o valor a ser exibido � um percentual do valor raiz da coluna.
     */
    public final boolean isCalcularPercentualPai() {
        return calcularPercentualPai_;
    }

    /**
     * Indica se o valor a ser exibido � um percentual do valor raiz da coluna.
     */
    public final Column setCalcularPercentualPai(boolean calcularPercentualPai) {
        calcularPercentualPai_ = calcularPercentualPai;
        return this;
    }

    final Number getValorReferenciaPercentual() {
        return valorReferenciaPercentual_;
    }

    final void setValorReferenciaPercentual(Number valorReferenciaPercentual) {
        valorReferenciaPercentual_ = valorReferenciaPercentual;
    }

    public void setTipo(ColumnType tipo_) {
        this.type = tipo_;
        this.processor = tipo_.getProcessor();
    }

    @Nonnull
    public ColumnTypeProcessor getProcessor() {
        if (processor == null) {
            throw new SingularException("Processador da coluna está null");
        }
        return processor;
    }

    public Column setShowZero() {
        showZero = true;
        return this;
    }

    public boolean isShowZero() {
        return showZero;
    }

    public void setNivelDados(int valor) {
        nivelDados = valor;
    }

    public final int getNivelDados() {
        return nivelDados;
    }

    public final int getIndex() {
        return index;
    }

    final void setIndex(int index) {
        this.index = index;
    }

    public int compare(InfoCelula c1, InfoCelula c2) {
        if (c1 != null && c1.getValue() == null) {
            c1 = null;
        }
        if (c2 != null && c2.getValue() == null) {
            c2 = null;
        }
        if (c1 == c2) {
            return 0;
        } else if (c1 == null) {
            return -1;
        } else if (c2 == null) {
            return 1;
        }
        switch (type) {
            case String:
            case Html:
                if (c1.getValorReal() == null || c2.getValorReal() == null) {
                    return Objects.toString(c1.getValue()).compareToIgnoreCase(Objects.toString(c2.getValue()));
                }
            case Money:
            case Number:
            case Integer:
            case Percent:
            case Hour:
                Object valorReal1 = MoreObjects.<Object>firstNonNull(c1.getValorReal(), c1.getValue());
                Object valorReal2 = MoreObjects.<Object>firstNonNull(c2.getValorReal(), c2.getValue());
                if (valorReal1 instanceof Number && valorReal2 instanceof Number) {
                    if (valorReal1 instanceof Integer && valorReal2 instanceof Integer) {
                        return ((Integer) valorReal1).intValue() - ((Integer) valorReal2).intValue();
                    }
                    double db1 = ((Number) valorReal1).doubleValue();
                    double db2 = ((Number) valorReal2).doubleValue();
                    return db1 < db2 ? -1 : db2 < db1 ? 1 : 0;
                }
            default:
                if (c1.getValue() instanceof Comparable<?> && c1.getValue().getClass().isAssignableFrom(
                        c2.getValue().getClass())) {
                    return ((Comparable<?>) c1.getValue()).compareTo(c2.getValue());
                }
                throw new RuntimeException("Comparador para coluna do tipo " + type + " n�o implementado");
        }
    }
}
