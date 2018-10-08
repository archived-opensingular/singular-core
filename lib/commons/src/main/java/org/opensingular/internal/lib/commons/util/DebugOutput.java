package org.opensingular.internal.lib.commons.util;

import org.opensingular.lib.commons.base.SingularException;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.io.IOException;
import java.util.Collection;
import java.util.Objects;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

/**
 * Helps to generate a text representation, i.e., a dump info of a object, for debug purposes. Besides helping format
 * the printed info, also helps to create indentation between sub levels of information.
 *
 * @author Daniel C. Bordin
 * @since 2018-09-10
 */
public class DebugOutput implements Appendable {

    private final Appendable appendable;
    private final int leftMargin;
    private boolean firstLineContent = true;

    /** Creates a debug output to the System.out. */
    public DebugOutput() {
        this(System.out);
    }

    /** Creates a debug output to the informed {@link Appendable} with text starting at column zero. */
    public DebugOutput(@Nonnull Appendable appendable) {
        this(appendable, 0);
    }

    /**
     * Creates a debug output to the informed {@link Appendable} with text starting at column informed.
     *
     * @param leftMargin How many spaces to be included in the begging of each line. May be zero.
     */
    public DebugOutput(@Nonnull Appendable appendable, int leftMargin) {
        this.appendable = Objects.requireNonNull(appendable);
        this.leftMargin = leftMargin;
    }

    @Nonnull
    public Appendable getAppendable() {
        return appendable;
    }

    /** Creates a new debug output with the left margin increased by three relative to the current debug output. */
    @Nonnull
    public DebugOutput addSubLevel() {
        if (!firstLineContent) {
            println();
        }
        return new DebugOutput(appendable, leftMargin + 3);
    }

    @Nonnull
    public DebugOutput println(@Nullable CharSequence csq) {
        print(csq);
        return println();
    }

    @Nonnull
    public DebugOutput println(@Nullable Object obj) {
        print(obj);
        return println();
    }

    @Nonnull
    public DebugOutput print(@Nullable CharSequence csq) {
        return append(csq);
    }

    @Nonnull
    public DebugOutput print(@Nullable Object obj) {
        if (obj == null) {
            append("null");
        } else if (obj instanceof CharSequence) {
            append((CharSequence) obj);
        } else {
            append(obj.toString());
        }
        return this;
    }

    @Nonnull
    public DebugOutput println() {
        appendInternal('\n');
        firstLineContent = true;
        return this;
    }

    private void checkFirstLineContent() {
        if (firstLineContent) {
            for (int i = leftMargin; i > 0; i--) {
                appendInternal(' ');
            }
            firstLineContent = false;
        }
    }

    private void verifyNewLine(char c) {
        if (c == '\n') {
            firstLineContent = true;
        }
    }

    private void verifyNewLine(@Nonnull CharSequence csq, int start, int end) {
        if (csq.charAt(end - 1) == '\n') {
            firstLineContent = true;
        }
    }

    @Nonnull
    @Override
    public DebugOutput append(@Nullable CharSequence csq) {
        CharSequence text = csq == null ? "null" : csq;
        checkFirstLineContent();
        appendInternal(text);
        verifyNewLine(text, 0, text.length());
        return this;
    }


    @Nonnull
    @Override
    public DebugOutput append(@Nullable CharSequence csq, int start, int end) {
        if (csq == null) {
            return append(null);
        }
        checkFirstLineContent();
        appendInternal(csq, start, end);
        verifyNewLine(csq, start, end);
        return this;
    }

    @Nonnull
    @Override
    public DebugOutput append(char c) {
        checkFirstLineContent();
        appendInternal(c);
        verifyNewLine(c);
        return this;
    }


    private void appendInternal(@Nullable CharSequence csq) {
        try {
            appendable.append(csq);
        } catch (IOException e) {
            throw SingularException.rethrow(e);
        }
    }

    private void appendInternal(@Nullable CharSequence csq, int start, int end) {
        try {
            appendable.append(csq, start, end);
        } catch (IOException e) {
            throw SingularException.rethrow(e);
        }
    }

    private void appendInternal(char c) {
        try {
            appendable.append(c);
        } catch (IOException e) {
            throw SingularException.rethrow(e);
        }
    }

    /** Creates a table generator base in the current output and indentation. */
    @Nonnull
    public DebugOutputTable table() {
        return new DebugOutputTable(this);
    }

    /**
     * Creates a table with a line for each element of the collection and the values for each cell of the line provided
     * by the informed line mapper.
     * <p><The collection will read twice.</p>
     */
    public <T> void table(@Nonnull Collection<T> value, @Nonnull BiConsumer<DebugOutputTable, T> lineMapper) {
        table().map(value, lineMapper);
    }

    /**
     * Creates a table with a line for each element of the collection and the values for each cell of the line provided
     * by the informed line mapper.
     * <p><The collection will read twice.</p>
     * @param preMapper Called before generating the values. It'll probably create the header.
     */
    public <T> void table(@Nonnull Collection<T> value, @Nonnull BiConsumer<DebugOutputTable, T> lineMapper,
            @Nullable Consumer<DebugOutputTable> preMapper) {
        table().map(value, lineMapper, preMapper);
    }
}