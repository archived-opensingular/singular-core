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

package org.opensingular.lib.commons.extension;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Repeatable;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Use optionally to specify a sub case (or more specific point of use) when declaring a implementation class of a
 * extension point.
 * <p>In the example bellow, if queried with different qualifiers the results are:</p>
 * <ol>
 *     <li>With "MainPageButtonBar" qualifier, the result is "ButtonC and ButtonA" (in this order);</li>
 *     <li>With "WorkListButtonBar" qualifier, the result is "ButtonB and ButtonC" (in this order);</li>
 *     <li>Without qualifier, the result is "ButtonB, ButtonC and ButtonA" (in this order);</li>
 * </ol>
 * <pre>
 *     public interface <b>ButtonExtension</b> extends SingularExtension {
 *         //Some methods
 *     }
 *
 *     <b>&#64;ExtensionQualifier("MainPageButtonBar")</b>
 *     public static class ButtonA implements ButtonExtension {
 *         &#64;Override
 *         public int getExtensionPriority() { return 5; }
 *         //Some other methods
 *     }
 *
 *     <b>&#64;ExtensionQualifier("WorkListButtonBar")</b>
 *     public static class <b>ButtonB</b> implements ButtonExtension {
 *         &#64;Override
 *         public int getExtensionPriority() { return 30; }
 *         //Some other methods
 *     }
 *
 *     <b>&#64;ExtensionQualifier("MainPageButtonBar")</b>
 *     <b>&#64;ExtensionQualifier("WorkListButtonBar")</b>
 *     public static class <b>ButtonC</b> implements ButtonExtension {
 *         &#64;Override
 *         public int getExtensionPriority() { return 20; }
 *         //Some other methods
 *     }</pre>
 *
 * @author Daniel C. Bordin
 * @since 2018-05-19
 */
@Repeatable(ExtensionQualifiers.class)
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface ExtensionQualifier {
    String value();
}
