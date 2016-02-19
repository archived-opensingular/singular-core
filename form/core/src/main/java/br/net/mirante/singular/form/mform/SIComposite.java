package br.net.mirante.singular.form.mform;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import br.net.mirante.singular.form.mform.STypeComposite.FieldMapOfRecordType;

public class SIComposite extends SInstance implements ICompositeInstance {

    private FieldMapOfRecordInstance fields;

    @Override
    public STypeComposite<?> getType() {
        return (STypeComposite<?>) super.getType();
    }

    @Override
    public Object getValue() {
        return getCampos();
    }

    @Override
    public void clearInstance() {
        getCampos().forEach(SInstance::clearInstance);
    }

    @Override
    //TODO: Won't "isEmpty" is enough? the "ofData" seems kind of redundant.
    public boolean isEmptyOfData() {
        return fields == null || fields.stream().allMatch(i -> i.isEmptyOfData());
    }

    /**
     * Retorna apenas os campos do tipo que já foram instanciados.
     *
     * @return instancias dos campos
     */
    public Collection<SInstance> getCampos() {
        return (fields == null) ? Collections.emptyList() : fields.getFields();
    }

    /**
     * Retorna todos os campos do tipo, instanciando os que ainda não foram.
     *
     * @return instancias dos campos
     */
    public Collection<SInstance> getAllFields() {
        for (SType<?> field : getType().getFields())
            getCampo(field.getSimpleName());
        return getCampos();
    }

    @Override
    public Collection<SInstance> getChildren() {
        return getCampos();
    }

    @Override
    public Collection<SInstance> getAllChildren() {
        return getAllFields();
    }

    @Override
    public Stream<? extends SInstance> stream() {
        return fields == null ? Stream.empty() : fields.stream();
    }

    @Override
    public SInstance getCampo(String path) {
        return getCampo(new PathReader(path));
    }

    @Override
    final SInstance getCampoLocal(PathReader leitor) {
        int fieldIndex = findIndexTrecho(leitor);
        SInstance instancia = (fields == null) ? null : fields.getByIndex(fieldIndex);
        if (instancia == null) {
            instancia = createField(fieldIndex);
        }
        return instancia;
    }

    @Override
    final SInstance getCampoLocalSemCriar(PathReader leitor) {
        int fieldIndex = findIndexTrecho(leitor);
        return (fields == null) ? null : fields.getByIndex(fieldIndex);
    }

    public <T extends SInstance> T getFilho(SType<T> tipoPai) {
        throw new RuntimeException("Método não implementado");
    }

    @Override
    public void setValue(Object obj) {
        if(obj instanceof SIComposite){
            clearInstance();
            fields = ((SIComposite)obj).fields;
            ((SIComposite)obj).fields = null;
        }else{
            throw new RuntimeException("SIComposite só suporta valores de mesmo tipo");
        }
    }

    @Override
    public final void setValor(String pathCampo, Object valor) {
        setValor(new PathReader(pathCampo), valor);
    }

    /**
     * Configura o valor de um tipo filho imediato do tipo composto
     * ao qual essa instancia se refere.
     * <p>
     * O Mtipo informado já precisa estar previamente configurado nesse
     * MtipoComposto
     *
     * @param campo Referencia ao mtipo filho do composto
     * @param valor Valor para o mtipo referenciado.
     */
    public final void setValor(SType<?> campo, Object valor) {
        setValor(new PathReader(campo.getSimpleName()), valor);
    }

    /**
     * Obtém o valor de um campo a partir do seu tipo
     * O campo deve ser filho imediato desse MTipo
     *
     * @param campo Tipo do campo filho
     * @return
     */
    public Object getValor(SType<?> campo) {
        return getValor(campo.getSimpleName());
    }

    private FieldMapOfRecordType getFieldsDef() {
        return getType().getFieldsConsolidated();
    }

    @Override
    void setValor(PathReader leitorPath, Object valor) {
        int fieldIndex = findIndexTrecho(leitorPath);
        SInstance instancia = (fields == null) ? null : fields.getByIndex(fieldIndex);
        if (instancia == null) {
            if (valor == null) {
                return;
            }
            instancia = createField(fieldIndex);
        }
        if (leitorPath.isUltimo()) {
            if (valor == null) {
                SInstance child = fields.getByIndex(fieldIndex);
                if (child != null) {
                    child.internalOnRemove();
                    fields.remove(fieldIndex);
                }
            } else {
                instancia.setValue(valor);
            }
        } else {
            instancia.setValor(leitorPath.proximo(), valor);
        }
    }

    private SInstance createField(int fieldIndex) {
        SInstance instancia;
        SType<?> tipoCampo = getFieldsDef().getByIndex(fieldIndex);
        instancia = tipoCampo.newInstance(getDocument());
        instancia.setParent(this);
        if (fields == null) {
            fields = new FieldMapOfRecordInstance(getFieldsDef());
        }
        fields.set(fieldIndex, instancia);
        return instancia;
    }

    private int findIndexTrecho(PathReader leitor) {
        if (leitor.isIndice()) {
            throw new SingularFormException(leitor.getTextoErro(this, "Não é uma lista"));
        }
        int fieldIndex = getFieldsDef().findIndex(leitor.getTrecho());
        if (fieldIndex == -1) {
            throw new SingularFormException(leitor.getTextoErro(this, "Não é um campo definido"));
        }
        return fieldIndex;
    }

    @Override
    public final <T extends Object> T getValor(String pathCampo, Class<T> classeDestino) {
        return getValor(new PathReader(pathCampo), classeDestino);
    }

    @Override
    final <T extends Object> T getValorWithDefaultIfNull(PathReader leitor, Class<T> classeDestino) {
        if (fields != null) {
            SInstance instancia = fields.getByIndex(findIndexTrecho(leitor));
            if (instancia != null) {
                return instancia.getValorWithDefaultIfNull(leitor.proximo(), classeDestino);
            }
        }
        SType<?> tipo = MFormUtil.resolverTipoCampo(getType(), leitor);
        return tipo.getValorAtributoOrDefaultValueIfNull(classeDestino);
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((fields == null) ? 0 : fields.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        SIComposite other = (SIComposite) obj;
        if (!getType().equals(other.getType())) {
            return false;
        }
        return Objects.equals(fields, other.fields);
    }

    private final static class FieldMapOfRecordInstance {

        private final SInstance[] instances;

        public FieldMapOfRecordInstance(FieldMapOfRecordType fieldsDef) {
            this.instances = new SInstance[fieldsDef.size()];
        }

        public SInstance getByIndex(int fieldIndex) {
            return instances[fieldIndex];
        }

        public void remove(int fieldIndex) {
            instances[fieldIndex] = null;
        }

        public void set(int fieldIndex, SInstance instance) {
            instances[fieldIndex] = instance;
        }

        public List<SInstance> getFields() {
            return stream().collect(Collectors.toList());
        }

        public Stream<SInstance> stream() {
            return Arrays.stream(instances).filter(i -> i != null);
        }

        @Override
        public int hashCode() {
            final int prime = 31;
            int result = 1;
            result = prime * result + Arrays.hashCode(instances);
            return result;
        }

        @Override
        public boolean equals(Object obj) {
            if (this == obj)
                return true;
            if (obj == null)
                return false;
            if (getClass() != obj.getClass())
                return false;
            FieldMapOfRecordInstance other = (FieldMapOfRecordInstance) obj;
            if (!Arrays.equals(instances, other.instances))
                return false;
            return true;
        }
    }
}