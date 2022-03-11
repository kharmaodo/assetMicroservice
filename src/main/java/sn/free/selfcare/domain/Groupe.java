package sn.free.selfcare.domain;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.Where;

import javax.persistence.*;
import javax.validation.constraints.*;

import org.springframework.data.elasticsearch.annotations.FieldType;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

import sn.free.selfcare.domain.enumeration.Periodicite;

import sn.free.selfcare.domain.enumeration.ObjectStatus;

/**
 * Groupe entity.\n@author Ahmadou Diaw
 */
//TODO : MD : Audit : Exploiter Lombok pour dit clean code
@Entity
@Table(name = "groupe")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@org.springframework.data.elasticsearch.annotations.Document(indexName = "groupe")
public class Groupe implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    @Column(name = "nom", nullable = false, unique = true)
    private String nom;

    @Column(name = "description")
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(name = "periodicite")
    private Periodicite periodicite;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private ObjectStatus status;

    @OneToMany(mappedBy = "groupe")
    @Where(clause = "status != 'ARCHIVED'")
    @JsonIgnoreProperties(value = { "groupe", "client" })
	private Set<Employe> employes = new HashSet<>();

	@Transient
	private Integer nbEmployes = 0;

    @ManyToOne
    //@JsonIgnoreProperties(value = "groupes", allowSetters = true)
    @JsonBackReference
    private Client client;

    @NotNull
    @ManyToOne
    //@JsonIgnoreProperties(value = "groupes", allowSetters = true)
    @JsonBackReference
    private Produit produit;

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

    public Groupe nom(String nom) {
        this.nom = nom;
        return this;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    public String getDescription() {
        return description;
    }

    public Groupe description(String description) {
        this.description = description;
        return this;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Periodicite getPeriodicite() {
        return periodicite;
    }

    public Groupe periodicite(Periodicite periodicite) {
        this.periodicite = periodicite;
        return this;
    }

    public void setPeriodicite(Periodicite periodicite) {
        this.periodicite = periodicite;
    }

    public ObjectStatus getStatus() {
        return status;
    }

    public Groupe status(ObjectStatus status) {
        this.status = status;
        return this;
    }

    public void setStatus(ObjectStatus status) {
        this.status = status;
    }

    public Set<Employe> getEmployes() {
        return employes;
    }

    public Groupe employes(Set<Employe> employes) {
        this.employes = employes;
		this.resetNbEmployes();
        return this;
    }

    public Groupe addEmployes(Employe employe) {
        this.employes.add(employe);
        employe.setGroupe(this);
		this.resetNbEmployes();
        return this;
    }

    public Groupe removeEmployes(Employe employe) {
        this.employes.remove(employe);
        employe.setGroupe(null);
		this.resetNbEmployes();
        return this;
    }

    public void setEmployes(Set<Employe> employes) {
        this.employes = employes;
		this.resetNbEmployes();
    }

    public Integer getNbEmployes() {
		return nbEmployes;
	}

	@PostLoad
	@PostUpdate
	public void resetNbEmployes() {
		this.nbEmployes = this.employes.size();
	}

    public Client getClient() {
        return client;
    }

    public Groupe client(Client client) {
        this.client = client;
        return this;
    }

    public void setClient(Client client) {
        this.client = client;
    }

    public Produit getProduit() {
        return produit;
    }

    public Groupe produit(Produit produit) {
        this.produit = produit;
        return this;
    }

    public void setProduit(Produit produit) {
        this.produit = produit;
    }
    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Groupe)) {
            return false;
        }
        return id != null && id.equals(((Groupe) o).id);
    }

    @Override
    public int hashCode() {
        return 31;
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "Groupe{" +
            "id=" + getId() +
            ", nom='" + getNom() + "'" +
            ", description='" + getDescription() + "'" +
            ", periodicite='" + getPeriodicite() + "'" +
            ", status='" + getStatus() + "'" +
            "}";
    }
}
