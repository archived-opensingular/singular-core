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

import com.google.common.base.Predicates;

import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;

@SuppressWarnings("unchecked")
public enum ColumnAggregationType {
    CALCULATION("Calcular") {
        @Override
        public boolean isApplicable(Class<?> dataClass) {
            return true;
        }

        @Override
        public Object calculate(List<?> data) {
            throw new UnsupportedOperationException();
        }
    },
    MIN("Mínimo") {
        @Override
        public boolean isApplicable(Class<?> dataClass) {
            return Comparable.class.isAssignableFrom(dataClass);
        }

        @Override
        public Object calculate(List<?> data) {
            return data.stream().filter(Predicates.instanceOf(Comparable.class)).map(Comparable.class::cast).min(Comparator.comparing(Function.identity())).orElse(null);
        }
    },
    MAX("Máximo") {
        @Override
        public boolean isApplicable(Class<?> dataClass) {
            return Comparable.class.isAssignableFrom(dataClass);
        }

        @Override
        public Object calculate(List<?> data) {
            return data.stream().filter(Predicates.instanceOf(Comparable.class)).map(Comparable.class::cast).max(Comparator.comparing(Function.identity())).orElse(null);
        }
    },
    MEDIA("Média") {
        @Override
        public boolean isApplicable(Class<?> dataClass) {
            return Long.class.isAssignableFrom(dataClass) ||
                    Integer.class.isAssignableFrom(dataClass) ||
                    Double.class.isAssignableFrom(dataClass);
        }

        @Override
        public Object calculate(List<?> data) {
            if (data.stream().anyMatch(Predicates.instanceOf(Double.class))) {
                return data.stream().map(dado -> dado == null ? 0.0d : dado).filter(Predicates.instanceOf(Double.class)).mapToDouble(Double.class::cast).average().orElse(0.0);
            }
            if (data.stream().anyMatch(Predicates.instanceOf(Integer.class))) {
                return data.stream().map(dado -> dado == null ? 0 : dado).filter(Predicates.instanceOf(Integer.class)).mapToInt(Integer.class::cast).average().orElse(0);
            }
            if (data.stream().anyMatch(Predicates.instanceOf(Long.class))) {
                return data.stream().map(dado -> dado == null ? 0L : dado).filter(Predicates.instanceOf(Long.class)).mapToLong(Long.class::cast).average().orElse(0L);
            }
            return 0;
        }
    },
    SUM("Soma") {
        @Override
        public boolean isApplicable(Class<?> dataClass) {
            return Long.class.isAssignableFrom(dataClass) ||
                    Integer.class.isAssignableFrom(dataClass) ||
                    Double.class.isAssignableFrom(dataClass);
        }

        @Override
        public Object calculate(List<?> data) {
            if (data.stream().anyMatch(Predicates.instanceOf(Collection.class))) {
                return data.stream().filter(Predicates.instanceOf(Collection.class)).map(Collection.class::cast).flatMap(Collection::stream).distinct().collect(Collectors.toList());
            }
            if (data.stream().anyMatch(Predicates.instanceOf(Double.class))) {
                return data.stream().filter(Predicates.instanceOf(Double.class)).mapToDouble(Double.class::cast).sum();
            }
            if (data.stream().anyMatch(Predicates.instanceOf(Integer.class))) {
                return data.stream().filter(Predicates.instanceOf(Integer.class)).mapToInt(Integer.class::cast).sum();
            }
            if (data.stream().anyMatch(Predicates.instanceOf(Long.class))) {
                return data.stream().filter(Predicates.instanceOf(Long.class)).mapToLong(Long.class::cast).sum();
            }
            if (data.stream().anyMatch(Predicates.instanceOf(String.class))) {
                return data.stream().filter(Predicates.instanceOf(String.class)).map(String.class::cast).distinct().collect(Collectors.joining("; "));
            }
            return null;
        }
    },
    COUNT("Qtd.") {
        @Override
        public boolean isApplicable(Class<?> dataClass) {
            return true;
        }

        @Override
        public Object calculate(List<?> data) {
            return data.stream().filter(Objects::nonNull).count();
        }
    },
    COUNT_DISTINCT("Qtd. �nica") {
        @Override
        public boolean isApplicable(Class<?> dataClass) {
            return true;
        }

        @Override
        public Object calculate(List<?> data) {
            return data.stream().filter(Objects::nonNull).distinct().count();
        }
    };

    private final String name;

    ColumnAggregationType(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public boolean isCalculation() {
        return this == CALCULATION;
    }

    public boolean isSum() {
        return this == SUM;
    }

    public boolean isCount() {
        return this == COUNT;
    }

    public boolean isCountDistinct() {
        return this == COUNT_DISTINCT;
    }

    public boolean isApplicable(Class<?> dataClass) {
        return true;
    }

    public Object calculate(Supplier<List<?>> valueListSupplier) {
        return calculate(valueListSupplier.get());
    }

    public abstract Object calculate(List<?> data);
}
