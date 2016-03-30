package br.net.mirante.singular.exemplos.notificacaosimpliciada.domain.generic;

import br.net.mirante.singular.exemplos.notificacaosimpliciada.domain.TipoTermo;
import br.net.mirante.singular.exemplos.notificacaosimpliciada.domain.enums.SimNao;
import br.net.mirante.singular.persistence.entity.BaseEntity;
import org.hibernate.annotations.Filter;
import org.hibernate.annotations.Parameter;
import org.hibernate.annotations.Type;

import javax.persistence.*;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;
import java.io.Serializable;
import java.util.Date;

/**
 * Classe marcadora para todos os tipos de vocabulario controlado Todo
 * vocabulario controlado deve herdar desta classe, direta ou indiretamente, por
 * questões de compatibilidade com os serviços que serão expostos
 */
@XmlRootElement(name = "vocabulario-controlado", namespace = "http://www.anvisa.gov.br/reg-med/schema/domains")
@XmlType(name = "vocabulario-controlado", namespace = "http://www.anvisa.gov.br/reg-med/schema/domains")
@Inheritance(strategy = InheritanceType.JOINED)
@Table(name = "TB_VOCABULARIO_CONTROLADO", schema = "DBMEDICAMENTO")
@Entity
@Filter(name = "VocabulariosAtivos", condition = "ativa = \"S\"")
public abstract class VocabularioControlado extends BaseEntity implements MedEntity<Long> {

    private static final long serialVersionUID = 496526748207612785L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "SEQ_VOCABULARIOCONTROLADO")
    @SequenceGenerator(sequenceName = "DBMEDICAMENTO.SQ_COSEQVOCABULARIOCONTROLADO", name = "SEQ_VOCABULARIOCONTROLADO", initialValue = 1, allocationSize = 1)
    @Column(name = "CO_SEQ_VOCABULARIO_CONTROLADO", unique = true, nullable = false, precision = 8, scale = 0)
    protected Long id;

    @Column(name = "DS_DESCRICAO", unique = true, nullable = false, length = 200)
    protected String descricao;

    @Column(name = "ST_REGISTRO_ATIVO", nullable = false, length = 1)
    @Type(type = "com.miranteinfo.seam.hibernate.usertype.GenericEnumUserType", parameters = {
            @Parameter(name = "enumClassName", value = SimNao.ENUM_CLASS_NAME),
            @Parameter(name = "identifierMethod", value = "getCodigo"),
            @Parameter(name = "valueOfMethod", value = "valueOfEnum")
    })
    protected SimNao ativa;

    @ManyToOne
    @JoinColumn(name = "CO_TIPO_TERMO")
    protected TipoTermo tipoTermo;

    @Column(name = "DS_JUSTIFICATIVA_EXCLUSAO", length = 1000)
    protected String justificativaExclusao;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "DT_CRIACAO", columnDefinition = "date")
    protected Date dataCriacao;

    public VocabularioControlado() {
    }

    public VocabularioControlado(Long id, String descricao, SimNao ativa,
                                 TipoTermo tipoTermo) {
        super();
        this.id = id;
        this.descricao = descricao;
        this.ativa = ativa;
        this.tipoTermo = tipoTermo;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    @XmlTransient
    public SimNao getAtiva() {
        return ativa;
    }

    public void setAtiva(SimNao ativa) {
        this.ativa = ativa;
    }

    @XmlTransient
    public TipoTermo getTipoTermo() {
        return tipoTermo;
    }

    public void setTipoTermo(TipoTermo tipoTermo) {
        this.tipoTermo = tipoTermo;
    }

    public String getJustificativaExclusao() {
        return justificativaExclusao;
    }

    public void setJustificativaExclusao(String justificativaExclusao) {
        this.justificativaExclusao = justificativaExclusao;
    }

    @Override
    public String toString() {
        return descricao;
    }

    @Override
    public int hashCode() {
        final int prime  = 31;
        int       result = 1;
        result = prime * result
                + ((descricao == null) ? 0 : descricao.hashCode());
        result = prime * result + ((id == null) ? 0 : id.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        VocabularioControlado other = (VocabularioControlado) obj;
        if (descricao == null) {
            if (other.descricao != null) {
                return false;
            }
        } else if (!descricao.equals(other.descricao)) {
            return false;
        }
        if (id == null) {
            if (other.id != null) {
                return false;
            }
        } else if (!id.equals(other.id)) {
            return false;
        }
        return true;
    }

    public Date getDataCriacao() {
        return dataCriacao;
    }

    public void setDataCriacao(Date dataCriacao) {
        this.dataCriacao = dataCriacao;
    }

    @Override
    public Serializable getCod() {
        return id;
    }
}
