package sn.free.selfcare.service.dto;

import io.swagger.annotations.ApiModel;
import javax.validation.constraints.*;
import java.io.Serializable;

/**
 * A DTO for the {@link sn.free.selfcare.domain.Country} entity.
 */
//TODO : MD : Audit : Exploiter Lombok pour dit clean code
@ApiModel(description = "Country entity.\n@author Ahmadou Diaw")
public class CountryDTO implements Serializable {
    
    private Long id;

    @NotNull
    private String nom;

    @NotNull
    private String code;

    @NotNull
    private String indicatif;

    @NotNull
    private String devise;

    
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNom() {
        return nom;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getIndicatif() {
        return indicatif;
    }

    public void setIndicatif(String indicatif) {
        this.indicatif = indicatif;
    }

    public String getDevise() {
        return devise;
    }

    public void setDevise(String devise) {
        this.devise = devise;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof CountryDTO)) {
            return false;
        }

        return id != null && id.equals(((CountryDTO) o).id);
    }

    @Override
    public int hashCode() {
        return 31;
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "CountryDTO{" +
            "id=" + getId() +
            ", nom='" + getNom() + "'" +
            ", code='" + getCode() + "'" +
            ", indicatif='" + getIndicatif() + "'" +
            ", devise='" + getDevise() + "'" +
            "}";
    }
}
