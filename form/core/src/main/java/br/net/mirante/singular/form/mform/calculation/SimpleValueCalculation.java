package br.net.mirante.singular.form.mform.calculation;

@FunctionalInterface
public interface SimpleValueCalculation<RESULT> {

    public RESULT calculate(CalculationContext context);
}
