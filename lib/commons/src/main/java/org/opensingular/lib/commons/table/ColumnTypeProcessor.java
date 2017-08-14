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

import org.joda.time.LocalDate;
import org.joda.time.LocalDateTime;
import org.joda.time.base.AbstractInstant;
import org.opensingular.lib.commons.base.SingularException;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Date;
import java.util.Objects;

/**
 * Fornece as implementações de manituplação de um tipo específicos de coluna, bem como meta dados sobre esse tipo.
 *
 * @author Daniel C. Bordin on 22/04/2017.
 */
public interface ColumnTypeProcessor {

    /**
     * Verifica se a celula em questão possui algum valor para ser exibido de acordo com as definições do procesador.
     */
    public default boolean isNullContent(InfoCelula cell) {
        return cell == null || cell.getValue() == null;
    }

    public default boolean shouldBePrinted() {
        return true;
    }

    public default boolean shouldBeGeneretedOnStaticContent() {
        return true;
    }

    public default PrintResult generatePrintValue(@Nonnull Column column, @Nullable Object value) {
        PrintResult result = new PrintResult();
        generatePrintValue(result, column, value);
        return result;
    }

    void generatePrintValue(@Nonnull PrintResult result, @Nonnull Column column, @Nullable Object value);


    public static final ColumnTypeProcessor BOOLEAN = new ColumnTypeProcessorTypeBoolean();
    public static final ColumnTypeProcessor ACTION = new ColumnTypeProcessorTypeAction();
    public static final ColumnTypeProcessor DATE = new ColumnTypeProcessorTypeDateBased("short");
    public static final ColumnTypeProcessor DATE_HOUR = new ColumnTypeProcessorTypeDateBased("dd/MM/yy HH:mm:ss");
    public static final ColumnTypeProcessor DATE_HOUR_SHORT = new ColumnTypeProcessorTypeDateBased("dd/MM/yy HH:mm");
    public static final ColumnTypeProcessor DAY = new ColumnTypeProcessorTypeDateBased("dd");
    public static final ColumnTypeProcessor RAW = new ColumnTypeProcessorTypeRaw();
    public static final ColumnTypeProcessor STRING = new ColumnTypeProcessorTypeString();
    public static final ColumnTypeProcessor NUMBER = new ColumnTypeProcessorTypeNumber();
    public static final ColumnTypeProcessor INTEGER = new ColumnTypeProcessorTypeInteger();
    public static final ColumnTypeProcessor PERCENT = new ColumnTypeProcessorTypePercent();
    public static final ColumnTypeProcessor HOUR = new ColumnTypeProcessorTypeHour();

    public default Column.Alignment getDefaultAlignment() {
        return Column.Alignment.LEFT;
    }

    /**
     * Verify the order between the two values.
     *
     * @see java.util.Comparator#compare(Object, Object)
     */
    public default int compare(@Nonnull Object v1, @Nonnull Object v2) {
        if (v1 instanceof Comparable<?> && v1.getClass().isAssignableFrom(v2.getClass())) {
            return ((Comparable<Object>) v1).compareTo(v2);
        }
        throw new SingularException(
                "It's not possible to compare the object of the class " + v1.getClass().getName() + " and " +
                        v2.getClass().getName());

    }

    public static class PrintResult {

        private String content;
        private boolean defined;

        public void setContent(String content) {
            this.content = content;
            this.defined = true;
        }

        public String getContent() {
            return content;
        }

        public boolean isDefined() {
            return defined;
        }
    }


    static class ColumnTypeProcessorTypeAction implements ColumnTypeProcessor {

        @Override
        public boolean isNullContent(InfoCelula cell) {
            return cell == null || cell.isAcaoEmpty();
        }

        @Override
        public boolean shouldBePrinted() {
            return false;
        }

        @Override
        public boolean shouldBeGeneretedOnStaticContent() {
            return false;
        }

        @Override
        public void generatePrintValue(PrintResult result, Column column, Object value) {
            throw new SingularException("This method shouldn't be called");
        }

    }

    static class ColumnTypeProcessorTypeBoolean implements ColumnTypeProcessor {

        @Override
        public void generatePrintValue(PrintResult result, Column column, Object value) {
            if (value instanceof Boolean) {
                result.setContent((Boolean) value ? "Sim" : "Não");
            }
        }

        @Override
        public Column.Alignment getDefaultAlignment() {
            return Column.Alignment.CENTER;
        }

        public int compare(@Nonnull Object v1, @Nonnull Object v2) {
            if (v1 instanceof Boolean && v2 instanceof Boolean) {
                return Boolean.compare((Boolean) v1, (Boolean) v2);
            }
            throw new SingularException(
                    "It's not possible to compare the object of the class " + v1.getClass().getName() + " and " +
                            v2.getClass().getName());
        }
    }

    static class ColumnTypeProcessorTypeDateBased implements ColumnTypeProcessor {

        private final String dateFormat;

        public ColumnTypeProcessorTypeDateBased(String dateFormat) {this.dateFormat = dateFormat;}

        @Override
        public void generatePrintValue(PrintResult result, Column column, Object value) {
            Date date = asDate(value);
            if (date != null) {
                result.setContent(ConversorToolkit.printDate(date, dateFormat));
            }
        }

        private Date asDate(Object value) {
            if (value == null) {
                return null;
            } else if (value instanceof Date) {
                return (Date) value;
            }
            if (value instanceof LocalDate) {
                return ((LocalDate) value).toDate();
            } else if (value instanceof LocalDateTime) {
                return ((LocalDateTime) value).toDate();
            } else if (value instanceof AbstractInstant) {
                return ((AbstractInstant) value).toDate();
            }
            return null;
        }

        @Override
        public Column.Alignment getDefaultAlignment() {
            return Column.Alignment.CENTER;
        }

        public int compare(@Nonnull Object v1, @Nonnull Object v2) {
            Date d1 = asDate(v1);
            Date d2 = asDate(v2);
            if (d1 == d2) {
                return 0;
            } else if( d1 == null) {
                return -1;
            } else if (d2 == null) {
                return 1;
            }
            return d1.compareTo(d2);
        }
    }

    static class ColumnTypeProcessorTypeRaw implements ColumnTypeProcessor {

        @Override
        public void generatePrintValue(PrintResult result, Column column, Object value) {
            result.setContent(value == null ? null : value.toString());
        }

        @Override
        public int compare(@Nonnull Object v1, @Nonnull Object v2) {
            return Objects.toString(v1).compareToIgnoreCase(Objects.toString(v2));
        }
    }

    static class ColumnTypeProcessorTypeString implements ColumnTypeProcessor {

        @Override
        public void generatePrintValue(PrintResult result, Column column, Object value) {
            //Deixa o tratamento default, que inclui a introdução de escapes HTML
        }

        @Override
        public int compare(@Nonnull Object v1, @Nonnull Object v2) {
            return Objects.toString(v1).compareToIgnoreCase(Objects.toString(v2));
        }
    }

    static class ColumnTypeProcessorTypeNumber implements ColumnTypeProcessor {

        private final int defaultNumberOfDigits;

        ColumnTypeProcessorTypeNumber() {this(2);}

        ColumnTypeProcessorTypeNumber(int defaultNumberOfDigits) {
            this.defaultNumberOfDigits = defaultNumberOfDigits;
        }

        /**
         * Verifica se a celula em questão possui algum valor para ser exibido de acordo com as definições do
         * procesador.
         */
        @Override
        public boolean isNullContent(InfoCelula cell) {
            if (cell == null || cell.getValue() == null) {
                return true;
            } else if (cell.getValue() instanceof Number) {
                return !cell.getColumn().isShowZero() && AlocproToolkit.isZero((Number) cell.getValue());
            }
            return false;
        }

        @Override
        public final void generatePrintValue(PrintResult result, Column column, Object value) {
            if (value instanceof Number) {
                Number n = (Number) value;
                if (!column.isShowZero() && AlocproToolkit.isZero(n)) {
                    result.setContent(null);
                } else {
                    generatePrintValue(result, column, n);
                }
            }
        }

        protected void generatePrintValue(@Nonnull PrintResult result, @Nonnull Column column, @Nonnull Number value) {
            result.setContent(format(column, value));
        }

        protected String format(Column column, Number value) {
            return AlocproToolkit.printNumber(value, column.getFractionDigits(defaultNumberOfDigits));
        }

        @Override
        public Column.Alignment getDefaultAlignment() {
            return Column.Alignment.RIGHT;
        }

        @Override
        public int compare(@Nonnull Object v1, @Nonnull Object v2) {
            if (v1 instanceof Integer && v2 instanceof Integer) {
                return Integer.compare((Integer) v1, (Integer) v2);
            } else if (v1 instanceof Number && v2 instanceof Number) {
                return Double.compare(((Number) v1).doubleValue(), ((Number) v2).doubleValue());
            }
            throw new SingularException(
                    "It's not possible to compare the object of the class " + v1.getClass().getName() + " and " +
                            v2.getClass().getName());
        }
    }

    static class ColumnTypeProcessorTypeInteger extends ColumnTypeProcessorTypeNumber {
        public ColumnTypeProcessorTypeInteger() {
            super(0);
        }
    }

    static class ColumnTypeProcessorTypePercent extends ColumnTypeProcessorTypeNumber {
        public ColumnTypeProcessorTypePercent() {
            super(1);
        }

        @Override
        protected void generatePrintValue(@Nonnull PrintResult result, @Nonnull Column column, @Nonnull Number value) {
            Number n = AlocproToolkit.multiply(value, 100);
            result.setContent(format(column, n) + "%");
        }
    }

    static class ColumnTypeProcessorTypeHour extends ColumnTypeProcessorTypeNumber {
        @Override
        protected void generatePrintValue(@Nonnull PrintResult result, @Nonnull Column column, @Nonnull Number value) {
            result.setContent(AlocproToolkit.toHora(value, null));
        }
    }
}
