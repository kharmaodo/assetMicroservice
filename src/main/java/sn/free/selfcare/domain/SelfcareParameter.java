package sn.free.selfcare.domain;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import javax.persistence.*;
import javax.validation.constraints.*;

import org.springframework.data.elasticsearch.annotations.FieldType;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.io.Serializable;

import sn.free.selfcare.domain.enumeration.ParameterCategory;

import sn.free.selfcare.domain.enumeration.ParameterOption;

import sn.free.selfcare.domain.enumeration.TypeValeur;

/**
 * SelfcareParameter entity.\n@author Ahmadou Diaw
 */
//TODO : MD : Audit : Exploiter Lombok pour dit clean code
@Entity
@Table(name = "selfcare_parameter")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@org.springframework.data.elasticsearch.annotations.Document(indexName = "selfcareparameter")
// Par MOF
@JsonIgnoreProperties
// Fin MOF
public class SelfcareParameter implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "category", nullable = false)
    private ParameterCategory category;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "nom", nullable = false)
    private ParameterOption nom;

    @Column(name = "description")
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(name = "type")
    private TypeValeur type;

    @Column(name = "valeur")
    private String valeur;

    @Column(name = "several_values")
    private Boolean severalValues;

    // jhipster-needle-entity-add-field - JHipster will add fields here
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public ParameterCategory getCategory() {
        return category;
    }

    public SelfcareParameter category(ParameterCategory category) {
        this.category = category;
        return this;
    }

    public void setCategory(ParameterCategory category) {
        this.category = category;
    }

    public ParameterOption getNom() {
        return nom;
    }

    public SelfcareParameter nom(ParameterOption nom) {
        this.nom = nom;
        return this;
    }

    public void setNom(ParameterOption nom) {
        this.nom = nom;
    }

    public String getDescription() {
        return description;
    }

    public SelfcareParameter description(String description) {
        this.description = description;
        return this;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public TypeValeur getType() {
        return type;
    }

    public SelfcareParameter type(TypeValeur type) {
        this.type = type;
        return this;
    }

    public void setType(TypeValeur type) {
        this.type = type;
    }

    public String getValeur() {
        return valeur;
    }

    public SelfcareParameter valeur(String valeur) {
        this.valeur = valeur;
        return this;
    }

    public void setValeur(String valeur) {
        this.valeur = valeur;
    }

    public Boolean isSeveralValues() {
        return severalValues;
    }

    public SelfcareParameter severalValues(Boolean severalValues) {
        this.severalValues = severalValues;
        return this;
    }

    public void setSeveralValues(Boolean severalValues) {
        this.severalValues = severalValues;
    }
    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof SelfcareParameter)) {
            return false;
        }
        return id != null && id.equals(((SelfcareParameter) o).id);
    }

    @Override
    public int hashCode() {
        return 31;
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "SelfcareParameter{" +
            "id=" + getId() +
            ", category='" + getCategory() + "'" +
            ", nom='" + getNom() + "'" +
            ", description='" + getDescription() + "'" +
            ", type='" + getType() + "'" +
            ", valeur='" + getValeur() + "'" +
            ", severalValues='" + isSeveralValues() + "'" +
            "}";
    }
}
