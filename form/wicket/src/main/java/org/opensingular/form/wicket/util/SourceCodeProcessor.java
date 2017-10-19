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

package org.opensingular.form.wicket.util;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class SourceCodeProcessor {

    private final String sourceCode;
    private final List<Integer> linesToBeHighlighted;
    private final List<String> finalSourceCode;
    private final List<String> classJavadoc;
    private final static List<String> TO_BE_IGNORED = Collections.singletonList("@formatter");

    public SourceCodeProcessor(String sourceCode) {
        this.sourceCode = sourceCode;
        this.linesToBeHighlighted = new ArrayList<>();
        this.finalSourceCode = new ArrayList<>();
        this.classJavadoc = new ArrayList<>();
        process();
    }

    private void process() {
        boolean        javadoc        = false;
        boolean        classStarted = false;
        final String[] lines         = sourceCode.split("\n");

        for (int i = 0; i < lines.length; i += 1) {
            final String line = lines[i];
            if (javadoc) {
                javadoc = processJavaDoc(line);
                continue;
            } else if (!classStarted) {
                if (line.startsWith("/**")) {
                    javadoc = true;
                    continue;
                }
                classStarted = line.contains("public class ");
            }
            if (!isLineToBeIgnored(line)) {
                i = processCode(lines, i, line);
            }
        }
    }

    private int processCode(String[] lines, int i, String line) {
        int j = i;

        if(isNotBeShow(line)){
        } else if (isBlock(line)) {
            j =  processBlock(lines, j);
        } else if (isLine(line)) {
            finalSourceCode.add(lines[++j]);
            linesToBeHighlighted.add(finalSourceCode.size());
        } else {
            finalSourceCode.add(line);
        }
        return j;
    }

    private boolean isNotBeShow(String line) {
        return line.contains("CaseItem") || line.contains("Resource") || line.contains("TODO");
    }

    private int processBlock(String[] lines, int i) {
        while (!isEndOfBlock(lines[++i])) {
            finalSourceCode.add(lines[i]);
            linesToBeHighlighted.add(finalSourceCode.size());
        }
        return i;
    }

    private boolean processJavaDoc(String line) {
        if (line.contains("*/")) {
            return false;
        } else {
            classJavadoc.add(line.replace(" *", ""));
        }
        return true;
    }

    private boolean isBlock(String candidate) {
        return candidate.contains("//@destacar:bloco") || candidate.contains("// @destacar:bloco");
    }

    private boolean isEndOfBlock(String candidate) {
        return candidate.contains("//@destacar:fim") || candidate.contains("// @destacar:fim");
    }

    private boolean isLine(String candidate) {
        return candidate.contains("//@destacar") || candidate.contains("// @destacar");
    }

    private boolean isLineToBeIgnored(String candidate) {
        for (String ignore : TO_BE_IGNORED) {
            if (candidate.contains(ignore)) {
                return true;
            }
        }
        return false;
    }

    public String getResultSourceCode() {
        StringBuilder sb = new StringBuilder();
        finalSourceCode.forEach(s -> sb.append(s).append('\n'));
        return sb.toString();
    }

    public String getJavadoc() {
        StringBuilder sb = new StringBuilder();
        classJavadoc.forEach(s -> sb.append(s).append('\n'));
        return sb.toString();
    }

    public List<Integer> getLinesToBeHighlighted() {
        return linesToBeHighlighted;
    }
}
