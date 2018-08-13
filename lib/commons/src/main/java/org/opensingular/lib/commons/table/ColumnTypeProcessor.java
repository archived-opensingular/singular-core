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

package org.opensingular.lib.commons.table;

import org.joda.time.LocalDate;
import org.joda.time.LocalDateTime;
import org.joda.time.base.AbstractInstant;
import org.opensingular.internal.lib.commons.xml.ConversorToolkit;
import org.opensingular.lib.commons.base.SingularException;
import org.opensingular.lib.commons.ui.Alignment;

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


    ColumnTypeProcessor BOOLEAN = new ColumnTypeProcessorTypeBoolean();


    ColumnTypeProcessor ACTION = new ColumnTypeProcessorTypeAction();


    ColumnTypeProcessor DATE = new ColumnTypeProcessorTypeDateBased("short");


    ColumnTypeProcessor DATE_HOUR = new ColumnTypeProcessorTypeDateBased("dd/MM/yy HH:mm:ss");


    ColumnTypeProcessor DATE_HOUR_SHORT = new ColumnTypeProcessorTypeDateBased("dd/MM/yy HH:mm");


    ColumnTypeProcessor DAY = new ColumnTypeProcessorTypeDateBased("dd");


    ColumnTypeProcessor RAW = new ColumnTypeProcessorTypeRaw();


    ColumnTypeProcessor STRING = new ColumnTypeProcessorTypeString();


    ColumnTypeProcessor NUMBER = new ColumnTypeProcessorTypeNumber();


    ColumnTypeProcessor INTEGER = new ColumnTypeProcessorTypeInteger();


    ColumnTypeProcessor PERCENT = new ColumnTypeProcessorTypePercent();


    ColumnTypeProcessor HOUR = new ColumnTypeProcessorTypeHour();

    /**
     * Verifica se a celula em questão possui algum valor para ser exibido de acordo com as definições do procesador.
     */
    default boolean isNullContent(InfoCell cell) {
        return cell == null || cell.getValue() == null;
    }

    default boolean shouldBePrinted() {
        return true;
    }

    default boolean shouldBeGeneretedOnStaticContent() {
        return true;
    }

    default PrintResult generatePrintValue(@Nonnull Column column, @Nullable Object value) {
        PrintResult result = new PrintResult();
        generatePrintValue(result, column, value);
        return result;
    }

    void generatePrintValue(@Nonnull PrintResult result, @Nonnull Column column, @Nullable Object value);

    default Alignment getDefaultAlignment() {
        return Alignment.LEFT;
    }

    /**
     * Verify the order between the two values.
     *
     * @see java.util.Comparator#compare(Object, Object)
     */
    default int compare(@Nonnull Object v1, @Nonnull Object v2) {
        if (v1 instanceof Comparable<?> && v1.getClass().isAssignableFrom(v2.getClass())) {
            return ((Comparable<Object>) v1).compareTo(v2);
        }
        throw new SingularException(
                "It's not possible to compare the object of the class " + v1.getClass().getName() + " and " +
                        v2.getClass().getName());

    }

    class PrintResult {

        private String content;
        private boolean defined;

        public String getContent() {
            return content;
        }

        public void setContent(String content) {
            this.content = content;
            this.defined = true;
        }

        public boolean isDefined() {
            return defined;
        }
    }


    class ColumnTypeProcessorTypeAction implements ColumnTypeProcessor {

        @Override
        public boolean isNullContent(InfoCell cell) {
            return cell == null || cell.isActionsEmpty();
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
        public void generatePrintValue(@Nonnull PrintResult result, @Nonnull Column column, Object value) {
            throw new SingularException("This method shouldn't be called");
        }

    }

    class ColumnTypeProcessorTypeBoolean implements ColumnTypeProcessor {

        @Override
        public void generatePrintValue(@Nonnull PrintResult result, @Nonnull Column column, Object value) {
            if (value instanceof Boolean) {
                result.setContent((Boolean) value ? "Sim" : "Não");
            }
        }

        @Override
        public Alignment getDefaultAlignment() {
            return Alignment.CENTER;
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

    class ColumnTypeProcessorTypeDateBased implements ColumnTypeProcessor {

        private final String dateFormat;

        public ColumnTypeProcessorTypeDateBased(String dateFormat) {
            this.dateFormat = dateFormat;
        }

        @Override
        public void generatePrintValue(@Nonnull PrintResult result, @Nonnull Column column, Object value) {
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
        public Alignment getDefaultAlignment() {
            return Alignment.CENTER;
        }

        public int compare(@Nonnull Object v1, @Nonnull Object v2) {
            Date d1 = asDate(v1);
            Date d2 = asDate(v2);
            if (d1 == d2) {
                return 0;
            } else if (d1 == null) {
                return -1;
            } else if (d2 == null) {
                return 1;
            }
            return d1.compareTo(d2);
        }

        public String getDateFormat() {
            return dateFormat;
        }
    }

    class ColumnTypeProcessorTypeRaw implements ColumnTypeProcessor {

        @Override
        public void generatePrintValue(@Nonnull PrintResult result, @Nonnull Column column, Object value) {
            result.setContent(value == null ? null : value.toString());
        }

        @Override
        public int compare(@Nonnull Object v1, @Nonnull Object v2) {
            return Objects.toString(v1).compareToIgnoreCase(Objects.toString(v2));
        }
    }

    class ColumnTypeProcessorTypeString implements ColumnTypeProcessor {

        @Override
        public void generatePrintValue(@Nonnull PrintResult result, @Nonnull Column column, Object value) {
            //Deixa o tratamento default, que inclui a introdução de escapes HTML
        }

        @Override
        public int compare(@Nonnull Object v1, @Nonnull Object v2) {
            return Objects.toString(v1).compareToIgnoreCase(Objects.toString(v2));
        }
    }

    class ColumnTypeProcessorTypeNumber implements ColumnTypeProcessor {

        private final int defaultNumberOfDigits;

        ColumnTypeProcessorTypeNumber() {
            this(2);
        }

        ColumnTypeProcessorTypeNumber(int defaultNumberOfDigits) {
            this.defaultNumberOfDigits = defaultNumberOfDigits;
        }

        /**
         * Verifica se a celula em questão possui algum valor para ser exibido de acordo com as definições do
         * procesador.
         */
        @Override
        public boolean isNullContent(InfoCell cell) {
            if (cell == null || cell.getValue() == null) {
                return true;
            } else if (cell.getValue() instanceof Number) {
                return !cell.getColumn().isShowZero() && ConversorToolkit.isZero((Number) cell.getValue());
            }
            return false;
        }

        @Override
        public final void generatePrintValue(@Nonnull PrintResult result, @Nonnull Column column, Object value) {
            if (value instanceof Number) {
                Number n = (Number) value;
                if (!column.isShowZero() && ConversorToolkit.isZero(n)) {
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
            return ConversorToolkit.printNumber(value, column.getFractionDigits(defaultNumberOfDigits));
        }

        @Override
        public Alignment getDefaultAlignment() {
            return Alignment.RIGHT;
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

    class ColumnTypeProcessorTypeInteger extends ColumnTypeProcessorTypeNumber {
        public ColumnTypeProcessorTypeInteger() {
            super(0);
        }
    }

    class ColumnTypeProcessorTypePercent extends ColumnTypeProcessorTypeNumber {
        public ColumnTypeProcessorTypePercent() {
            super(1);
        }

        @Override
        protected void generatePrintValue(@Nonnull PrintResult result, @Nonnull Column column, @Nonnull Number value) {
            Number n = ConversorToolkit.multiply(value, 100);
            result.setContent(format(column, n) + "%");
        }
    }

    class ColumnTypeProcessorTypeHour extends ColumnTypeProcessorTypeNumber {
        @Override
        protected void generatePrintValue(@Nonnull PrintResult result, @Nonnull Column column, @Nonnull Number value) {
            result.setContent(ConversorToolkit.toHour(value, null));
        }
    }
}
