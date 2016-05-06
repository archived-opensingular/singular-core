package br.net.mirante.singular.form.calculation;

@FunctionalInterface
public interface SimpleValueCalculation<RESULT> {

    public RESULT calculate(CalculationContext context);
}
