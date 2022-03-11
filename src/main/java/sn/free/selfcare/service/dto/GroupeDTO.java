package sn.free.selfcare.service.dto;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

import javax.validation.constraints.NotNull;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import io.swagger.annotations.ApiModel;
import sn.free.selfcare.domain.enumeration.ObjectStatus;
import sn.free.selfcare.domain.enumeration.Periodicite;

/**
 * A DTO for the {@link sn.free.selfcare.domain.Groupe} entity.
 */
@ApiModel(description = "Groupe entity.\n@author Ahmadou Diaw")
//TODO : MD : Audit : Exploiter Lombok pour dit clean code
public class GroupeDTO implements Serializable {

    private Long id;

    @NotNull
    private String nom;

    private String description;

    private Periodicite periodicite;

    @NotNull
    private ObjectStatus status;

    private Long clientId;

    @NotNull
    private Long produitId;

	private ProduitDTO produit;

	@JsonIgnoreProperties(value = { "groupe", "client" })
	private Set<EmployeDTO> employes = new HashSet<>();

	private Integer nbEmployes;

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

    public Periodicite getPeriodicite() {
        return periodicite;
    }

    public void setPeriodicite(Periodicite periodicite) {
        this.periodicite = periodicite;
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

    public Long getProduitId() {
        return produitId;
    }

    public void setProduitId(Long produitId) {
        this.produitId = produitId;
    }

	public ProduitDTO getProduit() {
		return produit;
	}

	public void setProduit(ProduitDTO produit) {
		this.produit = produit;
	}

	public Set<EmployeDTO> getEmployes() {
		return employes;
	}

	public void setEmployes(Set<EmployeDTO> employes) {
		this.employes = employes;
	}

	public Integer getNbEmployes() {
		return nbEmployes;
	}

	public void setNbEmployes(Integer nbEmployes) {
		this.nbEmployes = nbEmployes;
	}

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof GroupeDTO)) {
            return false;
        }

        return id != null && id.equals(((GroupeDTO) o).id);
    }

    @Override
    public int hashCode() {
        return 31;
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "GroupeDTO{" +
            "id=" + getId() +
            ", nom='" + getNom() + "'" +
            ", description='" + getDescription() + "'" +
            ", periodicite='" + getPeriodicite() + "'" +
            ", status='" + getStatus() + "'" +
            ", clientId=" + getClientId() +
            ", produitId=" + getProduitId() +
            "}";
    }
}
