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

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.text.Normalizer;

public final class SingularUtil {

    private SingularUtil() {}

    public static RuntimeException propagate(Throwable throwable) {
        Throwables.propagateIfPossible(throwable, SingularException.class);
        throw SingularException.rethrow(throwable);
    }
    
    public static String toSHA1(Object object) {
        return toSHA1(object.toString().getBytes(StandardCharsets.UTF_8));
    }

    public static String toSHA1(byte[] bytes) {
        MessageDigest sha1Digest = DigestUtils.getSha1Digest();
        sha1Digest.update(bytes);

        return Hex.encodeHexString(sha1Digest.digest());
    }

    public static String convertToJavaIdentity(String original, boolean normalize) {
        return convertToJavaIdentity(original, false, normalize);
    }

    public static String convertToJavaIdentity(String original, boolean firstCharacterUpperCase, boolean normalize) {
        String normalized = normalize ? normalize(original) : original;
        StringBuilder sb = new StringBuilder(normalized.length());
        boolean nextUpper = false;
        for (char c : normalized.toCharArray()) {
            if (sb.length() == 0) {
                if (Character.isJavaIdentifierStart(c)) {
                    c = firstCharacterUpperCase ? Character.toUpperCase(c) : Character.toLowerCase(c);
                    sb.append(c);
                }
            } else if (Character.isJavaIdentifierPart(c)) {
                if (nextUpper) {
                    c = Character.toUpperCase(c);
                    nextUpper = false;
                }
                sb.append(c);
            } else if (Character.isWhitespace(c)) {
                nextUpper = true;
            }
        }
        return sb.toString();
    }

    public static String normalize(String original) {
        return Normalizer.normalize(original, Normalizer.Form.NFD).replaceAll("[^\\p{ASCII}]", "");
    }
}
