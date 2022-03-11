package sn.free.selfcare.service.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import io.swagger.annotations.ApiModel;
import sn.free.selfcare.domain.enumeration.ObjectStatus;

import javax.validation.constraints.*;
import java.io.Serializable;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

/**
 * A DTO for the {@link sn.free.selfcare.domain.Client} entity.
 */
//TODO : MD : Audit : Exploiter Lombok pour dit clean code
@ApiModel(description = "Client entity.\n@author Ahmadou Diaw")
public class ClientDTO implements Serializable {

    private Long id;

    @NotBlank
    @Size(min = 4, max = 19)
    private String code;

    @NotBlank
    private String raisonSociale;

    @NotBlank
    private String ninea;

    private String registreComm;

    @NotBlank
    @Size(min = 9, max = 9)
    @Digits(fraction = 0, integer = 9)
    private String numeroVirtuel;

    @Size(min = 0, max = 9)
    private String numeroFM;

    @NotBlank
    @Size(max = 9)
    @Digits(fraction = 0, integer = 9)
    private String telephone;

    @NotBlank
    @Email
    private String email;

    @NotBlank
    private String adresse;

    private String ville;

    @NotBlank
    @Email
    private String gestionnaire;

    @JsonIgnoreProperties(value = "clients")
    private Set<OffreDTO> offres = new HashSet<>();

    private Set<ProduitDTO> produitsPersonnalises = new HashSet<>();

    private Set<LigneDTO> lignes = new HashSet<>();

    @JsonIgnoreProperties(value = "clients")
    private CountryDTO country;

    private Set<GroupeDTO> groupes = new HashSet<>();

    private Set<EmployeDTO> employes = new HashSet<>();

    @NotNull
    private ObjectStatus status;

    private Long countryId;

    private AdminClientDTO admin;

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

    public String getRaisonSociale() {
        return raisonSociale;
    }

    public void setRaisonSociale(String raisonSociale) {
        this.raisonSociale = raisonSociale;
    }

    public String getNinea() {
        return ninea;
    }

    public void setNinea(String ninea) {
        this.ninea = ninea;
    }

    public String getRegistreComm() {
        return registreComm;
    }

    public void setRegistreComm(String registreComm) {
        this.registreComm = registreComm;
    }

    public String getNumeroVirtuel() {
        return numeroVirtuel;
    }

    public void setNumeroVirtuel(String numeroVirtuel) {
        this.numeroVirtuel = numeroVirtuel;
    }

    public String getNumeroFM() {
        return numeroFM;
    }

    public void setNumeroFM(String numeroFM) {
        this.numeroFM = numeroFM;
    }

    public String getTelephone() {
        return telephone;
    }

    public void setTelephone(String telephone) {
        this.telephone = telephone;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getAdresse() {
        return adresse;
    }

    public void setAdresse(String adresse) {
        this.adresse = adresse;
    }

    public String getVille() {
        return ville;
    }

    public void setVille(String ville) {
        this.ville = ville;
    }

    public String getGestionnaire() {
        return gestionnaire;
    }

    public void setGestionnaire(String gestionnaire) {
        this.gestionnaire = gestionnaire;
    }

    public ObjectStatus getStatus() {
        return status;
    }

    public void setStatus(ObjectStatus status) {
        this.status = status;
    }

    public Set<OffreDTO> getOffres() {
        return offres;
    }

    public void setOffres(Set<OffreDTO> offres) {
        this.offres = offres;
    }

    public Set<ProduitDTO> getProduitsPersonnalises() {
        return produitsPersonnalises;
    }

    public void setProduitsPersonnalises(Set<ProduitDTO> produitsPersonnalises) {
        this.produitsPersonnalises = produitsPersonnalises;
    }

    public Set<LigneDTO> getLignes() {
        return lignes;
    }

    public void setLignes(Set<LigneDTO> lignes) {
        this.lignes = lignes;
    }

    public CountryDTO getCountry() {
        return country;
    }

    public void setCountry(CountryDTO country) {
        this.country = country;
    }

    public Set<GroupeDTO> getGroupes() {
        return groupes;
    }

    public void setGroupes(Set<GroupeDTO> groupes) {
        this.groupes = groupes;
    }

    public Set<EmployeDTO> getEmployes() {
        return employes;
    }

    public void setEmployes(Set<EmployeDTO> employes) {
        this.employes = employes;
    }

    public AdminClientDTO getAdmin() {
        return admin;
    }

    public void setAdmin(AdminClientDTO admin) {
        this.admin = admin;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof ClientDTO)) {
            return false;
        }

        return id != null && id.equals(((ClientDTO) o).id);
    }

    @Override
    public int hashCode() {
        return 31;
    }

    @Override
    public String toString() {
        final int maxLen = 5;
        return "ClientDTO [id=" + id + ", code=" + code + ", raisonSociale=" + raisonSociale + ", ninea=" + ninea
            + ", registreComm=" + registreComm + ", numeroVirtuel=" + numeroVirtuel + ", numeroFM=" + numeroFM + ", telephone=" + telephone
            + ", email=" + email + ", adresse=" + adresse + ", ville=" + ville + ", gestionnaire=" + gestionnaire
            + ", admin=" + admin + ", offres=" + (offres != null ? toString(offres, maxLen) : null) + ", produitsPersonnalises="
            + (produitsPersonnalises != null ? toString(produitsPersonnalises, maxLen) : null) + ", lignes="
            + (lignes != null ? toString(lignes, maxLen) : null) + ", country=" + country + ", groupes="
            + (groupes != null ? toString(groupes, maxLen) : null) + ", employes="
            + (employes != null ? toString(employes, maxLen) : null) + ", status=" + status + ", countryId="
            + countryId + "]";
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
