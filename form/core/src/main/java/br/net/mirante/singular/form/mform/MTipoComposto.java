package br.net.mirante.singular.form.mform;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import br.net.mirante.singular.form.mform.core.MPacoteCore;
import br.net.mirante.singular.form.mform.core.MTipoBoolean;
import br.net.mirante.singular.form.mform.core.MTipoData;
import br.net.mirante.singular.form.mform.core.MTipoInteger;
import br.net.mirante.singular.form.mform.core.MTipoString;

@MInfoTipo(nome = "MTipoComposto", pacote = MPacoteCore.class)
public class MTipoComposto<TIPO_INSTANCIA extends MIComposto> extends MTipo<TIPO_INSTANCIA> {

    private Map<String, MTipo<?>> fieldsLocal;

    private transient FieldMapOfRecordType fieldsConsolidated;

    @SuppressWarnings("unchecked")
    public MTipoComposto() {
        super((Class<? extends TIPO_INSTANCIA>) MIComposto.class);
    }

    protected MTipoComposto(Class<TIPO_INSTANCIA> classeInstancia) {
        super(classeInstancia);
    }

    private <I extends MInstancia, T extends MTipo<I>> T addInterno(String localName, T type) {
        if (fieldsLocal == null) {
            fieldsLocal = new LinkedHashMap<>();
        }
        fieldsConsolidated = null;

        fieldsLocal.put(localName, type);
        return type;
    }

    final FieldMapOfRecordType getFieldsConsolidated() {
        if (fieldsConsolidated == null) {
            if (fieldsLocal == null) {
                if (getSuperTipo() != null && getSuperTipo() instanceof MTipoComposto) {
                    // Busca reaproveitar, pois muitas extensões são locais e
                    // não acrescentam campso
                    fieldsConsolidated = ((MTipoComposto<?>) getSuperTipo()).getFieldsConsolidated();
                } else {
                    fieldsConsolidated = new FieldMapOfRecordType();
                }
            } else {
                fieldsConsolidated = new FieldMapOfRecordType();
                if (getSuperTipo() != null && getSuperTipo() instanceof MTipoComposto) {
                    fieldsConsolidated.addAll(((MTipoComposto<?>) getSuperTipo()).getFieldsConsolidated());
                }
                fieldsConsolidated.addAll(fieldsLocal);
            }
        }
        return fieldsConsolidated;
    }

    public <I extends MInstancia, T extends MTipo<I>> T addCampo(Class<T> classeTipo) {
        T tipo = resolverTipo(classeTipo);
        return extenderTipo(tipo.getNomeSimples(), tipo);
    }

    public <I extends MInstancia, T extends MTipo<I>> T addCampo(String nomeCampo, Class<T> tipo, boolean obrigatorio) {
        T novo = addCampo(nomeCampo, tipo);
        novo.withObrigatorio(obrigatorio);
        return novo;
    }

    public <I extends MInstancia, T extends MTipo<I>> T addCampo(String nomeCampo, Class<T> classeTipo) {
        T novo = extenderTipo(nomeCampo, classeTipo);
        return addInterno(nomeCampo, novo);
    }

    public <T extends MTipo<I>, I extends MInstancia> MTipoLista<T, I> addCampoListaOf(String nomeSimplesNovoTipo, Class<T> classeTipoLista) {
        T tipo = resolverTipo(classeTipoLista);
        MTipoLista<T, I> novo = createTipoListaOf(nomeSimplesNovoTipo, tipo);
        return addInterno(nomeSimplesNovoTipo, novo);
    }

    public <T extends MTipo<I>, I extends MInstancia> MTipoLista<T, I> addCampoListaOf(String nomeCampo, T tipoElementos) {
        MTipoLista<T, I> novo = createTipoListaOf(nomeCampo, tipoElementos);
        return addInterno(nomeCampo, novo);
    }

    public <I extends MIComposto> MTipoLista<MTipoComposto<I>, I> addCampoListaOfComposto(String nomeCampo, String nomeNovoTipoComposto) {
        MTipoLista<MTipoComposto<I>, I> novo = createTipoListaOfNovoTipoComposto(nomeCampo, nomeNovoTipoComposto);
        return addInterno(nomeCampo, novo);
    }

    public MTipo<?> getCampo(String nomeCampo) {
        return getFieldsConsolidated().get(nomeCampo);
    }

    public Collection<MTipo<?>> getFields() {
        return getFieldsConsolidated().getFields();
    }

    public Collection<MTipo<?>> getFieldsLocal() {
        return (fieldsLocal == null) ? Collections.emptyList() : fieldsLocal.values();

    }

    @Deprecated
    public Set<String> getCampos() {
        return getFields().stream().map(f -> f.getNome()).collect(Collectors.toSet());
    }

    // --------------------------------------------------------------------------
    // Atalhos de conveniência
    // --------------------------------------------------------------------------

    public MTipoComposto<?> addCampoComposto(String nomeCampo) {
        return addCampo(nomeCampo, MTipoComposto.class);
    }

    public MTipoString addCampoString(String nomeCampo) {
        return addCampo(nomeCampo, MTipoString.class);
    }

    public MTipoString addCampoString(String nomeCampo, boolean obrigatorio) {
        return addCampo(nomeCampo, MTipoString.class, obrigatorio);
    }

    public MTipoData addCampoData(String nomeCampo) {
        return addCampo(nomeCampo, MTipoData.class);
    }

    public MTipoData addCampoData(String nomeCampo, boolean obrigatorio) {
        return addCampo(nomeCampo, MTipoData.class, obrigatorio);
    }

    public MTipoBoolean addCampoBoolean(String nomeCampo) {
        return addCampo(nomeCampo, MTipoBoolean.class);
    }

    public MTipoBoolean addCampoBoolean(String nomeCampo, boolean obrigatorio) {
        return addCampo(nomeCampo, MTipoBoolean.class, obrigatorio);
    }

    public MTipoInteger addCampoInteger(String nomeCampo) {
        return addCampo(nomeCampo, MTipoInteger.class);
    }

    public MTipoInteger addCampoInteger(String nomeCampo, boolean obrigatorio) {
        return addCampo(nomeCampo, MTipoInteger.class, obrigatorio);
    }

    /**
     * Mapa de alto nível que funciona tanto por nome quanto por índice do campo
     * ordenado. Otimiza a performance e mantem a ordem original da criação dos
     * campos.
     */
    final static class FieldMapOfRecordType {

        private LinkedHashMap<String, FieldRef> fields;

        private List<MTipo<?>> fieldsList;

        public int size() {
            return (fields == null) ? 0 : fields.size();
        }

        public boolean isEmpty() {
            return (fields == null) || fields.isEmpty();
        }

        public List<MTipo<?>> getFields() {
            return (fields == null) ? Collections.emptyList() : garantirLista();
        }

        public MTipo<?> get(String fieldName) {
            if (fields != null) {
                FieldRef fr = fields.get(fieldName);
                if (fr != null) {
                    return fr.getField();
                }
            }
            return null;
        }

        public void addAll(FieldMapOfRecordType toBeAdded) {
            if (!toBeAdded.isEmpty()) {
                toBeAdded.fields.values().forEach(fr -> addInterno(fr.getField()));
            }
        }

        public void addAll(Map<String, MTipo<?>> toBeAdded) {
            toBeAdded.values().forEach(f -> addInterno(f));
        }

        private void addInterno(MTipo<?> field) {
            if (fields == null) {
                fields = new LinkedHashMap<>();
            }
            fields.put(field.getNomeSimples(), new FieldRef(field));
        }

        public int findIndex(String fieldName) {
            if (fields != null) {
                garantirLista();
                FieldRef fr = fields.get(fieldName);
                if (fr != null) {
                    return fr.getIndex();
                }
            }
            return -1;
        }

        public MTipo<?> getByIndex(int fieldIndex) {
            if (fields != null) {
                return garantirLista().get(fieldIndex);
            }
            throw new SingularFormException("Indice do campo incorreto: " + fieldIndex);
        }

        private List<MTipo<?>> garantirLista() {
            if (fieldsList == null) {
                int index = 0;
                fieldsList = new ArrayList<>(fields.size());
                for (FieldRef ref : fields.values()) {
                    ref.setIndex(index);
                    fieldsList.add(ref.getField());
                    index++;
                }
            }
            return fieldsList;
        }
    }

    private static final class FieldRef {
        private final MTipo<?> field;
        private int            index = -1;

        public FieldRef(MTipo<?> field) {
            this.field = field;
        }

        public int getIndex() {
            return index;
        }

        public void setIndex(int index) {
            this.index = index;
        }

        public MTipo<?> getField() {
            return field;
        }
    }
}
