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

package org.opensingular.internal.lib.commons.util;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Random;

/**
 * Helpers methods to deal with random related tasks.
 *
 * @author Daniel C. Bordin
 * @since 2017-10-19
 */
public final class RandomUtil {
    private RandomUtil() {}

    private static final char[] VALID_PASSWORD_CHAR = new char[62];
    private static final Random RANDOM = new SecureRandom();

    static {
        for (int i = 48, j = 0; i < 123; i++) {
            if (Character.isLetterOrDigit(i)) {
                VALID_PASSWORD_CHAR[j] = (char) i;
                j++;
            }
        }
    }

    /** Generates a random password with a specific length. */
    @Nonnull
    public static String generateRandomPassword(int length) {
        return generateString(length, VALID_PASSWORD_CHAR);
    }

    //@formatter:off
    private static final char[] VALID_URL_CHAR = {
            'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L', 'M',
            'N', 'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z',
            'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm',
            'n', 'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z',
            '0', '1', '2', '3', '4', '5', '6', '7', '8', '9'
    };
    //@formatter:on

    /**
     * Generates a ID with random selected char and letters (a-z,A-Z,0-9) representation at least 128 bits. It is a
     * little bit more random than {@link java.util.UUID}, but generates a smaller String (22 chars instead of 36
     * chars).
     */
    @Nonnull
    public static String generateID() {
        return generateID(128);
    }

    /**
     * Generates a ID with random selected char and letters (a-z,A-Z,0-9) representation at least the amount of random
     * bits specified.
     */
    @Nonnull
    public static String generateID(int bytesSize) {
        double bitsRepresentation = log2(VALID_URL_CHAR.length);
        int charSize = (int) Math.ceil(bytesSize / bitsRepresentation);
        return generateString(charSize, VALID_URL_CHAR);
    }

    private static double log2(int x) {
        return Math.ceil(Math.log(x) / Math.log(2));
    }

    @Nonnull
    private static String generateString(int size, @Nonnull char[] validValues) {
        char[] result = new char[size];
        for (int i = 0; i < size; i++) {
            result[i] = validValues[RANDOM.nextInt(validValues.length)];
        }
        return String.valueOf(result);
    }

    /** Selects a random element of the provided collection. */
    @SuppressWarnings({"unchecked"})
    @Nullable
    public static <T> T selectRandom(@Nonnull Collection<T> list) {
        if (list.isEmpty()) {
            return null;
        }
        int pos;
        if (list.size() == 1) {
            pos = 0;
        } else {
            pos = RANDOM.nextInt(list.size());
        }
        if (list instanceof ArrayList) {
            return (T) ((ArrayList<?>) list).get(pos);
        }
        int i = 0;
        for (T obj : list) {
            if (i == pos) {
                return obj;
            }
            i++;
        }
        return null;
    }

    /** Returns the current random object being used. */
    @Nonnull
    public static Random getRandom() {
        return RANDOM;
    }
}
