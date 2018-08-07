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

package org.opensingular.flow.core;

/**
 * Represents the different strategies for displaying transition's label when generating a diagram for a flow.
 *
 * @author Daniel C. Bordin
 * @since 2018-04-29
 */
public enum DisplayTransitionLabelStrategy {
    /** All transition's labels will be showed without any suppression. */
    ALL, /**
     * In some situations a label may be suppressed if it doesn't strongly helps the understanding of the flow. For
     * example, if the task has just one out going transition, so the transition won't have its label displayed.
     */
    SMART, /**
     * All transition's label will be suppressed. None will be showed.
     */
    NONE
}
