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

package org.opensingular.form.aspect;

import org.opensingular.form.SType;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.function.Supplier;

/**
 * Defines a Java code (aspect) that can be added to any {@link org.opensingular.form.SType}. Usually it's a Java
 * Interface that each {@link org.opensingular.form.SType} my have a different implementation. This allows do add new
 * behaviors to the {@link org.opensingular.form.SType}.
 * <p>
 * <p>Works polymorphically in the {@SType} hierarchy, i.e. when searching for a aspect implementation of a {@link
 * SType}, if there isn't a implementation direct associated to the type, then it will search in the type witch type
 * extends ({@link SType#getSuperType()}.</p>
 * <p>A {@link AspectRef} may or may not define the loading of a default registration of implementations for the {@link
 * org.opensingular.form.SType} hierarchy depending on the constructors that are used.</p>
 * <p>When searching for as aspect implementation for a particular {@link SType} the following steps are tried:</p>
 * <ol>
 * <li>Looks if the type has a direct implementation associated directly to the type through {@link
 * SType#setAspect(AspectRef, Supplier)}</li>
 * <li>Looks into the defaultRegistry associated with the aspect ({@link AspectRef#getRegistryClass()}) for a
 * registered
 * implementation to the particular {@link SType} class.</li>
 * <ol type="a">
 * <li>If there is more then one match implementation for the type, then it's used {@link
 * QualifierMatcher#compare(AspectEntry, AspectEntry)} to decide witch one is more relevant.</li>
 * <li>If there isn't a {@link QualifierStrategy} defined for the aspect or the compare result in a tie, then return the
 * one with the highest priority number ({@link AspectEntry#getPriority()}).</li>
 * <li>If all have the same priority, then the first implementation found is returned.</li>
 * </ol>
 * <li>If the above steps fail and the current type isn't equals to {@link SType} (the top most type), then gets {@link
 * SType#getSuperType()} and start over step one.</li>
 * </ol>
 * <p><b>Example of aspect without default registration:</b></p>
 * <pre>{@code
 *
 *     //Defines MyInterface as new aspect. The reference must be available globally
 *     public static final AspectRef<MyInterface> ASPECT_MY_INTERFACE = new AspectRef<>(MyInterface.class);
 *
 *     //A example of the definition of the interface reference by the aspect
 *     private static interface MyInterface {
 *         public String getText(int i);
 *     }
 *
 *     //Example of a implementation of the aspect
 *     public class MyInterfaceForSTypeExample implements MyInterface {
 *         public String getText(int i) {
 *             return "Hello World: " + i;
 *         }
 *     }
 *
 *     //Binds a specific implementation to a particular SType
 *     public static class STypeMyExample extends STypeString {
 *         protected void onLoadType(TypeBuilder tb) {
 *             setAspect(ASPECT_MY_INTERFACE, MyInterfaceForSTypeExample::new);
 *         }
 *     }
 *
 *     //Using the aspect
 *     SType type = ...
 *     Optional<MyInterface> impl = type.getAspect(ASPECT_MY_INTERFACE);
 *     if(impl.isPresent()) {
 *         System.out.println(impl.get().getText(10));
 *     }
 * } </pre>
 * <p><b>Example of aspect with default registration:</b></p>
 * <pre>{@code
 *
 *     //Defines MyInterface as new aspect and with MyInterfaceRegistry as as a default registry
 *     public static final AspectRef<MyInterface> ASPECT_MY_INTERFACE
 *              = new AspectRef<>(MyInterface.class, MyInterfaceRegistry.class);
 *
 *     //Creates a registry with the default implementations and associations
 *     public static class MyInterfaceRegistry extends SingleAspectRegistry<MyInterface, Object> {
 *         public MyInterfaceRegistry(@Nonnull AspectRef<MyInterface> aspectRef) {
 *             super(aspectRef);
 *             addFixImplementation(STypeString.class, MyInterfaceForSTypeString::new);
 *             addFixImplementation(STypeList.class  , MyInterfaceForSTypeList::new);
 *         }
 *     }
 *
 *     //Using the aspect
 *     STypeString tString = ...
 *     tString.getAspect(ASPECT_MY_INTERFACE);  // return a MyInterfaceForSTypeString reference
 *     STypeEMail tMail = ...                   // STypeEMail extends STypeString
 *     tMail.getAspect(ASPECT_MY_INTERFACE);    // return a MyInterfaceForSTypeString reference
 *     STypeBoolean tBoolean = ...
 *     tBoolean.getAspect(ASPECT_MY_INTERFACE); // return null reference
 *     STypeList tList = ....
 *     tList.getAspect(ASPECT_MY_INTERFACE);    // return a MyInterfaceForSTypeList reference
 *
 *     SIString iString = ...                   // a instance of STypeString
 *     iString.getAspect(ASPECT_MY_INTERFACE);  // return a MyInterfaceForSTypeString reference
 *     SIBoolean iBoolean = ...                 // a instance of STypeString
 *     iBoolean.getAspect(ASPECT_MY_INTERFACE); // return null reference
 * } </pre>
 *
 * @author Daniel C. Bordin on 09/08/2017.
 * @see org.opensingular.form.SType#getAspect(AspectRef)
 * @see org.opensingular.form.SInstance#getAspect(AspectRef)
 */
public class AspectRef<ASPECT> {

    private final Class<ASPECT> aspectClass;

    private final Class<? extends SingleAspectRegistry<ASPECT, ?>> registryClass;

    /** Create a new aspect of the appointed class and <b>without</b> a default registration of implementations. */
    public AspectRef(@Nonnull Class<ASPECT> aspectClass) {
        this(aspectClass, null);
    }

    /** Create a new aspect of the appointed class and <b>with</b> a default registration of implementations. */
    public AspectRef(@Nonnull Class<ASPECT> aspectClass,
            @Nullable Class<? extends SingleAspectRegistry<ASPECT, ?>> registryClass) {
        this.aspectClass = aspectClass;
        this.registryClass = registryClass;
    }

    /** The java class of the implementations that will lookup for this aspect. Usually its a Java interface. */
    @Nonnull
    public Class<ASPECT> getAspectClass() {
        return aspectClass;
    }

    /**
     * Identifies de the default registration of implementations for this aspect. Being null, means that this aspect
     * doesn't have default mapping of implementations.
     */
    @Nullable
    public Class<? extends SingleAspectRegistry<ASPECT, ?>> getRegistryClass() {
        return registryClass;
    }
}
