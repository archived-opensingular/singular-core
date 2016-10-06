package org.opensingular.singular.server.commons.persistence.entity.form;


import org.opensingular.singular.server.commons.persistence.entity.enums.PersonType;
import org.opensingular.lib.support.persistence.entity.BaseEntity;
import org.opensingular.lib.support.persistence.util.Constants;
import org.opensingular.lib.support.persistence.util.GenericEnumUserType;
import org.opensingular.lib.support.persistence.util.HybridIdentityOrSequenceGenerator;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;
import org.hibernate.annotations.Type;

import javax.persistence.*;

@Entity
@Table(schema = Constants.SCHEMA, name = "TB_PETICIONANTE")
@GenericGenerator(name = PetitionerEntity.PK_GENERATOR_NAME, strategy = HybridIdentityOrSequenceGenerator.CLASS_NAME)
public class PetitionerEntity extends BaseEntity<Long> {


    public static final String PK_GENERATOR_NAME = "GENERATED_CO_PETICIONANTE";

    @Id
    @Column(name = "CO_PETICIONANTE")
    @GeneratedValue(generator = PK_GENERATOR_NAME)
    private Long cod;

    @Column(name = "DS_NOME")
    private String name;

    @Column(name = "ID_PESSOA")
    private String idPessoa;

    @Column(name = "NU_CPF_CNPJ")
    private String cpfCNPJ;

    @Type(type = GenericEnumUserType.CLASS_NAME, parameters = {
            @Parameter(name = "enumClass", value = PersonType.CLASS_NAME),
            @Parameter(name = "identifierMethod", value = "getCod"),
            @Parameter(name = "valueOfMethod", value = "valueOfEnum")})
    @Column(name = "TP_PESSOA", insertable = false, updatable = false)
    private PersonType personType;

    @Override
    public Long getCod() {
        return cod;
    }

    public void setCod(Long cod) {
        this.cod = cod;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getIdPessoa() {
        return idPessoa;
    }

    public void setIdPessoa(String idPessoa) {
        this.idPessoa = idPessoa;
    }

    public String getCpfCNPJ() {
        return cpfCNPJ;
    }

    public void setCpfCNPJ(String cpfCNPJ) {
        this.cpfCNPJ = cpfCNPJ;
    }

    public PersonType getPersonType() {
        return personType;
    }

    public void setPersonType(PersonType personType) {
        this.personType = personType;
    }
}
