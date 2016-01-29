package br.net.mirante.singular.form.mform.core;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.google.common.base.Strings;
import com.google.common.base.Throwables;

import br.net.mirante.singular.form.mform.MInfoTipo;
import br.net.mirante.singular.form.mform.STypeSimples;

@MInfoTipo(nome = "Data", pacote = SPackageCore.class)
public class STypeData extends STypeSimples<SIData, Date> {
    private static final Logger LOGGER = Logger.getLogger(SIData.class.getName());
    
    public static final String FORMAT = "dd/MM/yyyy";

    public STypeData() {
        super(SIData.class, Date.class);
    }

    protected STypeData(Class<? extends SIData> classeInstancia) {
        super(classeInstancia, Date.class);
    }

    public Date fromString(String valor) {
        try {
            if(Strings.isNullOrEmpty(valor)) return null;
            return (new SimpleDateFormat(STypeData.FORMAT)).parse(valor);
        } catch (ParseException e) {
            String msg = String.format("Can't parse value '%s' with format '%s'.", valor, STypeData.FORMAT);
            LOGGER.log(Level.WARNING, msg, e);
            throw Throwables.propagate(e);
        }
    }
    
    @Override
    protected String toStringPersistencia(Date valorOriginal) {
        return (new SimpleDateFormat(STypeData.FORMAT)).format(valorOriginal);
    }
}
