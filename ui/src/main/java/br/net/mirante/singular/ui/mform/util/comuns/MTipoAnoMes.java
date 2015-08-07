package br.net.mirante.singular.ui.mform.util.comuns;

import java.time.YearMonth;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import org.apache.commons.lang.StringUtils;

import br.net.mirante.singular.ui.mform.MFormTipo;
import br.net.mirante.singular.ui.mform.MTipoSimples;

@MFormTipo(nome = "AnoMes", pacote = MPacoteUtil.class)
public class MTipoAnoMes extends MTipoSimples<MIAnoMes, YearMonth> {

    public MTipoAnoMes() {
        super(MIAnoMes.class, YearMonth.class);
    }

    protected MTipoAnoMes(Class<? extends MIAnoMes> classeInstancia) {
        super(classeInstancia, YearMonth.class);
    }

    @Override
    public YearMonth converterNaoNativoNaoString(Object valor) {
        if (valor instanceof Integer) {
            return converterFromInteger(((Integer) valor).intValue());
        } else if (valor instanceof Date) {
            Calendar cal = new GregorianCalendar();
            cal.setTime((Date) valor);
            return YearMonth.of(cal.get(Calendar.YEAR), cal.get(Calendar.MONTH) + 1);
        } else if (valor instanceof Calendar) {
            Calendar cal = (Calendar) valor;
            return YearMonth.of(cal.get(Calendar.YEAR), cal.get(Calendar.MONTH) + 1);
        }
        throw createErroConversao(valor);
    }

    private YearMonth converterFromInteger(int valor) {
        int mes = valor % 100;
        int ano = valor / 100;
        if (ano < 1 || ano > 12) {
            throw createErroConversao(valor, YearMonth.class, "Não representa um mês válido (entre 1 e 12)", null);
        }
        return YearMonth.of(ano, mes);
    }

    @Override
    public YearMonth fromString(String valor) {
        valor = StringUtils.trimToNull(valor);
        if (valor == null) {
            return null;
        }
        try {
            int pos = valor.indexOf('/');
            if (pos != -1 && pos != 0 && pos != valor.length() - 1) {
                String mes = valor.substring(0, pos);
                String ano = valor.substring(pos + 1);
                return YearMonth.of(Integer.parseInt(ano), Integer.parseInt(mes));
            }

            return converterFromInteger(Integer.parseInt(valor));
        } catch (Exception e) {
            throw createErroConversao(valor, Integer.class, null, e);
        }
    }

    @Override
    protected String toStringPersistencia(YearMonth valorOriginal) {
        if (valorOriginal == null) {
            return null;
        }
        return Integer.toString(valorOriginal.getYear() * 100 + valorOriginal.getMonthValue());
    }

    @Override
    public String toStringDisplay(YearMonth valor) {
        if (valor == null) {
            return null;
        }
        YearMonth anoMes = converter(valor);
        StringBuilder sb = new StringBuilder(7);
        if (anoMes.getMonthValue() < 10) {
            sb.append('0');
        }
        sb.append(anoMes.getMonthValue()).append('/');
        int ano = anoMes.getYear();
        if (ano > 0 && ano < 1000) {
            sb.append('0');
            if (ano < 100) {
                sb.append('0');
                if (ano < 10) {
                    sb.append('0');
                }
            }
        }
        sb.append(ano);
        return sb.toString();
    }
}
