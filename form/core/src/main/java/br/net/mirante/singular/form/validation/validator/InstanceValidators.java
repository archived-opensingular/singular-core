package br.net.mirante.singular.form.validation.validator;

import br.net.mirante.singular.form.validation.IInstanceValidator;

/**
 * Coleção de {@link IInstanceValidator}
 */
public final class InstanceValidators {

    private InstanceValidators() {
    }
    
    /**
     * Verifica se todos ou nenhum campo foi preenchido
     */
    public static AllOrNothingInstanceValidator allOrNothing(){
        return AllOrNothingInstanceValidator.INSTANCE;
    }

    /**
     * Verifica se o CEP é válido
     */
    public static MCEPValidator cep(){
        return MCEPValidator.INSTANCE;
    }

    /**
     * Verifica se o CNPJ é válido
     */
    public static MCNPJValidator cnpj(){
        return MCNPJValidator.INSTANCE;
    }

    /**
     * Verifica se o CPF é válido
     */
    public static MCPFValidator cpf(){
        return MCPFValidator.INSTANCE;
    }

    /**
     * Verifica se o email é válido
     */
    public static MEmailValidator email(){
        return MEmailValidator.INSTANCE;
    }

    /**
     * Verifica se o email é válido. Permite informar endereço local.
     */
    public static MEmailValidator emailLocalAddress(){
        return MEmailValidator.INSTANCE_ALLOW_LOCAL_ADDRESS;
    }
}
