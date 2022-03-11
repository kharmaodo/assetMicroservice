package sn.free.selfcare.service.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import io.swagger.annotations.ApiModel;
import sn.free.selfcare.domain.enumeration.ObjectStatus;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

/**
 * A DTO for the {@link sn.free.selfcare.domain.Employe} entity.
 */
//TODO : MD : Audit : Exploiter Lombok pour dit clean code
@ApiModel(description = "Employe entity.\n@author Ahmadou Diaw")
public class EmployeDTO implements Serializable {

    private Long id;

    @NotBlank
    private String nom;

    @NotBlank
    private String prenom;

    private String fonction;

    @Email
    private String email;

    @NotNull
    private ObjectStatus status;

    private LigneDTO ligne;

    @JsonIgnoreProperties({"employes", "produit"})
    private GroupeDTO groupe;

    private Long ligneId;

    private Long clientId;

    private Long groupeId;

    private List<ActivateDeactivateServiceDTO> adServicesRequest;

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

    public String getPrenom() {
        return prenom;
    }

    public void setPrenom(String prenom) {
        this.prenom = prenom;
    }

    public String getFonction() {
        return fonction;
    }

    public void setFonction(String fonction) {
        this.fonction = fonction;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public ObjectStatus getStatus() {
        return status;
    }

    public void setStatus(ObjectStatus status) {
        this.status = status;
    }

    public Long getLigneId() {
        return ligneId;
    }

    public void setLigneId(Long ligneId) {
        this.ligneId = ligneId;
    }

    public Long getClientId() {
        return clientId;
    }

    public void setClientId(Long clientId) {
        this.clientId = clientId;
    }

    public Long getGroupeId() {
        return groupeId;
    }

    public void setGroupeId(Long groupeId) {
        this.groupeId = groupeId;
    }

    public GroupeDTO getGroupe() {
        return groupe;
    }

    public void setGroupe(GroupeDTO groupe) {
        this.groupe = groupe;
    }

    public LigneDTO getLigne() {
        return ligne;
    }

    public void setLigne(LigneDTO ligne) {
        this.ligne = ligne;
    }

    public List<ActivateDeactivateServiceDTO> getAdServicesRequest() {
        return adServicesRequest;
    }

    public void setAdServicesRequest(List<ActivateDeactivateServiceDTO> adServicesRequest) {
        this.adServicesRequest = adServicesRequest;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof EmployeDTO)) {
            return false;
        }

        return id != null && id.equals(((EmployeDTO) o).id);
    }

    @Override
    public int hashCode() {
        return 31;
    }

    // prettier-ignore
    @Override
    public String toString() {
        final int maxLen = 15;
        return "EmployeDTO{" +
            "id=" + getId() +
            ", nom='" + getNom() + "'" +
            ", prenom='" + getPrenom() + "'" +
            ", fonction='" + getFonction() + "'" +
            ", email='" + getEmail() + "'" +
            ", status='" + getStatus() + "'" +
            ", ligneId=" + getLigneId() +
            ", clientId=" + getClientId() +
            ", groupeId=" + getGroupeId() +
            ", adServicesRequest=" + (getAdServicesRequest() != null ? toString(getAdServicesRequest(), maxLen) : null) +
            "}";
    }

    private String toString(Collection<?> collection, int maxLen) {
        StringBuilder builder = new StringBuilder();
        builder.append("[");
        int i = 0;
        for (Iterator<?> iterator = collection.iterator(); iterator.hasNext() && i < maxLen; i++) {
            if (i > 0) builder.append(", ");
            builder.append(iterator.next());
        }
        builder.append("]");
        return builder.toString();
    }
}
