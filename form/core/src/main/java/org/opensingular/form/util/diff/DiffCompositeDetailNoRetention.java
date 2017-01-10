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

package org.opensingular.form.util.diff;

import org.opensingular.form.SPackage;
import org.opensingular.form.SType;
import org.opensingular.form.STypeComposite;
import org.opensingular.form.view.SView;

import java.lang.annotation.*;

/**
 * A classes derivadas de {@link SView} e que forem aplicadas esta anotação, implica que
 * quando um tipo de {@link STypeComposite} usar essa view, então o composite não terá o diff detalhando para os seus
 * sub campos no caso de resumo. Essa marcação é útil no caso de um composite que representa apenas uma única
 * informação. Por exemplo, tipo produto com código e descrição. Nesse caso, o usuário precisa apenas saber que o tipo
 * do produto foi alterado e não campo a campo.
 *
 * @author Daniel C. Bordin
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Documented
public @interface DiffCompositeDetailNoRetention {

}
