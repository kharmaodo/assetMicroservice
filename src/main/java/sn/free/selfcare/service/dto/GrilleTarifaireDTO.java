package sn.free.selfcare.service.dto;

import io.swagger.annotations.ApiModel;
import javax.validation.constraints.*;
import java.io.Serializable;
import sn.free.selfcare.domain.enumeration.TypeTarification;
import sn.free.selfcare.domain.enumeration.ObjectStatus;

/**
 * A DTO for the {@link sn.free.selfcare.domain.GrilleTarifaire} entity.
 */
//TODO : MD : Audit : Exploiter Lombok pour dit clean code
@ApiModel(description = "GrilleTarifaire entity.\n@author Ahmadou Diaw")
public class GrilleTarifaireDTO implements Serializable {
    
    private Long id;

    @NotNull
    private TypeTarification typeTarification;

    @NotNull
    private Double sms;

    @NotNull
    private Double minAppel;

    @NotNull
    private Double goData;

    @NotNull
    private ObjectStatus status;

    
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public TypeTarification getTypeTarification() {
        return typeTarification;
    }

    public void setTypeTarification(TypeTarification typeTarification) {
        this.typeTarification = typeTarification;
    }

    public Double getSms() {
        return sms;
    }

    public void setSms(Double sms) {
        this.sms = sms;
    }

    public Double getMinAppel() {
        return minAppel;
    }

    public void setMinAppel(Double minAppel) {
        this.minAppel = minAppel;
    }

    public Double getGoData() {
        return goData;
    }

    public void setGoData(Double goData) {
        this.goData = goData;
    }

    public ObjectStatus getStatus() {
        return status;
    }

    public void setStatus(ObjectStatus status) {
        this.status = status;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof GrilleTarifaireDTO)) {
            return false;
        }

        return id != null && id.equals(((GrilleTarifaireDTO) o).id);
    }

    @Override
    public int hashCode() {
        return 31;
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "GrilleTarifaireDTO{" +
            "id=" + getId() +
            ", typeTarification='" + getTypeTarification() + "'" +
            ", sms=" + getSms() +
            ", minAppel=" + getMinAppel() +
            ", goData=" + getGoData() +
            ", status='" + getStatus() + "'" +
            "}";
    }
}
