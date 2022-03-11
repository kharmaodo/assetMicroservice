package sn.free.selfcare.domain;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonManagedReference;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.Where;

import javax.persistence.*;
import javax.validation.constraints.*;

import org.springframework.data.elasticsearch.annotations.FieldType;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

import sn.free.selfcare.domain.enumeration.TypeOffre;

import sn.free.selfcare.domain.enumeration.ObjectStatus;

/**
 * Produit entity.\n@author Ahmadou Diaw
 */
//TODO : MD : Audit : Exploiter Lombok pour dit clean code
@Entity
@Table(name = "produit")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@org.springframework.data.elasticsearch.annotations.Document(indexName = "produit")
public class Produit extends AbstractAuditingEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    @Column(name = "nom", nullable = false)
    private String nom;

    @Column(name = "description")
    private String description;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "type_produit", nullable = false)
    private TypeOffre typeProduit;

    @NotNull
    @Column(name = "credit", nullable = false)
    private Double credit;

    @NotNull
    @Column(name = "sms", nullable = false)
    private Double sms;

    @NotNull
    @Column(name = "min_appel", nullable = false)
    private Double minAppel;

    @NotNull
    @Column(name = "go_data", nullable = false)
    private Double goData;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private ObjectStatus status;

    @OneToMany(mappedBy = "produit")
    @Where(clause = "status != 'ARCHIVED'")
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
    @JsonManagedReference
    private Set<Groupe> groupes = new HashSet<>();

    @ManyToOne
    //@JsonIgnoreProperties(value = "produitsPersonnalises", allowSetters = true)
    @JsonBackReference
    private Client client;

    @ManyToMany(mappedBy = "produits")
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
    @JsonIgnore
    private Set<Offre> offres = new HashSet<>();

    // jhipster-needle-entity-add-field - JHipster will add fields here
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNom() {
        return nom;
    }

    public Produit nom(String nom) {
        this.nom = nom;
        return this;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    public String getDescription() {
        return description;
    }

    public Produit description(String description) {
        this.description = description;
        return this;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public TypeOffre getTypeProduit() {
        return typeProduit;
    }

    public Produit typeProduit(TypeOffre typeProduit) {
        this.typeProduit = typeProduit;
        return this;
    }

    public void setTypeProduit(TypeOffre typeProduit) {
        this.typeProduit = typeProduit;
    }

    public Double getCredit() {
        return credit;
    }

    public Produit credit(Double credit) {
        this.credit = credit;
        return this;
    }

    public void setCredit(Double credit) {
        this.credit = credit;
    }

    public Double getSms() {
        return sms;
    }

    public Produit sms(Double sms) {
        this.sms = sms;
        return this;
    }

    public void setSms(Double sms) {
        this.sms = sms;
    }

    public Double getMinAppel() {
        return minAppel;
    }

    public Produit minAppel(Double minAppel) {
        this.minAppel = minAppel;
        return this;
    }

    public void setMinAppel(Double minAppel) {
        this.minAppel = minAppel;
    }

    public Double getGoData() {
        return goData;
    }

    public Produit goData(Double goData) {
        this.goData = goData;
        return this;
    }

    public void setGoData(Double goData) {
        this.goData = goData;
    }

    public ObjectStatus getStatus() {
        return status;
    }

    public Produit status(ObjectStatus status) {
        this.status = status;
        return this;
    }

    public void setStatus(ObjectStatus status) {
        this.status = status;
    }

    public Set<Groupe> getGroupes() {
        return groupes;
    }

    public Produit groupes(Set<Groupe> groupes) {
        this.groupes = groupes;
        return this;
    }

    public Produit addGroupes(Groupe groupe) {
        this.groupes.add(groupe);
        groupe.setProduit(this);
        return this;
    }

    public Produit removeGroupes(Groupe groupe) {
        this.groupes.remove(groupe);
        groupe.setProduit(null);
        return this;
    }

    public void setGroupes(Set<Groupe> groupes) {
        this.groupes = groupes;
    }

    public Client getClient() {
        return client;
    }

    public Produit client(Client client) {
        this.client = client;
        return this;
    }

    public void setClient(Client client) {
        this.client = client;
    }

    public Set<Offre> getOffres() {
        return offres;
    }

    public Produit offres(Set<Offre> offres) {
        this.offres = offres;
        return this;
    }

    public Produit addOffres(Offre offre) {
        this.offres.add(offre);
        offre.getProduits().add(this);
        return this;
    }

    public Produit removeOffres(Offre offre) {
        this.offres.remove(offre);
        offre.getProduits().remove(this);
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
        if (!(o instanceof Produit)) {
            return false;
        }
        return id != null && id.equals(((Produit) o).id);
    }

    @Override
    public int hashCode() {
        return 31;
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "Produit{" +
            "id=" + getId() +
            ", nom='" + getNom() + "'" +
            ", description='" + getDescription() + "'" +
            ", typeProduit='" + getTypeProduit() + "'" +
            ", credit=" + getCredit() +
            ", sms=" + getSms() +
            ", minAppel=" + getMinAppel() +
            ", goData=" + getGoData() +
            ", status='" + getStatus() + "'" +
            "}";
    }
}
