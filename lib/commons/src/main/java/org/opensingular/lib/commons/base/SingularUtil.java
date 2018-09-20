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

package org.opensingular.lib.commons.base;

import com.google.common.base.Throwables;
import org.apache.commons.codec.binary.Hex;
import org.apache.commons.codec.digest.DigestUtils;

import javax.annotation.Nonnull;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.text.Normalizer;
import java.util.Objects;
import java.util.function.Function;

public final class SingularUtil {

    private SingularUtil() {}

    public static RuntimeException propagate(Throwable throwable) {
        Throwables.propagateIfPossible(throwable, SingularException.class);
        throw SingularException.rethrow(throwable);
    }

    @Nonnull
    public static String toSHA1(@Nonnull Object object) {
        return toSHA1(object.toString().getBytes(StandardCharsets.UTF_8));
    }

    @Nonnull
    public static String toSHA1(@Nonnull byte[] bytes) {
        Objects.requireNonNull(bytes);
        MessageDigest sha1Digest = DigestUtils.getSha1Digest();
        sha1Digest.update(bytes);

        return Hex.encodeHexString(sha1Digest.digest());
    }

    /**
     * Converts a arbitrary string to a valid java identifier (valid characters ans without spaces). If it's not
     * possible to convert, a exception is thrown.
     */
    @Nonnull
    public static String convertToJavaIdentifier(@Nonnull String original) {
        return convertToJavaIdentifier(original, false);
    }

    /**
     * Converts a arbitrary string to a valid java identifier (valid characters ans without spaces). If it's not
     * possible to convert, a exception is thrown.
     */
    @Nonnull
    public static String convertToJavaIdentifier(@Nonnull String original, boolean firstCharacterUpperCase) {
        Objects.requireNonNull(original);
        String normalized = normalize(original);
        StringBuilder sb = new StringBuilder(normalized.length());
        boolean nextUpper = false;
        for (char c : normalized.toCharArray()) {
            if (sb.length() == 0) {
                appendLengthZero(firstCharacterUpperCase, sb, c);
            } else if (Character.isJavaIdentifierPart(c)) {
                nextUpper = appendJavaIdentifierPart(sb, nextUpper, c);
            } else if (Character.isWhitespace(c)) {
                nextUpper = true;
            }
        }
        if (sb.length() == 0) {
            throw new SingularException("'" + original + "' it's no possible to convert to valid identifier");
        }
        return sb.toString();
    }

    private static boolean appendJavaIdentifierPart(StringBuilder sb, boolean nextUpper, char c) {
        char _c = c;
        if (nextUpper) {
            _c = Character.toUpperCase(_c);
        }
        sb.append(_c);
        return false;
    }

    private static void appendLengthZero(boolean firstCharacterUpperCase, StringBuilder sb, char c) {
        char _c = c;
        if (Character.isJavaIdentifierStart(_c)) {
            _c = firstCharacterUpperCase ? Character.toUpperCase(_c) : Character.toLowerCase(_c);
            sb.append(_c);
        }
    }

    /**
     * Transforms unicode characters in a simpler common version., because some different unicode characters have the
     * same semantic meaning. Also removes some special characters.
     *
     * @see Normalizer
     */
    @Nonnull
    public static String normalize(@Nonnull String original) {
        return Normalizer.normalize(original, Normalizer.Form.NFD).replaceAll("[^\\p{ASCII}]", "");
    }

    @SafeVarargs
    @SuppressWarnings("unchecked")
    public static <T> boolean areEqual(T a, Object b, Function<T, Object>... propertyFunctions) {
        if (a == b)
            return true;
        if (a == null || b == null)
            return false;
        if (a.getClass() != b.getClass())
            return false;
        final T typedB = (T) b;
        for (Function<T, Object> func : propertyFunctions) {
            final Object propA = func.apply(a);
            final Object propB = func.apply(typedB);
            if (propA == null) {
                if (propB != null)
                    return false;
            } else if (!propA.equals(propB))
                return false;
        }
        return true;
    }
}
