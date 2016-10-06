package org.opensingular.singular.form.wicket.component;

import org.opensingular.form.SInstance;
import org.opensingular.singular.form.wicket.behavior.AjaxUpdateSingularFormComponentPanel;
import org.apache.wicket.Component;
import org.apache.wicket.behavior.Behavior;
import org.apache.wicket.markup.html.form.FormComponentPanel;
import org.apache.wicket.model.IModel;

/**
 * Extensão do form component panel para se adequar ao modelo de ajax do singular.
 * A subclasses desta tem por obrigação definir como serão enviados os dados de seus subcomponentes via
 * ajax e fazer todo o processamento necessário das requições ajax e de submit.
 * Note que talvez seja preciso manipular o funcionamento dos métodos
 * {@link FormComponentPanel#updateModel()}, {@link FormComponentPanel#convertInput()}, {@link FormComponentPanel#processInput()}, {@link FormComponentPanel#processChildren()}
 * para alcançar o resultado desejado.
 *
 *
 * @param <T>
 * @param <TYPE>
 */
public abstract class SingularFormComponentPanel<T, TYPE> extends FormComponentPanel<T> {

    public SingularFormComponentPanel(String id) {
        super(id);
    }

    public SingularFormComponentPanel(String id, IModel<T> model) {
        super(id, model);
    }

    @SuppressWarnings("unchecked")
    @Override
    public Component add(Behavior... behaviors) {
        for (Behavior b : behaviors) {
            if (b instanceof AjaxUpdateSingularFormComponentPanel) {
                AjaxUpdateSingularFormComponentPanel<TYPE> bh = (AjaxUpdateSingularFormComponentPanel<TYPE>) b;
                bh.setType(configureAjaxBehavior(bh, AjaxUpdateSingularFormComponentPanel.VALUE_REQUEST_PARAMETER_NAME));
                bh.setValueModelResolver(this::ajaxValueToModel);
            }
        }
        return super.add(behaviors);
    }

    /**
     * Permite ao componente configurar de maneira arbitrária como serão retornados os valores ajas
     * dos eventos associados a este.*
     * @param behavior
     *  Behavior default a ser configurado para envio das informações do componente composto
     * @param requestParameterName
     *  Nome do parâmetro que será recuperado nos envios das requisições ajax e enviado para processamento através
     *  do método {@link #ajaxValueToModel(Object, IModel)}
     * @return
     */
    public abstract Class<TYPE> configureAjaxBehavior(AjaxUpdateSingularFormComponentPanel<TYPE> behavior, String requestParameterName);

    /**
     * É responsável por receber o valor ajax, converter e atualizar as instancias do singular.
     * @param value
     *  Valore recebido na requisição ajax.
     * @param instanceModel
     *  Model da instancia fornecido pelo singular.
     */
    public abstract void ajaxValueToModel(TYPE value, IModel<SInstance> instanceModel);

}
