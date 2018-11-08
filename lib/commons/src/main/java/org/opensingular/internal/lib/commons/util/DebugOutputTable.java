package org.opensingular.internal.lib.commons.util;

import org.apache.commons.lang3.StringUtils;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Function;

/**
 * Helps formats the output in columns with values aligned between lines.
 * <p>The resultatnt table ill respect the {@link DebugOutput} current indentation level.</p>
 *
 * @author Daniel C. Bordin
 * @since 2018-10-03
 */
public class DebugOutputTable {

    private final DebugOutput out;
    private boolean silentMode;
    private int currentPos;
    private boolean pendingValue;

    private final List<InfoColumn> columns = new ArrayList<>();

    DebugOutputTable(@Nonnull DebugOutput debugOutput) {
        this.out = Objects.requireNonNull(debugOutput);
    }

    /**
     * Creates a table with a line for each element of the collection and the values for each cell of the line provided
     * by the informed line mapper.
     * <p><The collection will read twice.</p>
     */
    public <T> void map(@Nonnull Collection<T> value, @Nonnull BiConsumer<DebugOutputTable, T> lineMapper) {
        map(value, lineMapper, null);
    }

    /**
     * Creates a table with a line for each element of the collection and the values for each cell of the line provided
     * by the informed line mapper.
     * <p><The collection will read twice.</p>
     *
     * @param preMapper Called before generating the values. It'll probably create the header.
     */
    public <T> void map(@Nonnull Collection<T> value, @Nonnull BiConsumer<DebugOutputTable, T> lineMapper,
            @Nullable Consumer<DebugOutputTable> preMapper) {
        silentMode = true;
        mapInternal(value, lineMapper, preMapper);
        silentMode = false;
        mapInternal(value, lineMapper, preMapper);
    }

    private <T> void mapInternal(@Nonnull Collection<T> value, @Nonnull BiConsumer<DebugOutputTable, T> lineMapper,
            @Nullable Consumer<DebugOutputTable> preMapper) {
        if (preMapper != null) {
            preMapper.accept(this);
            if (pendingValue) {
                println();
            }
        }
        value.forEach(v -> {
            lineMapper.accept(this, v);
            println();
        });
    }

    /**
     * Creates a new column with the initial informed width. The width will be automatic adjusted if the column receives
     * a larger value.
     */
    public void addColumn(int width) {
        columns.add(new InfoColumn(width));
    }


    /**
     * Adds a new value for in the current line and column, then changes the current cell to the next column in the
     * line.
     */
    public void addValue(@Nullable Object value) {
        setValue(currentPos, value);
        currentPos++;
    }

    /**
     * Add all the values to the current line (multiples call to {@link #addValue(Object)} then close the current line
     * (prinln()).
     */
    public void addLine(@Nullable Object... values) {
        if (values != null) {
            for (Object v : values) {
                addValue(v);
            }
        }
        println();
    }

    /**
     * Add all the values to the current line (multiples call to {@link #addValue(Object)} then close the current line
     * (println()).
     */
    public void addLine(@Nullable Collection<?> values) {
        addLine(values, null);
    }

    /**
     * Add all the values, after transforming then with the informed mapper, to the current line (multiples call to
     * {@link #addValue(Object)} then close the current line (println()).
     */
    public <T> void addLine(@Nullable Collection<T> values, @Nullable Function<T, Object> mapper) {
        if (values != null) {
            Function<T, ?> f = mapper == null ? Function.identity() : mapper;
            values.forEach(v -> addValue(v == null ? null : f.apply(v)));
        }
        println();
    }

    /** Same as {@link #addLine(Collection)}. */
    public void println(@Nullable Object... values) {
        addLine(values);
    }

    /** Set the value of column in the index position in the current line. Null values won't be printed. */
    public void setValue(int index, @Nullable Object value) {
        if (value != null) {
            while (index >= columns.size()) {
                addColumn(0);
            }
            columns.get(index).setValue(value);
            pendingValue = true;
        }
    }

    /** Writes all the value (columns) to the {@link DebugOutput} and closes the current line. */
    public void println() {
        int last = columns.size() - 1;
        while (last != -1 && columns.get(last).getValue() == null) {
            last--;
        }
        for (int i = 0; i <= last; i++) {
            if (i != 0 && !silentMode) {
                out.print(' ');
            }
            InfoColumn col = columns.get(i);
            String v = col.getValue();
            col.setValue(null);
            if (v == null) {
                v = "";
            }
            if (i != last) {
                v = StringUtils.rightPad(v, col.size);
            }
            if (!silentMode) {
                out.print(v);
            }
        }
        currentPos = 0;
        pendingValue = false;
        if (!silentMode) {
            out.println();
        }
    }

    private static class InfoColumn {
        public int size;
        private String value;

        InfoColumn(int size) {
            this.size = size;
        }

        public void setValue(Object v) {
            setValue(v == null ? null : v.toString());
        }

        public void setValue(String v) {
            value = v;
            if (v != null) {
                size = Math.max(size, v.length());
            }
        }

        public String getValue() {
            return value;
        }
    }
}
