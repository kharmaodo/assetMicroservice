package sn.free.selfcare.service.dto;

import io.swagger.annotations.ApiModel;
import javax.validation.constraints.*;
import java.io.Serializable;
import sn.free.selfcare.domain.enumeration.ParameterCategory;
import sn.free.selfcare.domain.enumeration.ParameterOption;
import sn.free.selfcare.domain.enumeration.TypeValeur;

/**
 * A DTO for the {@link sn.free.selfcare.domain.SelfcareParameter} entity.
 */
//TODO : MD : Audit : Exploiter Lombok pour dit clean code
@ApiModel(description = "SelfcareParameter entity.\n@author Ahmadou Diaw")
public class SelfcareParameterDTO implements Serializable {
    
    private Long id;

    @NotNull
    private ParameterCategory category;

    @NotNull
    private ParameterOption nom;

    private String description;

    private TypeValeur type;

    private String valeur;

    private Boolean severalValues;

    
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public ParameterCategory getCategory() {
        return category;
    }

    public void setCategory(ParameterCategory category) {
        this.category = category;
    }

    public ParameterOption getNom() {
        return nom;
    }

    public void setNom(ParameterOption nom) {
        this.nom = nom;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public TypeValeur getType() {
        return type;
    }

    public void setType(TypeValeur type) {
        this.type = type;
    }

    public String getValeur() {
        return valeur;
    }

    public void setValeur(String valeur) {
        this.valeur = valeur;
    }

    public Boolean isSeveralValues() {
        return severalValues;
    }

    public void setSeveralValues(Boolean severalValues) {
        this.severalValues = severalValues;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof SelfcareParameterDTO)) {
            return false;
        }

        return id != null && id.equals(((SelfcareParameterDTO) o).id);
    }

    @Override
    public int hashCode() {
        return 31;
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "SelfcareParameterDTO{" +
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
