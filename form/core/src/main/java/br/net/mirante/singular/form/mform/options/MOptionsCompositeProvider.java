package br.net.mirante.singular.form.mform.options;

import br.net.mirante.singular.form.mform.MILista;
import br.net.mirante.singular.form.mform.MInstancia;
import br.net.mirante.singular.form.mform.MTipo;
import br.net.mirante.singular.form.mform.MTipoComposto;
import br.net.mirante.singular.form.mform.util.transformer.MListaBuilder;

/**
 * Interface funcional para prover lambda de montagem de MIlista de MTipoComposto
 * a partir de um ListBuilder
 */
@FunctionalInterface
public interface MOptionsCompositeProvider extends MOptionsProvider {


    /**
     * Returns the list of options for this selection.
     *
     * @param optionsInstance : Current isntance used to select the options.
     * @return list of options from the expected {@link MInstancia} type.
     */
    @Override
    public default MILista<? extends MInstancia> listOptions(MInstancia optionsInstance) {
        MTipo<?> tipo;
        if (optionsInstance instanceof MILista){
            tipo = ((MILista) optionsInstance).getTipoElementos();
        } else {
            tipo = optionsInstance.getMTipo();
        }
        MListaBuilder<MTipoComposto> lb = new MListaBuilder<>((MTipoComposto)tipo);
        listOptions(optionsInstance, lb);
        return lb.getList();
    }

    /**
     * Método para montar uma MLista a partir do MListaBuilder
     *
     * @param instancia
     * @param lb
     * @return
     */
    public void listOptions(MInstancia instancia, MListaBuilder<MTipoComposto> lb);


}
