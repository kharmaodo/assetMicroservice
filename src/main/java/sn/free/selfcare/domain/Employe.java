package sn.free.selfcare.domain;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonManagedReference;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import javax.persistence.*;
import javax.validation.constraints.*;

import org.springframework.data.elasticsearch.annotations.FieldType;
import java.io.Serializable;

import sn.free.selfcare.domain.enumeration.ObjectStatus;

/**
 * Employe entity.\n@author Ahmadou Diaw
 */
//TODO : MD : Audit : Exploiter Lombok pour dit clean code
@Entity
@Table(name = "employe")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@org.springframework.data.elasticsearch.annotations.Document(indexName = "employe")
public class Employe implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    @Column(name = "nom", nullable = false)
    private String nom;

    @NotBlank
    @Column(name = "prenom", nullable = false)
    private String prenom;

    @Column(name = "fonction")
    private String fonction;

    @Email
    @Column(name = "email")
    private String email;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private ObjectStatus status;

    @OneToOne
    @JoinColumn(unique = true)
    @JsonManagedReference
    private Ligne ligne;

    @ManyToOne
    //@JsonIgnoreProperties(value = "employes", allowSetters = true)
    @JsonBackReference
    private Client client;

    @ManyToOne
    @JsonIgnoreProperties(value = "employes", allowSetters = true)
    private Groupe groupe;

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

    public Employe nom(String nom) {
        this.nom = nom;
        return this;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    public String getPrenom() {
        return prenom;
    }

    public Employe prenom(String prenom) {
        this.prenom = prenom;
        return this;
    }

    public void setPrenom(String prenom) {
        this.prenom = prenom;
    }

    public String getFonction() {
        return fonction;
    }

    public Employe fonction(String fonction) {
        this.fonction = fonction;
        return this;
    }

    public void setFonction(String fonction) {
        this.fonction = fonction;
    }

    public String getEmail() {
        return email;
    }

    public Employe email(String email) {
        this.email = email;
        return this;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public ObjectStatus getStatus() {
        return status;
    }

    public Employe status(ObjectStatus status) {
        this.status = status;
        return this;
    }

    public void setStatus(ObjectStatus status) {
        this.status = status;
    }

    public Ligne getLigne() {
        return ligne;
    }

    public Employe ligne(Ligne ligne) {
        this.ligne = ligne;
        return this;
    }

    public void setLigne(Ligne ligne) {
        this.ligne = ligne;
    }

    public Client getClient() {
        return client;
    }

    public Employe client(Client client) {
        this.client = client;
        return this;
    }

    public void setClient(Client client) {
        this.client = client;
    }

    public Groupe getGroupe() {
        return groupe;
    }

    public Employe groupe(Groupe groupe) {
        this.groupe = groupe;
        return this;
    }

    public void setGroupe(Groupe groupe) {
        this.groupe = groupe;
    }
    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Employe)) {
            return false;
        }
        return id != null && id.equals(((Employe) o).id);
    }

    @Override
    public int hashCode() {
        return 31;
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "Employe{" +
            "id=" + getId() +
            ", nom='" + getNom() + "'" +
            ", prenom='" + getPrenom() + "'" +
            ", fonction='" + getFonction() + "'" +
            ", email='" + getEmail() + "'" +
            ", status='" + getStatus() + "'" +
            "}";
    }
}
