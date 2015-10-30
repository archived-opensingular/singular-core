package br.net.mirante.singular.persistence.entity;

import br.net.mirante.singular.flow.core.MUser;
import br.net.mirante.singular.persistence.util.Constants;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * The persistent class for the TB_ATOR database table.
 */
@Entity
@Table(name = "VW_ATOR", schema = Constants.SCHEMA)
public class Actor extends BaseEntity implements MUser {

    /* nao deve ter generator, deve ser uma view*/
    @Id
    @Column(name = "CO_ATOR")
    private Integer cod;

    @Column(name = "CO_USUARIO", nullable = false)
    private String codUsuario;

    @Column(name = "NO_ATOR", nullable = false)
    private String nome;

    @Column(name = "DS_EMAIL", nullable = false)
    private String email;

    public Actor() {
    }

    public Actor(Integer cod, String codUsuario, String nome, String email) {
        this.cod = cod;
        this.codUsuario = codUsuario;
        this.nome = nome;
        this.email = email;
    }

    @Override
    public String getSimpleName() {
        return getNome();
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    @Override
    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getCodUsuario() {
        return codUsuario;
    }

    public void setCodUsuario(String codUsuario) {
        this.codUsuario = codUsuario;
    }

    @Override
    public Integer getCod() {
        return cod;
    }

    public void setCod(Integer cod) {
        this.cod = cod;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof Actor)) {
            return false;
        }
        if (this.getCod() != null && ((Actor) obj).getCod() != null) {
            return super.equals(obj);
        }
        return new EqualsBuilder().append(this.getCodUsuario(), ((Actor) obj).getCodUsuario()).build();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder().append(this.getCodUsuario()).build();
    }
}