package sn.free.selfcare.service.dto;

import java.io.Serializable;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import javax.validation.constraints.NotNull;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonProperty.Access;

import io.swagger.annotations.ApiModel;
import sn.free.selfcare.domain.enumeration.ModePaiement;
import sn.free.selfcare.domain.enumeration.ObjectStatus;
import sn.free.selfcare.domain.enumeration.TypeOffre;

/**
 * A DTO for the {@link sn.free.selfcare.domain.Offre} entity.
 */
//TODO : MD : Audit : Exploiter Lombok pour dit clean code
@ApiModel(description = "Offre entity.\n@author Ahmadou Diaw")
public class OffreDTO implements Serializable {

	private Long id;

	private String code;

	@NotNull
	private TypeOffre typeOffre;

	@NotNull
	private ModePaiement modePaiement;

	@NotNull
	private Double montant;

	private Integer dureeEngagement;

	@NotNull
	private ObjectStatus status;

	//@JsonProperty(access = Access.WRITE_ONLY)
	private Set<ClientDTO> clients = new HashSet<>();

	private Set<ProduitDTO> produits = new HashSet<>();

	private Long grilleTarifaireId;
	private GrilleTarifaireDTO grilleTarifaire;

	private Set<LigneDTO> lignes = new HashSet<>();

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public TypeOffre getTypeOffre() {
		return typeOffre;
	}

	public void setTypeOffre(TypeOffre typeOffre) {
		this.typeOffre = typeOffre;
	}

	public ModePaiement getModePaiement() {
		return modePaiement;
	}

	public void setModePaiement(ModePaiement modePaiement) {
		this.modePaiement = modePaiement;
	}

	public Double getMontant() {
		return montant;
	}

	public void setMontant(Double montant) {
		this.montant = montant;
	}

	public Integer getDureeEngagement() {
		return dureeEngagement;
	}

	public void setDureeEngagement(Integer dureeEngagement) {
		this.dureeEngagement = dureeEngagement;
	}

	public ObjectStatus getStatus() {
		return status;
	}

	public void setStatus(ObjectStatus status) {
		this.status = status;
	}

	public Set<ClientDTO> getClients() {
		return clients;
	}

	public void setClients(Set<ClientDTO> clients) {
		this.clients = clients;
	}

	public Set<ProduitDTO> getProduits() {
		return produits;
	}

	public void setProduits(Set<ProduitDTO> produits) {
		this.produits = produits;
	}

	public Set<LigneDTO> getLignes() {
		return lignes;
	}

	public void setLignes(Set<LigneDTO> lignes) {
		this.lignes = lignes;
	}

	public Long getGrilleTarifaireId() {
		return grilleTarifaireId;
	}

	public void setGrilleTarifaireId(Long grilleTarifaireId) {
		this.grilleTarifaireId = grilleTarifaireId;
	}

	public GrilleTarifaireDTO getGrilleTarifaire() {
		return grilleTarifaire;
	}

	public void setGrilleTarifaire(GrilleTarifaireDTO grilleTarifaire) {
		this.grilleTarifaire = grilleTarifaire;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) {
			return true;
		}
		if (!(o instanceof OffreDTO)) {
			return false;
		}

		return id != null && id.equals(((OffreDTO) o).id);
	}

	@Override
	public int hashCode() {
		return 31;
	}

	@Override
	public String toString() {
		final int maxLen = 5;
		return "OffreDTO [id=" + id + ", code=" + code + ", typeOffre=" + typeOffre + ", modePaiement=" + modePaiement
				+ ", montant=" + montant + ", dureeEngagement=" + dureeEngagement + ", status=" + status + ", clients="
				+ (clients != null ? toString(clients, maxLen) : null) + ", produits="
				+ (produits != null ? toString(produits, maxLen) : null) + ", grilleTarifaireId=" + grilleTarifaireId
				+ ", grilleTarifaire=" + grilleTarifaire + ", lignes="
				+ (lignes != null ? toString(lignes, maxLen) : null) + "]";
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
