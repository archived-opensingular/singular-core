package br.net.mirante.singular.flow.util.vars.types;

import br.net.mirante.singular.flow.util.vars.VarDefinition;
import br.net.mirante.singular.flow.util.vars.VarInstance;
import br.net.mirante.singular.flow.util.vars.VarType;

import java.text.SimpleDateFormat;
import java.util.Date;

public class VarTypeDate implements VarType {

    private SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
    private SimpleDateFormat timeFormatter = new SimpleDateFormat("dd/MM/yyyy HH:mm");

    @Override
    public String getName() {
        return getClass().getSimpleName();
    }

    @Override
    public String toDisplayString(VarInstance varInstance) {
        return toDisplayString(varInstance.getValor(), varInstance.getDefinicao());
    }

    @Override
    public String toDisplayString(Object valor, VarDefinition varDefinition) {
        Date date = (Date) valor;
        if (new SimpleDateFormat("hh:mm:ss").format(date).equals("00:00:00")) {
            return formatter.format(date);
        } else {
            return timeFormatter.format(date);
        }
    }

    @Override
    public String toPersistenceString(VarInstance varInstance) {
        return Integer.toString((Integer) varInstance.getValor());
    }


}
