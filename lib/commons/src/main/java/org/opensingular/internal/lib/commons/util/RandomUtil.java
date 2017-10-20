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

    private static final char[] ALL_CHARS = new char[62];
    private static final Random RANDOM = new SecureRandom();

    static {
        for (int i = 48, j = 0; i < 123; i++) {
            if (Character.isLetterOrDigit(i)) {
                ALL_CHARS[j] = (char) i;
                j++;
            }
        }
    }

    /** Generates a random password with a specific length. */
    @Nonnull
    public static String generateRandomPassword(int length) {
        char[] result = new char[length];
        for (int i = 0; i < length; i++) {
            result[i] = ALL_CHARS[RANDOM.nextInt(ALL_CHARS.length)];
        }
        return String.valueOf(result);
    }

    /** Selects a random element of the provided collection. */
    @SuppressWarnings({"unchecked"})
    @Nullable
    public static <T> T selectRandom(Collection<T> list) {
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
}
