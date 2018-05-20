/*
 *
 *  * Copyright (C) 2016 Singular Studios (a.k.a Atom Tecnologia) - www.opensingular.com
 *  *
 *  * Licensed under the Apache License, Version 2.0 (the "License");
 *  *  you may not use this file except in compliance with the License.
 *  * You may obtain a copy of the License at
 *  *
 *  * http://www.apache.org/licenses/LICENSE-2.0
 *  *
 *  * Unless required by applicable law or agreed to in writing, software
 *  * distributed under the License is distributed on an "AS IS" BASIS,
 *  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  * See the License for the specific language governing permissions and
 *  * limitations under the License.
 *
 */

package org.opensingular.lib.commons.extension;

/**
 * Common base interface for extensions point in Singular. Specific code extension points must create a new interface
 * that also extends this interface.
 * <p>All extensions of this interface must have the 'Extension' suffix.</p>
 * <p>For more information of how to use extensions points, see {@link SingularExtensionUtil}.</p>
 */
public interface SingularExtension {

    /**
     * Indicates the priority of the implementation in comparision to another implementation of the same interface. This
     * is used to select the more relevant implementation when two or mores implementation are available.
     *
     * @return 0 by default
     */
    public default int getExtensionPriority() {
        return 0;
    }
}