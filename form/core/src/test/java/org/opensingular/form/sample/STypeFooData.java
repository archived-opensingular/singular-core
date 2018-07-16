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

package org.opensingular.form.sample;

import org.opensingular.form.SInfoType;
import org.opensingular.form.TypeBuilder;
import org.opensingular.form.type.core.STypeString;
import org.opensingular.form.type.core.STypeTime;

import java.util.Calendar;

@SInfoType(spackage = FormTestPackage.class,  name = "PortoLocalHorario")
public class STypeFooData extends StypeFooDataLocation {

    
    public STypeString diaSemana;
    public STypeTime   horario;

    @Override
    protected void onLoadType(TypeBuilder tb) {

        localAtracacao.asAtr().label("Local/Terminal Hidroviário");
        localAtracacao.asAtrBootstrap().colPreference(3);

        diaSemana = addFieldString("diaSemana");
        diaSemana.selectionOf("Segunda-feira", "Terça-feira", "Quarta-feira", "Quinta-feira", "Sexta-feira", "Sábado", "Domingo");
        diaSemana.asAtr().label("Dia da semana");
        diaSemana.asAtrBootstrap().colPreference(2);

        horario = addField("horario", STypeTime.class);
        horario.asAtr().label("Horário");
        horario.asAtrBootstrap().colPreference(1);
        Calendar cal = Calendar.getInstance();
        cal.set(0, 0, 0, 0, 0,0);
        horario.setInitialValue(cal.getTime());
    }
    
    
}
