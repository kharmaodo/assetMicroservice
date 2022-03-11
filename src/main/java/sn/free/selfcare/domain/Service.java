package sn.free.selfcare.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import javax.persistence.*;
import javax.validation.constraints.*;

import org.springframework.data.elasticsearch.annotations.FieldType;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

import sn.free.selfcare.domain.enumeration.ObjectStatus;

/**
 * Ligne entity.\n@author Alassane DIALLO
 */
//TODO : MD : Audit : Exploiter Lombok pour dit clean code
@Entity
@Table(name = "service")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@org.springframework.data.elasticsearch.annotations.Document(indexName = "service")
public class Service implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    @Column(name = "service_code", nullable = false)
    private String serviceCode;

    @Column(name = "service_name")
    private String serviceName;

    @Column(name = "description")
    private String description;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private ObjectStatus status;

    @Column(name = "service_default")
    private Boolean serviceDefault;

    @ManyToMany(mappedBy = "services")
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
    @JsonIgnore
    private Set<Ligne> lignes = new HashSet<>();

    // jhipster-needle-entity-add-field - JHipster will add fields here
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getServiceCode() {
        return serviceCode;
    }

    public Service serviceCode(String serviceCode) {
        this.serviceCode = serviceCode;
        return this;
    }

    public void setServiceCode(String serviceCode) {
        this.serviceCode = serviceCode;
    }

    public String getServiceName() {
        return serviceName;
    }

    public Service serviceName(String serviceName) {
        this.serviceName = serviceName;
        return this;
    }

    public void setServiceName(String serviceName) {
        this.serviceName = serviceName;
    }

    public String getDescription() {
        return description;
    }

    public Service description(String description) {
        this.description = description;
        return this;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public ObjectStatus getStatus() {
        return status;
    }

    public Service status(ObjectStatus status) {
        this.status = status;
        return this;
    }

    public void setStatus(ObjectStatus status) {
        this.status = status;
    }

    public Boolean isServiceDefault() {
        return serviceDefault;
    }

    public Service serviceDefault(Boolean serviceDefault) {
        this.serviceDefault = serviceDefault;
        return this;
    }

    public void setServiceDefault(Boolean serviceDefault) {
        this.serviceDefault = serviceDefault;
    }

    public Set<Ligne> getLignes() {
        return lignes;
    }

    public Service lignes(Set<Ligne> lignes) {
        this.lignes = lignes;
        return this;
    }

    public Service addLignes(Ligne ligne) {
        this.lignes.add(ligne);
        ligne.getServices().add(this);
        return this;
    }

    public Service removeLignes(Ligne ligne) {
        this.lignes.remove(ligne);
        ligne.getServices().remove(this);
        return this;
    }

    public void setLignes(Set<Ligne> lignes) {
        this.lignes = lignes;
    }
    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Service)) {
            return false;
        }
        return id != null && id.equals(((Service) o).id);
    }

    @Override
    public int hashCode() {
        return 31;
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "Service{" +
            "id=" + getId() +
            ", serviceCode='" + getServiceCode() + "'" +
            ", serviceName='" + getServiceName() + "'" +
            ", description='" + getDescription() + "'" +
            ", status='" + getStatus() + "'" +
            ", serviceDefault='" + isServiceDefault() + "'" +
            "}";
    }
}
