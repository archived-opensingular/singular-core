package org.opensingular.form.sample;

import org.opensingular.form.SInfoType;
import org.opensingular.form.TypeBuilder;
import org.opensingular.form.type.core.STypeString;
import org.opensingular.form.type.core.STypeTime;

import java.util.Calendar;

@SInfoType(spackage = AntaqPackage.class, newable = false, name = "PortoLocalHorario")
public class STypePortoLocalHorario extends STypePortoLocal {

    
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
