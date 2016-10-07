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

import java.security.MessageDigest;
import java.text.Normalizer;

import org.apache.commons.codec.binary.Hex;
import org.apache.commons.codec.digest.DigestUtils;

import com.google.common.base.Throwables;

public final class SingularUtil {

    public static RuntimeException propagate(Throwable throwable) {
        Throwables.propagateIfPossible(throwable, SingularException.class);
        throw new SingularException(throwable);
    }
    
    public static String toSHA1(Object object) {
        return toSHA1(object.toString().getBytes());
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
        if (normalize) {
            original = normalize(original);
        }
        StringBuilder sb = new StringBuilder(original.length());
        boolean nextUpper = false;
        for (char c : original.toCharArray()) {
            if (sb.length() == 0) {
                if (Character.isJavaIdentifierStart(c)) {
                    if (firstCharacterUpperCase) {
                        sb.append(Character.toUpperCase(c));
                    } else {
                        sb.append(Character.toLowerCase(c));
                    }
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
