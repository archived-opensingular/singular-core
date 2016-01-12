package br.net.mirante.singular.form.mform.options;

import java.io.Serializable;
import java.util.Collection;

import br.net.mirante.singular.form.mform.MILista;
import br.net.mirante.singular.form.mform.MInstancia;
import br.net.mirante.singular.form.mform.MTipoSimples;

/**
 *  This interface represents the providers which will load options that
 *  populate the choices for a specific field. The provider is specified 
 *  during the declaration of a Type or Field by using the 
 *  {@link MTipoSimples#withSelectionFromProvider(String)} method.
 *
 */
public interface MOptionsProvider extends Serializable {

    public default String toDebug() {
        return null;
    }

    /**
     * Returns the list of options available for this selection, considering
     * also the old state of it.
     * 
     * @param optionsInstance : Current isntance used to select the options.
     * @return list of options from the expected {@link MInstancia} type.
     */
    default public MILista<? extends MInstancia> listAvailableOptions(
            MInstancia optionsInstance){
        MILista<? extends MInstancia> defaultOptions = listOptions(optionsInstance);
        checkForDanglingValues(optionsInstance, defaultOptions);
        return defaultOptions;
    }

    public default void checkForDanglingValues(MInstancia optionsInstance, MILista<? extends MInstancia> defaultOptions) {
        Object value = optionsInstance.getValor();
        if( value == null ) return;
        if( value instanceof Collection && ((Collection<?>)value).isEmpty()) return;
        addNotPresentElement(optionsInstance, defaultOptions);
    }

    public default void addNotPresentElement(MInstancia optionsInstance, 
            MILista<? extends MInstancia> defaultOptions) {
        if(optionsInstance instanceof MILista){
            addNotPresentElementsOfList(optionsInstance, defaultOptions);
        } else{
            addNotPresentElement(defaultOptions, optionsInstance);
        }
    }

    @SuppressWarnings("rawtypes")
    public default void addNotPresentElementsOfList(MInstancia optionsInstance, 
            MILista<? extends MInstancia> defaultOptions) {
        MILista selected = (MILista) optionsInstance;
        for(MInstancia subValue : selected.getAllChildren()){
            addNotPresentElement(defaultOptions, subValue);
        }
    }

    public default void addNotPresentElement(
            MILista<? extends MInstancia> defaultOptions, MInstancia subValue) {
        if(!containsValue(defaultOptions, subValue)){
            addNewValueUpfront(defaultOptions, subValue);
        }
    }

    public default boolean containsValue(
            MILista<? extends MInstancia> defaultOptions, MInstancia value) {
        if(value instanceof MSelectionableInstance){
            MSelectionableInstance item = (MSelectionableInstance) value;
            if(((MSelectionableInstance) value).getSelectValue() == null){
                return true;
            }
        }
        for(MInstancia c : defaultOptions.getAllChildren()){
           if (value.equals(c)){
               return true;
           }
        }
        return false;
    }
    
    public default void addNewValueUpfront(MILista<? extends MInstancia> defaultOptions, MInstancia value) {
        defaultOptions.addElementAt(0, value);
//        MSelectionableInstance newValue = (MSelectionableInstance)defaultOptions.addNovoAt(0);
//        MSelectionableInstance currentValue = (MSelectionableInstance)value;
//        newValue.set
//        ((MSelectionableInstance)newValue).setValueSelectLabel(value.getSelectValue(), value.getDescricao);
    }
    
    /**
     * Returns the list of options for this selection.
     * 
     * @param optionsInstance : Current isntance used to select the options.
     * @return list of options from the expected {@link MInstancia} type.
     */
    public MILista<? extends MInstancia> listOptions(MInstancia optionsInstance);
}
