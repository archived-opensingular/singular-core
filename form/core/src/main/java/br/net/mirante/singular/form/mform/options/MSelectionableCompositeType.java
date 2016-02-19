package br.net.mirante.singular.form.mform.options;

import br.net.mirante.singular.form.mform.SType;
import br.net.mirante.singular.form.mform.STypeSimple;

@SuppressWarnings({"rawtypes", "unchecked"})
public interface MSelectionableCompositeType<BASE extends SType> extends MSelectionableType<BASE> {

    /**
     * Monta um campo de seleção a partir de um tipo composto utilizando
     * um provider de MILista de MTipoComposto e utiliza o MTipoSimples label filho imediato do
     * tipo composto como label
     *
     * @param label    MTipoSimples filho do composto que será utilizado como label.
     * @param provider
     * @return
     */
    default public BASE withSelectionFromProvider(STypeSimple label, MOptionsCompositeProvider provider) {
        this.setSelectLabel(label.getSimpleName());
        this.setProviderOpcoes(provider);
        return (BASE) this;
    }

    /**
     * Monta um campo de seleção a partir de um tipo composto utilizando
     * um provider de MILista de MTipoComposto e utiliza o pathtoLabel como caminho para um MTipoSimples filho imediato do
     * tipo composto para identificar o label
     *
     * @param pathTolabel
     * @param provider
     * @return
     */
    default public BASE withSelectionFromProvider(String pathTolabel, MOptionsCompositeProvider provider) {
        this.setSelectLabel(pathTolabel);
        this.setProviderOpcoes(provider);
        return (BASE) this;
    }

    default public BASE withSelectionFromProvider(STypeSimple label, MOptionsProvider provider) {
        this.setSelectLabel(label.getSimpleName());
        this.setProviderOpcoes(provider);
        return (BASE) this;
    }

    default public BASE withSelectionFromProvider(String pathTolabel, MOptionsProvider provider) {
        this.setSelectLabel(pathTolabel);
        this.setProviderOpcoes(provider);
        return (BASE) this;
    }

}
