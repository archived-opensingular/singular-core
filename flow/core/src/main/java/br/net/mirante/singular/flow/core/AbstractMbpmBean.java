package br.net.mirante.singular.flow.core;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.lang.StringUtils;

import br.net.mirante.singular.flow.util.view.Lnk;
import br.net.mirante.singular.flow.core.entity.IEntityProcessInstance;
import br.net.mirante.singular.flow.core.entity.persistence.IPersistenceService;
import br.net.mirante.singular.flow.schedule.IScheduleService;

public abstract class AbstractMbpmBean {

    protected void init() {

    }

    // ------- Método de recuperação de definições --------------------

    protected abstract ProcessDefinitionCache getCacheDefinicao();

    public <K extends ProcessDefinition<?>> K getProcessDefinition(Class<K> classe) {
        return ProcessDefinitionCache.getDefinition(classe);
    }

    public ProcessDefinition<?> getProcessDefinition(String sigla) {
        return getCacheDefinicao().getDefinition(sigla);
    }

    public List<ProcessDefinition<?>> getDefinicoes() {
        return getCacheDefinicao().getDefinitions();
    }

    private <T extends ProcessInstance> ProcessDefinition<?> getDefinicaoForInstanciaOrException(Class<T> classeIntancia) {
        ProcessDefinition<?> def = getCacheDefinicao().getDefinitionForInstance(classeIntancia);
        if (def == null) {
            throw new RuntimeException("Não existe definição de processo para '" + classeIntancia.getName() + "'");
        }
        return def;
    }

    // ------- Método de recuperação de instâncias --------------------

    private ProcessInstance getInstancia(Integer codDadosInstanciaProcesso) {
        IEntityProcessInstance dadosInstanciaProcesso = getPersistenceService().recuperarInstanciaPorCod(codDadosInstanciaProcesso);
        ProcessDefinition<?> def = getProcessDefinition(dadosInstanciaProcesso.getDefinicao().getSigla());
        return def.dadosToInstancia(dadosInstanciaProcesso);
    }

    public ProcessInstance getInstancia(IEntityProcessInstance dadosInstanciaProcesso) {
        return getInstancia(dadosInstanciaProcesso.getCod());
    }

    public final <T extends ProcessInstance> T getInstancia(Class<T> classeIntancia, Integer id) {
        return classeIntancia.cast(getDefinicaoForInstanciaOrException(classeIntancia).recuperarInstancia(id));
    }

    public final <T extends ProcessInstance> T getInstanciaOrException(Class<T> classeIntancia, String id) {
        T instancia = getInstancia(classeIntancia, id);
        if (instancia == null) {
            throw new RuntimeException("Não foi encontrada a instancia '" + id + "' do tipo " + classeIntancia.getName());
        }
        return instancia;
    }

    public final <T extends ProcessInstance> T getInstancia(Class<T> classeIntancia, String id) {
        if (StringUtils.isNumeric(id)) {
            return getInstancia(classeIntancia, Integer.parseInt(id));
        } else {
            return classeIntancia.cast(getInstancia(id));
        }
    }

    @SuppressWarnings("unchecked")
    public <X extends ProcessInstance> X getInstancia(String instanciaID) {
        if (instanciaID == null) {
            return null;
        }
        MapeamentoId mapeamento = parseId(instanciaID);
        if (mapeamento.sigla == null) {
            return (X) getInstancia(mapeamento.cod);
        } else {
            final ProcessDefinition<?> def = getProcessDefinition(mapeamento.sigla);
            if (def == null) {
                throw new RuntimeException("Não existe definição de processo '" + mapeamento.sigla + "'");
            }
            return (X) def.recuperarInstancia(mapeamento.cod);
        }
    }

    // ------- Manipulação de ID --------------------------------------

    protected abstract String generateID(ProcessInstance instancia);

    protected abstract String generateID(TaskInstance instanciaTarefa);

    protected abstract MapeamentoId parseId(String instanciaID);

    protected static class MapeamentoId {
        public final String sigla;
        public final Integer cod;

        public MapeamentoId(String sigla, int cod) {
            this.sigla = sigla;
            this.cod = cod;
        }
    }

    // ------- Geração de link ----------------------------------------

    public abstract Lnk getHrefPadrao(ProcessInstance instanciaProcesso);

    public abstract Lnk getHrefPadrao(TaskInstance instanciaTarefa);

    // ------- Manipulação de Usuário ---------------------------------

    public abstract MUser getUserSeDisponivel();

    public abstract boolean isPessoaAtivaParaTerTarefa(MUser user);

    protected abstract AbstractNotificadores getNotificadores();

    // ------- Consultas ----------------------------------------------

    public final List<ProcessDefinition<?>> getProcessosIniciaveis(MUser user) {
        return getDefinicoes().stream().filter(d -> d.isIniciavelPeloUsuario(user)).sorted().collect(Collectors.toList());
    }

    // ------- Outros -------------------------------------------------

    protected abstract IPersistenceService<?, ?, ?, ?, ?, ?> getPersistenceService();

    protected abstract IScheduleService getScheduleService();

    protected abstract void notifyStateUpdate(ProcessInstance instanciaProcessoMBPM);

    public final Object executeTask(MTaskJava task) {
        final ProcessDefinition<?> definicao = task.getFlowMap().getDefinicaoProcesso();
        final Collection<? extends ProcessInstance> instancias = definicao.getInstanciasNoEstado(task);
        if (task.isCalledInBlock()) {
            return task.executarByBloco(instancias);
        } else {
            for (final ProcessInstance instanciaProcessoMBPM : instancias) {
                EngineProcessamentoMBPM.executarTransicaoAgendada(task, instanciaProcessoMBPM);
            }
            return null;
        }
    }

}
