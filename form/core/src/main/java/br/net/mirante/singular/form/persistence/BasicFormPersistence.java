package br.net.mirante.singular.form.persistence;

import br.net.mirante.singular.form.SInstance;

import java.util.List;
import java.util.Optional;

/**
 * Serviço com as operações básicas de persistência de formulário, mas sem as funções de recuperação e listagem.
 *
 * @author Daniel C. Bordin
 */
public interface BasicFormPersistence<INSTANCE extends SInstance>  {

    /**
     * Converte uma string representando a chave para o obejto de chave utilizado pela persitência. Dispara exception se
     * a String não for compatível com o tipo de chave da persistência. <p>Esse metodo seria tipicamente usado para
     * converter chave passadas por parâmetro (por exemplo na URL) de volta a FormKey.</p>
     */
    public FormKey keyFromString(String persistenceString);

    /**
     * Tenta converter o valor para o tipo de FormKey utlizado pela FormPersitente. Se o tipo não for uma representação
     * de chave entendível pela persitencia atual, então dispara uma exception.
     *
     * @return null se o valor for null
     */
    public FormKey keyFromObject(Object objectValueToBeConverted);

    /**
     * Insere uma instância nova e devolve a chave do novo registro.
     *
     * @return Nunca Null
     */
    public FormKey insert(INSTANCE instance, Integer inclusionActor);

    /**
     * Apaga a instância correspondente a chave informada.
     */
    public void delete(FormKey key);

    /**
     * Atualiza a instância na base de dados, com base no atributo FormmKey contido na instância informada.
     *
     * @param instance A mesma deverá conter o atributo FormKey, para tanto deverá ter sido recuperada pela própria
     *                 persitência.
     */
    public void update(INSTANCE instance, Integer inclusionActor);

    /**
     * Atualiza ou insere a instância de acordo se a mesma ja tiver ou não um FormKey associado (como atributo da instância).
     * @return Chave da instância criada ou atualizada.
     */
    public FormKey insertOrUpdate(INSTANCE instance, Integer inclusionActor);


    /**
     * Informa se a SInstance passada por parâmetro possui uma chave associada.
     * Caso contrário é considerado um formulário não persistence
     * @param instance
     * @return
     */
    public boolean isPersistent(INSTANCE instance);

    /**
     * Salva as alterações na versão atual e incrementa versão do formulário
     * e replica as anotações em suas versões iniciais
     * @param instance
     * @return
     */
    public default FormKey newVersion(INSTANCE instance, Integer inclusionActor){
        return newVersion(instance, inclusionActor, true);
    }

    /**
     * Salva as alterações na versão atual e incrementa versão do formulário
     * e das anotações vinculadas
     * @param instance
     * @return
     */
    public FormKey newVersion(INSTANCE instance, Integer inclusionActor, boolean keepAnnotations);
}
