package sn.free.selfcare.service.dto;

import io.swagger.annotations.ApiModel;
import javax.validation.constraints.*;
import java.io.Serializable;
import sn.free.selfcare.domain.enumeration.TypeOffre;
import sn.free.selfcare.domain.enumeration.ObjectStatus;

/**
 * A DTO for the {@link sn.free.selfcare.domain.Produit} entity.
 */
//TODO : MD : Audit : Exploiter Lombok pour dit clean code
@ApiModel(description = "Produit entity.\n@author Ahmadou Diaw")
public class ProduitDTO implements Serializable {
    
    private Long id;

    @NotBlank
    private String nom;

    private String description;

    @NotNull
    private TypeOffre typeProduit;

    @NotNull
    private Double credit;

    @NotNull
    private Double sms;

    @NotNull
    private Double minAppel;

    @NotNull
    private Double goData;

    @NotNull
    private ObjectStatus status;


    private Long clientId;
    
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

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public TypeOffre getTypeProduit() {
        return typeProduit;
    }

    public void setTypeProduit(TypeOffre typeProduit) {
        this.typeProduit = typeProduit;
    }

    public Double getCredit() {
        return credit;
    }

    public void setCredit(Double credit) {
        this.credit = credit;
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

    public Long getClientId() {
        return clientId;
    }

    public void setClientId(Long clientId) {
        this.clientId = clientId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof ProduitDTO)) {
            return false;
        }

        return id != null && id.equals(((ProduitDTO) o).id);
    }

    @Override
    public int hashCode() {
        return 31;
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "ProduitDTO{" +
            "id=" + getId() +
            ", nom='" + getNom() + "'" +
            ", description='" + getDescription() + "'" +
            ", typeProduit='" + getTypeProduit() + "'" +
            ", credit=" + getCredit() +
            ", sms=" + getSms() +
            ", minAppel=" + getMinAppel() +
            ", goData=" + getGoData() +
            ", status='" + getStatus() + "'" +
            ", clientId=" + getClientId() +
            "}";
    }
}
