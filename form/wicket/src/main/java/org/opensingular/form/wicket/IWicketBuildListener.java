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

package org.opensingular.form.wicket;

import java.io.Serializable;

/**
 * Interface listener para o ciclo de build
 */
public interface IWicketBuildListener extends Serializable {
    //@formatter:off

    default void onBuildContextInitialized(WicketBuildContext ctx) {}

    default void onMapperResolved(WicketBuildContext child, IWicketComponentMapper mapper) {}

    default WicketBuildContext decorateContext(WicketBuildContext ctx, IWicketComponentMapper mapper) { return null; }

    default void onBeforeBuild(WicketBuildContext ctx, IWicketComponentMapper mapper) {}

    default void onAfterBuild(WicketBuildContext ctx, IWicketComponentMapper mapper) {}
    
    default boolean isActive(WicketBuildContext ctx, IWicketComponentMapper mapper) { return true; }
    
    //@formatter:on
}
