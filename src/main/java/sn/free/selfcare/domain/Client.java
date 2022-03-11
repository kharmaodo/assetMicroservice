package sn.free.selfcare.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonManagedReference;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.Where;

import javax.persistence.*;
import javax.validation.constraints.*;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

import sn.free.selfcare.domain.enumeration.ObjectStatus;

/**
 * Client entity.\n@author Ahmadou Diaw
 */
//TODO : MD : Audit : Exploiter Lombok pour dit clean code
@Entity
@Table(name = "client")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@org.springframework.data.elasticsearch.annotations.Document(indexName = "client")
public class Client extends AbstractAuditingEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
	@Size(min = 4, max = 19)
    @Column(name = "code", nullable = false, unique = true)
    private String code;

    @NotBlank
    @Column(name = "raison_sociale", nullable = false, unique = true)
    private String raisonSociale;

    @NotBlank
    @Column(name = "ninea", nullable = false, unique = true)
    //TODO : MD : Audit : Privilégier le pattern pour les nineas sénégalais ou français ou ivoiriens
    //TODO : MD : Audit : faire les validations métiers Cf ninea DECRET N°2019-1240  12409_NIN 
    private String ninea;

    @Column(name = "registre_comm")
    private String registreComm;

    @NotBlank
	@Size(min = 9, max = 9)
	@Digits(fraction = 0, integer = 9)
    @Column(name = "numero_virtuel", nullable = false)
    private String numeroVirtuel;

    @Size(min = 0, max = 9)
    @Column(name = "numero_fm", nullable = false)
    private String numeroFM;


    @NotBlank
	@Size(max = 9)
	@Digits(fraction = 0, integer = 9)
    @Column(name = "telephone")
    //TODO : MD : Audit : Privilégier le pattern pour les telephones sénégalais ou français ou ivoiriens
    //TODO : MD :Audit : faire les validations métiers Cf telephone 000000 , 12345, 2009
    private String telephone;

    @NotBlank
	@Email
    @Column(name = "email")
    private String email;

    @NotBlank
    @Column(name = "adresse")
    private String adresse;

    @Column(name = "ville")
    private String ville;

    @NotBlank
	@Email
    @Column(name = "gestionnaire")
    private String gestionnaire;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private ObjectStatus status;

    @OneToMany(mappedBy = "client")
    @Where(clause = "status != 'ARCHIVED'")
    @JsonManagedReference
    private Set<Ligne> lignes = new HashSet<>();

    @OneToMany(mappedBy = "client")
    @Where(clause = "status != 'ARCHIVED'")
    @JsonManagedReference
    private Set<Groupe> groupes = new HashSet<>();

    @OneToMany(mappedBy = "client")
    @Where(clause = "status != 'ARCHIVED'")
    @JsonManagedReference
    private Set<Employe> employes = new HashSet<>();

    @OneToMany(mappedBy = "client")
    @Where(clause = "status != 'ARCHIVED'")
    @JsonManagedReference
    private Set<Produit> produitsPersonnalises = new HashSet<>();

    @ManyToOne
    @JsonIgnoreProperties(value = "clients", allowSetters = true)
    private Country country;

    @ManyToMany(mappedBy = "clients")
    @Where(clause = "status != 'ARCHIVED'")
    //@JsonIgnoreProperties(value = "clients", allowSetters = true)
    @JsonManagedReference
	private Set<Offre> offres = new HashSet<>();

    // jhipster-needle-entity-add-field - JHipster will add fields here
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getCode() {
        return code;
    }

    public Client code(String code) {
        this.code = code;
        return this;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getRaisonSociale() {
        return raisonSociale;
    }

    public Client raisonSociale(String raisonSociale) {
        this.raisonSociale = raisonSociale;
        return this;
    }

    public void setRaisonSociale(String raisonSociale) {
        this.raisonSociale = raisonSociale;
    }

    public String getNinea() {
        return ninea;
    }

    public Client ninea(String ninea) {
        this.ninea = ninea;
        return this;
    }

    public void setNinea(String ninea) {
        this.ninea = ninea;
    }

    public String getRegistreComm() {
        return registreComm;
    }

    public Client registreComm(String registreComm) {
        this.registreComm = registreComm;
        return this;
    }

    public void setRegistreComm(String registreComm) {
        this.registreComm = registreComm;
    }

    public String getNumeroVirtuel() {
        return numeroVirtuel;
    }

    public Client numeroVirtuel(String numeroVirtuel) {
        this.numeroVirtuel = numeroVirtuel;
        return this;
    }

    public void setNumeroVirtuel(String numeroVirtuel) {
        this.numeroVirtuel = numeroVirtuel;
    }

    public String getNumeroFM() {
        return numeroFM;
    }

    public Client numeroFM(String numeroFM) {
        this.numeroFM = numeroFM;
        return this;
    }

    public void setNumeroFM(String numeroFM) {
        this.numeroFM = numeroFM;
    }

    public String getTelephone() {
        return telephone;
    }

    public Client telephone(String telephone) {
        this.telephone = telephone;
        return this;
    }

    public void setTelephone(String telephone) {
        this.telephone = telephone;
    }

    public String getEmail() {
        return email;
    }

    public Client email(String email) {
        this.email = email;
        return this;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getAdresse() {
        return adresse;
    }

    public Client adresse(String adresse) {
        this.adresse = adresse;
        return this;
    }

    public void setAdresse(String adresse) {
        this.adresse = adresse;
    }

    public String getVille() {
        return ville;
    }

    public Client ville(String ville) {
        this.ville = ville;
        return this;
    }

    public void setVille(String ville) {
        this.ville = ville;
    }

    public String getGestionnaire() {
        return gestionnaire;
    }

    public Client gestionnaire(String gestionnaire) {
        this.gestionnaire = gestionnaire;
        return this;
    }

    public void setGestionnaire(String gestionnaire) {
        this.gestionnaire = gestionnaire;
    }

    public ObjectStatus getStatus() {
        return status;
    }

    public Client status(ObjectStatus status) {
        this.status = status;
        return this;
    }

    public void setStatus(ObjectStatus status) {
        this.status = status;
    }

    public Set<Ligne> getLignes() {
        return lignes;
    }

    public Client lignes(Set<Ligne> lignes) {
        this.lignes = lignes;
        return this;
    }

    public Client addLignes(Ligne ligne) {
        this.lignes.add(ligne);
        ligne.setClient(this);
        return this;
    }

    public Client removeLignes(Ligne ligne) {
        this.lignes.remove(ligne);
        ligne.setClient(null);
        return this;
    }

    public void setLignes(Set<Ligne> lignes) {
        this.lignes = lignes;
    }

    public Set<Groupe> getGroupes() {
        return groupes;
    }

    public Client groupes(Set<Groupe> groupes) {
        this.groupes = groupes;
        return this;
    }

    public Client addGroupes(Groupe groupe) {
        this.groupes.add(groupe);
        groupe.setClient(this);
        return this;
    }

    public Client removeGroupes(Groupe groupe) {
        this.groupes.remove(groupe);
        groupe.setClient(null);
        return this;
    }

    public void setGroupes(Set<Groupe> groupes) {
        this.groupes = groupes;
    }

    public Set<Employe> getEmployes() {
        return employes;
    }

    public Client employes(Set<Employe> employes) {
        this.employes = employes;
        return this;
    }

    public Client addEmployes(Employe employe) {
        this.employes.add(employe);
        employe.setClient(this);
        return this;
    }

    public Client removeEmployes(Employe employe) {
        this.employes.remove(employe);
        employe.setClient(null);
        return this;
    }

    public void setEmployes(Set<Employe> employes) {
        this.employes = employes;
    }

    public Set<Produit> getProduitsPersonnalises() {
        return produitsPersonnalises;
    }

    public Client produitsPersonnalises(Set<Produit> produits) {
        this.produitsPersonnalises = produits;
        return this;
    }

    public Client addProduitsPersonnalises(Produit produit) {
        this.produitsPersonnalises.add(produit);
        produit.setClient(this);
        return this;
    }

    public Client removeProduitsPersonnalises(Produit produit) {
        this.produitsPersonnalises.remove(produit);
        produit.setClient(null);
        return this;
    }

    public void setProduitsPersonnalises(Set<Produit> produits) {
        this.produitsPersonnalises = produits;
    }

    public Country getCountry() {
        return country;
    }

    public Client country(Country country) {
        this.country = country;
        return this;
    }

    public void setCountry(Country country) {
        this.country = country;
    }

    public Set<Offre> getOffres() {
        return offres;
    }

    public Client offres(Set<Offre> offres) {
        this.offres = offres;
        return this;
    }

    public Client addOffres(Offre offre) {
        this.offres.add(offre);
        offre.getClients().add(this);
        return this;
    }

    public Client removeOffres(Offre offre) {
        this.offres.remove(offre);
        offre.getClients().remove(this);
        return this;
    }

    public void setOffres(Set<Offre> offres) {
        this.offres = offres;
    }
    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Client)) {
            return false;
        }
        return id != null && id.equals(((Client) o).id);
    }

    @Override
    public int hashCode() {
        return 31;
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "Client{" +
            "id=" + getId() +
            ", code='" + getCode() + "'" +
            ", raisonSociale='" + getRaisonSociale() + "'" +
            ", ninea='" + getNinea() + "'" +
            ", registreComm='" + getRegistreComm() + "'" +
            ", numeroVirtuel='" + getNumeroVirtuel() + "'" +
            ", numeroFM='" + getNumeroFM() + "'" +
            ", telephone='" + getTelephone() + "'" +
            ", email='" + getEmail() + "'" +
            ", adresse='" + getAdresse() + "'" +
            ", ville='" + getVille() + "'" +
            ", gestionnaire='" + getGestionnaire() + "'" +
            ", status='" + getStatus() + "'" +
            "}";
    }
}
