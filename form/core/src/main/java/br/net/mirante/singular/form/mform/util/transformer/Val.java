package br.net.mirante.singular.form.mform.util.transformer;

import br.net.mirante.singular.form.mform.MIComposto;
import br.net.mirante.singular.form.mform.MILista;
import br.net.mirante.singular.form.mform.MISimples;
import br.net.mirante.singular.form.mform.MInstancia;
import br.net.mirante.singular.form.mform.MTipo;
import br.net.mirante.singular.form.mform.MTipoComposto;
import br.net.mirante.singular.form.mform.MTipoLista;
import br.net.mirante.singular.form.mform.MTipoSimples;
import br.net.mirante.singular.form.mform.SingularFormException;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Essa classe utilitaria realiza uma serie de operacoes sobre os valores guardados pelos MTIpos
 */
public class Val {

    private MInstancia instancia;

    public Val(MInstancia instancia) {
        this.instancia = instancia;
    }

    private static MInstancia getInstance(MInstancia instancia, MTipo target) {
        return (MInstancia) instancia.findNearest(target).orElse(null);
    }

    /**
     * @param current instancia a partir da qual será buscada a instancia mais proxima do tipo simples tipo
     * @param tipo    um tipo simples
     * @param <T>
     * @return false se o valor do tipo simples for nulo ou se o tipo não for encontrado a partir da instancia
     * current informada
     */
    public static <T> boolean notNull(MInstancia current, MTipoSimples<? extends MISimples<T>, T> tipo) {
        return Val.of(current, tipo) != null;
    }

    public static <T> boolean notNull(MInstancia current, MTipoComposto tipo) {
        MIComposto targetInstance = (MIComposto) getInstance(current, tipo);
        return Val.notNull(targetInstance);
    }


    public static <T> boolean notNull(MInstancia current, MTipoLista tipo) {
        MILista instanciaLista = (MILista) getInstance(current, tipo);
        return Val.notNull(instanciaLista);
    }

    public static <T> boolean notNull(MILista instanciaLista) {
        return instanciaLista != null && !instanciaLista.isEmpty();
    }

    public static <T> boolean notNull(MIComposto instanciaComposta) {
        return instanciaComposta != null && !instanciaComposta.isEmptyOfData();
    }

    public static <T> boolean notNull(MISimples instanciaSimples) {
        return instanciaSimples != null && !instanciaSimples.isEmptyOfData();
    }

    public static <T> T of(MISimples<?> instanciaSinmples) {
        return (T) instanciaSinmples.getValor();
    }

    public static <T> boolean notNull(MInstancia instancia) {
        if (instancia instanceof MIComposto) {
            return Val.notNull((MIComposto) instancia);
        } else if (instancia instanceof MISimples) {
            return Val.notNull((MISimples) instancia);
        } else if (instancia instanceof MILista) {
            return Val.notNull((MILista) instancia);
        } else {
            throw new SingularFormException("Tipo de instancia não suportado");
        }
    }

    public static <T> T of(MInstancia instancia, MTipoSimples<? extends MISimples<T>, T> tipo) {
        MISimples targetInstance = (MISimples) getInstance(instancia, tipo);
        if (targetInstance != null) {
            return (T) Val.of(targetInstance);
        }
        return null;
    }

    /**
     * Configura os valores contidos em value
     * na MInstancia passara como parametro recursivamente.
     * Usualmente value é o retorno do metodo dehydrate.
     * @param instancia
     * @param value
     */
    public static void hydrate(MInstancia instancia, Object value) {
        if (instancia instanceof MIComposto) {
            fromMap((Map<String, Object>) value, (MIComposto) instancia);
        } else if (instancia instanceof MISimples) {
            ((MISimples) instancia).setValor(value);
        } else if (instancia instanceof MILista) {
            fromList((List<Object>) value, (MILista) instancia);
        } else {
            throw new SingularFormException("Tipo de instancia não suportado");
        }
    }

    private static void fromMap(Map<String, Object> map, MIComposto instancia) {
        for (Map.Entry<String, Object> entry : map.entrySet()) {
            hydrate(instancia.getCampo(entry.getKey()), entry.getValue());
        }
    }

    private static void fromList(List<Object> list, MILista miLista) {
        for (Object o : list) {
            MInstancia novo = miLista.addNovo();
            hydrate(novo, o);
        }
    }

    /**
     * Extrai para objetos serializáveis todos
     * os dados de uma MIinstancia recursivamente
     *
     * @param value
     *  MIinstancia a partir da qual se deseja extrair os dados
     * @return
     *  Objetos serializáveis representando os dados da MInstancia
     */
    public static Object dehydrate(MInstancia value) {
        if (value instanceof MIComposto) {
            Map<String, Object> map = new LinkedHashMap<>();
            toMap(map, (MInstancia) value);
            return map;
        } else if (value instanceof MISimples) {
            return ((MISimples) value).getValor();
        } else if (value instanceof MILista) {
            List<Object> list = new ArrayList<>();
            toList(list, (MInstancia) value);
            return list;
        } else  {
            throw new SingularFormException("Tipo de instancia não suportado");
        }
    }

    /**
     * Remove um espiríto maligno (valores serializaveis) de um corpo puro e inocente (MIinstancia)
     * e de toda sua descendência.
     * @param innocentVessel
     * @return
     */
    public static Soul exorcize(MInstancia innocentVessel){
        Soul s = new Soul();
        s.value = dehydrate(innocentVessel);
        return s;
    }

    /**
     * Realiza um ritual para encarnar um espirito maligno em um pobre corpo inocente.
     * @param pureVessel
     *  A pobre vitma do ritual
     * @param evilSpirit
     *  A alma do espírito realmente extraída a partir do método exorcize
     */
    public static void possess(MInstancia pureVessel, Soul evilSpirit) {
        hydrate(pureVessel, evilSpirit.value);
    }

    private static void toMap(Map<String, Object> value, MInstancia instancia) {
        if (instancia instanceof MIComposto) {
            MIComposto item = (MIComposto) instancia;
            for (MInstancia i : item.getAllChildren()) {
                value.put(i.getNome(), dehydrate(i));
            }
        }
    }

    private static void toList(List<Object> value, MInstancia instancia) {
        if (instancia instanceof MILista<?>) {
            for (MInstancia i : ((MILista<?>) instancia).getValores()) {
                value.add(dehydrate(i));
            }
        }
    }

    public <T> T of(MTipoSimples<? extends MISimples<T>, T> tipo) {
        return Val.of(instancia, tipo);
    }

    public <T> boolean notNull(MTipoSimples<? extends MISimples<T>, T> tipo) {
        return Val.notNull(instancia, tipo);
    }

    public static class Soul {
        private Object value;
    }

}
