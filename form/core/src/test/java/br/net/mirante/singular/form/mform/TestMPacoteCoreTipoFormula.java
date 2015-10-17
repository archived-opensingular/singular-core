package br.net.mirante.singular.form.mform;

import junit.framework.TestCase;

public class TestMPacoteCoreTipoFormula extends TestCase {
    public void testEmpty() {}
    /*
     * public void testCodeSimple() { MDicionario dicionario =
     * MDicionario.create(); PacoteBuilder pb =
     * dicionario.criarNovoPacote("teste");
     *
     * MTipoComposto<?> tipoCampos = pb.createTipoComposto("campos");
     * MTipoInteger tipoQtd = tipoCampos.addCampoInteger("qtd"); MTipoCode
     * tipoCalc = tipoCampos.addCode("calc", Supplier.class);
     *
     * MIComposto campos = tipoCampos.novaInstancia();
     *
     * assertNull(campos.getValor("calc"));
     *
     * campos.setValor("calc", 10); assertNull(campos.getValor("calc"));
     *
     * campos.setValor("calc", (Supplier<Integer>) () -> 10);
     * assertNotNull(campos.getValor("calc"));
     * assertTrue(campos.getValor("calc") instanceof Supplier);
     * assertEquals(campos.getValor("calc", Supplier.class).get(), 10); } public
     * void testMetodoSimples() { MDicionario dicionario = MDicionario.create();
     * PacoteBuilder pb = dicionario.criarNovoPacote("teste");
     *
     * MTipoComposto<?> tipoCampos = pb.createTipoComposto("campos");
     * MTipoInteger tipoQtd = tipoCampos.addCampoInteger("qtd"); MTipoCode
     * tipoCalc = tipoCampos.addMetodo("isNegativo", Boolean.class,
     * (Supplier<Boolean>) () -> false);
     *
     * MIComposto campos = tipoCampos.novaInstancia();
     *
     * assertNull(campos.getValor("isNegativo"));
     *
     * campos.setValor("calc", 10); assertNull(campos.getValor("calc"));
     *
     * assertNotNull(campos.getValor("calc"));
     * assertTrue(campos.getValor("calc") instanceof Supplier);
     * assertEquals(campos.getValor("calc", Supplier.class).get(), 10); }
     *
     * public void testBasicFunction() { MDicionario dicionario =
     * MDicionario.create(); PacoteBuilder pb =
     * dicionario.criarNovoPacote("teste");
     *
     * MTipoComposto<?> tipoCampos = pb.createTipoComposto("campos");
     * MTipoInteger tipoSimples = tipoCampos.addCampoInteger("simples");
     *
     * pb.debug();
     *
     * tipoSimples.asFormula().set(() -> 1 * 2); // tipoSimples.withFormula(()
     * -> 1 * 2); // tipoSimples.withFormulaJS("1 * 2");
     *
     * // tipoSimples.withValorInicial(valor)
     *
     * MIComposto campos = tipoCampos.novaInstancia(); campos.debug();
     * assertEquals(2, campos.getValor("simples")); }
     */
}
