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

import org.opensingular.lib.commons.base.SingularException;

import javax.annotation.Nonnull;
import java.io.Serializable;

public class Column implements Serializable {

    public enum Alignment {
        LEFT, CENTER, RIGHT
    }

    private String id;

    private ColumnType type;

    private transient ColumnTypeProcessor processor;

    private int index;

    private String superTitle;

    private String title;

    private Alignment alignment;

    private String width;

    private boolean small;

    private boolean strong;

    private boolean visible = true;

    private Integer fractionDigits;

    private boolean showZero;

    private boolean showAsPercentageOfParent;

    private Number valueForPercentageCalculation;

    private boolean totalize = true;

    private Double total;

    private boolean hasSeparator;

    private int dataLevel = 0;

    private Decorator decoratorTitleAndValue = new Decorator();

    private Decorator decoratorTitle = decoratorTitleAndValue.newDerivedDecorator();

    private Decorator decoratorValues = decoratorTitleAndValue.newDerivedDecorator();

    public Column(@Nonnull ColumnType type) {
        setType(type);
    }

    public ColumnType getType() {
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

    public boolean isTotalize() {
        return totalize;
    }

    public Column setTotalize(boolean totalize) {
        this.totalize = totalize;
        return this;
    }

    public void addTotal(Number number) {
        Number n = number;
        if (number == null) {
            n = 0.0;
        }
        if (total == null) {
            total = n.doubleValue();
        } else {
            total += n.doubleValue();
        }
    }

    public void setTotal(Double total) {
        this.total = total;
    }

    public Double getTotal() {
        return total;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getTitle() {
        return title;
    }

    public boolean isVisible() {
        return visible;
    }

    public Column setAlignment(Alignment alignment) {
        this.alignment = alignment;
        return this;
    }

    public boolean hasSeparator() {
        return hasSeparator;
    }

    public Column setHasSeparator(boolean hasSeparator) {
        this.hasSeparator = hasSeparator;
        return this;
    }

    public Column setAlignmentLeft() {
        alignment = Alignment.LEFT;
        return this;
    }

    public Column setAlignmentCenter() {
        alignment = Alignment.CENTER;
        return this;
    }

    public Column setAlignmentRight() {
        alignment = Alignment.RIGHT;
        return this;
    }

    public Column setWidth(String w) {
        this.width = w;
        return this;
    }

    public String getWidth() {
        return width;
    }

    public Column setSmall(boolean v) {
        this.small = v;
        return this;
    }

    public boolean isSmall() {
        return small;
    }

    public Column setStrong(boolean strong) {
        this.strong = strong;
        return this;
    }

    public boolean isStrong() {
        return strong;
    }

    public Column setFractionDigits(Integer qtd) {
        fractionDigits = qtd;
        return this;
    }

    public Integer getFractionDigits() {
        return fractionDigits;
    }

    int getFractionDigits(int defaultNumberOfDigits) {
        return fractionDigits != null ? fractionDigits : defaultNumberOfDigits;
    }

    public Alignment getAlignment() {
        if (alignment != null) {
            return alignment;
        }
        return getProcessor().getDefaultAlignment();
    }

    public boolean isTypeAction() {
        return ColumnType.ACTION == type;
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
    public final boolean isShowAsPercentageOfParent() {
        return showAsPercentageOfParent;
    }

    /**
     * Indica se o valor a ser exibido � um percentual do valor raiz da coluna.
     */
    public final Column setShowAsPercentageOfParent(boolean showAsPercentageOfParent) {
        this.showAsPercentageOfParent = showAsPercentageOfParent;
        return this;
    }

    final Number getValueForPercentageCalculation() {
        return valueForPercentageCalculation;
    }

    final void setValueForPercentageCalculation(Number valueForPercentageCalculation) {
        this.valueForPercentageCalculation = valueForPercentageCalculation;
    }

    public void setType(@Nonnull ColumnType type) {
        this.type = type;
        this.processor = type.getProcessor();
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

    public void setDataLevel(int value) {
        dataLevel = value;
    }

    public final int getDataLevel() {
        return dataLevel;
    }

    public final int getIndex() {
        return index;
    }

    final void setIndex(int index) {
        this.index = index;
    }

    public int compare(InfoCell infoCell1, InfoCell infoCell2) {
        Object v1 = normalizeToNull(infoCell1);
        Object v2 = normalizeToNull(infoCell2);
        if (v1 == v2) {
            return 0;
        } else if (v1 == null) {
            return -1;
        } else if (v2 == null) {
            return 1;
        }
        return getProcessor().compare(v1, v2);
    }

    private Object normalizeToNull(InfoCell c) {
        return c == null ? null : c.getValueReal() != null ? c.getValueReal() : c.getValue();
    }
}
