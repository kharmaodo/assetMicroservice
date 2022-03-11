package sn.free.selfcare.domain;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.Where;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonManagedReference;

import sn.free.selfcare.domain.enumeration.ModePaiement;
import sn.free.selfcare.domain.enumeration.ObjectStatus;
import sn.free.selfcare.domain.enumeration.TypeOffre;

/**
 * Offre entity.\n@author Ahmadou Diaw
 */
//TODO : MD : Audit : Exploiter Lombok pour dit clean code
@Entity
@Table(name = "offre")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@org.springframework.data.elasticsearch.annotations.Document(indexName = "offre")
public class Offre implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "code")
    private String code;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "type_offre", nullable = false)
    private TypeOffre typeOffre;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "mode_paiement", nullable = false)
    private ModePaiement modePaiement;

    @NotNull
    @Column(name = "montant", nullable = false)
    private Double montant;

    @Column(name = "duree_engagement")
    private Integer dureeEngagement;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private ObjectStatus status;

    @OneToMany(mappedBy = "offre")
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
    @Where(clause = "status != 'ARCHIVED'")
    @JsonManagedReference
    private Set<Ligne> lignes = new HashSet<>();

    @ManyToMany
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
    @JoinTable(name = "offre_clients",
               joinColumns = @JoinColumn(name = "offre_id", referencedColumnName = "id"),
               inverseJoinColumns = @JoinColumn(name = "clients_id", referencedColumnName = "id"))
    @Where(clause = "status != 'ARCHIVED'")
    @JsonBackReference
    private Set<Client> clients = new HashSet<>();

    @ManyToMany
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
    @JoinTable(name = "offre_produits",
               joinColumns = @JoinColumn(name = "offre_id", referencedColumnName = "id"),
               inverseJoinColumns = @JoinColumn(name = "produits_id", referencedColumnName = "id"))
    @Where(clause = "status != 'ARCHIVED'")
    @JsonManagedReference
    private Set<Produit> produits = new HashSet<>();

    @ManyToOne
    @JsonIgnoreProperties(value = "offres", allowSetters = true)
    @Where(clause = "status != 'ARCHIVED'")
    @JsonManagedReference
    private GrilleTarifaire grilleTarifaire;

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

    public Offre code(String code) {
        this.code = code;
        return this;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public TypeOffre getTypeOffre() {
        return typeOffre;
    }

    public Offre typeOffre(TypeOffre typeOffre) {
        this.typeOffre = typeOffre;
        return this;
    }

    public void setTypeOffre(TypeOffre typeOffre) {
        this.typeOffre = typeOffre;
    }

    public ModePaiement getModePaiement() {
        return modePaiement;
    }

    public Offre modePaiement(ModePaiement modePaiement) {
        this.modePaiement = modePaiement;
        return this;
    }

    public void setModePaiement(ModePaiement modePaiement) {
        this.modePaiement = modePaiement;
    }

    public Double getMontant() {
        return montant;
    }

    public Offre montant(Double montant) {
        this.montant = montant;
        return this;
    }

    public void setMontant(Double montant) {
        this.montant = montant;
    }

    public Integer getDureeEngagement() {
        return dureeEngagement;
    }

    public Offre dureeEngagement(Integer dureeEngagement) {
        this.dureeEngagement = dureeEngagement;
        return this;
    }

    public void setDureeEngagement(Integer dureeEngagement) {
        this.dureeEngagement = dureeEngagement;
    }

    public ObjectStatus getStatus() {
        return status;
    }

    public Offre status(ObjectStatus status) {
        this.status = status;
        return this;
    }

    public void setStatus(ObjectStatus status) {
        this.status = status;
    }

    public Set<Ligne> getLignes() {
        return lignes;
    }

    public Offre lignes(Set<Ligne> lignes) {
        this.lignes = lignes;
        return this;
    }

    public Offre addLignes(Ligne ligne) {
        this.lignes.add(ligne);
        ligne.setOffre(this);
        return this;
    }

    public Offre removeLignes(Ligne ligne) {
        this.lignes.remove(ligne);
        ligne.setOffre(null);
        return this;
    }

    public void setLignes(Set<Ligne> lignes) {
        this.lignes = lignes;
    }

    public Set<Client> getClients() {
        return clients;
    }

    public Offre clients(Set<Client> clients) {
        this.clients = clients;
        return this;
    }

    public Offre addClients(Client client) {
        this.clients.add(client);
        client.getOffres().add(this);
        return this;
    }

    public Offre removeClients(Client client) {
        this.clients.remove(client);
        client.getOffres().remove(this);
        return this;
    }

    public void setClients(Set<Client> clients) {
        this.clients = clients;
    }

    public Set<Produit> getProduits() {
        return produits;
    }

    public Offre produits(Set<Produit> produits) {
        this.produits = produits;
        return this;
    }

    public Offre addProduits(Produit produit) {
        this.produits.add(produit);
        produit.getOffres().add(this);
        return this;
    }

    public Offre removeProduits(Produit produit) {
        this.produits.remove(produit);
        produit.getOffres().remove(this);
        return this;
    }

    public void setProduits(Set<Produit> produits) {
        this.produits = produits;
    }

    public GrilleTarifaire getGrilleTarifaire() {
        return grilleTarifaire;
    }

    public Offre grilleTarifaire(GrilleTarifaire grilleTarifaire) {
        this.grilleTarifaire = grilleTarifaire;
        return this;
    }

    public void setGrilleTarifaire(GrilleTarifaire grilleTarifaire) {
        this.grilleTarifaire = grilleTarifaire;
    }
    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Offre)) {
            return false;
        }
        return id != null && id.equals(((Offre) o).id);
    }

    @Override
    public int hashCode() {
        return 31;
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "Offre{" +
            "id=" + getId() +
            ", code='" + getCode() + "'" +
            ", typeOffre='" + getTypeOffre() + "'" +
            ", modePaiement='" + getModePaiement() + "'" +
            ", montant=" + getMontant() +
            ", dureeEngagement=" + getDureeEngagement() +
            ", status='" + getStatus() + "'" +
            "}";
    }
}
