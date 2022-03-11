package sn.free.selfcare.domain;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;

import javax.persistence.*;
import javax.validation.constraints.*;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

import sn.free.selfcare.domain.enumeration.TypeTarification;

import sn.free.selfcare.domain.enumeration.ObjectStatus;

/**
 * GrilleTarifaire entity.\n@author Ahmadou Diaw
 */
//TODO : MD : Audit : Exploiter Lombok pour dit clean code
@Entity
@Table(name = "grille_tarifaire")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@org.springframework.data.elasticsearch.annotations.Document(indexName = "grilletarifaire")
public class GrilleTarifaire implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "type_tarification", nullable = false)
    private TypeTarification typeTarification;

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

    @OneToMany(mappedBy = "grilleTarifaire")
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
    @JsonBackReference
    private Set<Offre> offres = new HashSet<>();

    // jhipster-needle-entity-add-field - JHipster will add fields here
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public TypeTarification getTypeTarification() {
        return typeTarification;
    }

    public GrilleTarifaire typeTarification(TypeTarification typeTarification) {
        this.typeTarification = typeTarification;
        return this;
    }

    public void setTypeTarification(TypeTarification typeTarification) {
        this.typeTarification = typeTarification;
    }

    public Double getSms() {
        return sms;
    }

    public GrilleTarifaire sms(Double sms) {
        this.sms = sms;
        return this;
    }

    public void setSms(Double sms) {
        this.sms = sms;
    }

    public Double getMinAppel() {
        return minAppel;
    }

    public GrilleTarifaire minAppel(Double minAppel) {
        this.minAppel = minAppel;
        return this;
    }

    public void setMinAppel(Double minAppel) {
        this.minAppel = minAppel;
    }

    public Double getGoData() {
        return goData;
    }

    public GrilleTarifaire goData(Double goData) {
        this.goData = goData;
        return this;
    }

    public void setGoData(Double goData) {
        this.goData = goData;
    }

    public ObjectStatus getStatus() {
        return status;
    }

    public GrilleTarifaire status(ObjectStatus status) {
        this.status = status;
        return this;
    }

    public void setStatus(ObjectStatus status) {
        this.status = status;
    }

    public Set<Offre> getOffres() {
        return offres;
    }

    public GrilleTarifaire offres(Set<Offre> offres) {
        this.offres = offres;
        return this;
    }

    public GrilleTarifaire addOffres(Offre offre) {
        this.offres.add(offre);
        offre.setGrilleTarifaire(this);
        return this;
    }

    public GrilleTarifaire removeOffres(Offre offre) {
        this.offres.remove(offre);
        offre.setGrilleTarifaire(null);
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
        if (!(o instanceof GrilleTarifaire)) {
            return false;
        }
        return id != null && id.equals(((GrilleTarifaire) o).id);
    }

    @Override
    public int hashCode() {
        return 31;
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "GrilleTarifaire{" +
            "id=" + getId() +
            ", typeTarification='" + getTypeTarification() + "'" +
            ", sms=" + getSms() +
            ", minAppel=" + getMinAppel() +
            ", goData=" + getGoData() +
            ", status='" + getStatus() + "'" +
            "}";
    }
}
