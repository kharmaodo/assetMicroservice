package sn.free.selfcare.domain;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.*;
import javax.validation.constraints.Digits;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import sn.free.selfcare.domain.enumeration.ObjectStatus;

/**
 * Ligne entity.\n@author Ahmadou Diaw
 */
//TODO : MD : Audit : Exploiter Lombok pour dit clean code
@Entity
@Table(name = "ligne")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@org.springframework.data.elasticsearch.annotations.Document(indexName = "ligne")
public class Ligne implements Serializable {

	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@NotBlank
    @Size(min = 9, max = 9)
    @Digits(fraction = 0, integer = 9)
	@Pattern(regexp="^(75|76|77|78|32|70).{7}$")
	@Column(name = "numero", nullable = false, unique = true)
	private String numero;

	@NotBlank
    @Size(min = 15, max = 15)
    @Digits(fraction = 0, integer = 15)
	@Pattern(regexp="^(60802).{10}$")
	@Column(name = "imsi", nullable = false, unique = true)
	private String imsi;

	@NotNull
	@Enumerated(EnumType.STRING)
	@Column(name = "status", nullable = false)
	private ObjectStatus status;

	@OneToOne(mappedBy = "ligne")
	@JsonIgnore
	private Employe employe;

	//TODO : MD : Audit : Casser les manyToOne dans une base NoSQL
	@ManyToOne
	//@JsonIgnoreProperties(value = "lignes", allowSetters = true)
	@JsonBackReference
	private Client client;
	//TODO : MD : Audit : Casser les manyToOne dans une base NoSQL
	@ManyToOne
	//@JsonIgnoreProperties(value = "lignes", allowSetters = true)
	@JsonBackReference
	private Offre offre;

    @ManyToMany
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
    @JoinTable(name = "ligne_services",
               joinColumns = @JoinColumn(name = "ligne_id", referencedColumnName = "id"),
               inverseJoinColumns = @JoinColumn(name = "services_id", referencedColumnName = "id"))
    @JsonManagedReference
    private Set<Service> services = new HashSet<>();

	// jhipster-needle-entity-add-field - JHipster will add fields here
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getNumero() {
		return numero;
	}

	public Ligne numero(String numero) {
		this.numero = numero;
		return this;
	}

	public void setNumero(String numero) {
		this.numero = numero;
	}

	public String getImsi() {
		return imsi;
	}

	public Ligne imsi(String imsi) {
		this.imsi = imsi;
		return this;
	}

	public void setImsi(String imsi) {
		this.imsi = imsi;
	}

	public ObjectStatus getStatus() {
		return status;
	}

	public Ligne status(ObjectStatus status) {
		this.status = status;
		return this;
	}

	public void setStatus(ObjectStatus status) {
		this.status = status;
	}

	public Employe getEmploye() {
		return employe;
	}

	public Ligne employe(Employe employe) {
		this.employe = employe;
		return this;
	}

	public void setEmploye(Employe employe) {
		this.employe = employe;
	}

	public Client getClient() {
		return client;
	}

	public Ligne client(Client client) {
		this.client = client;
		return this;
	}

	public void setClient(Client client) {
		this.client = client;
	}

	public Offre getOffre() {
		return offre;
	}

	public Ligne offre(Offre offre) {
		this.offre = offre;
		return this;
	}

	public void setOffre(Offre offre) {
		this.offre = offre;
	}

    public Set<Service> getServices() {
        return services;
    }

    public Ligne services(Set<Service> services) {
        this.services = services;
        return this;
    }

    public Ligne addServices(Service service) {
        this.services.add(service);
        service.getLignes().add(this);
        return this;
    }

    public Ligne removeServices(Service service) {
        this.services.remove(service);
        service.getLignes().remove(this);
        return this;
    }

    public void setServices(Set<Service> services) {
        this.services = services;
    }
    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

	@Override
	public boolean equals(Object o) {
		if (this == o) {
			return true;
		}
		if (!(o instanceof Ligne)) {
			return false;
		}
		return id != null && id.equals(((Ligne) o).id);
	}

	@Override
	public int hashCode() {
		return 31;
	}

	// prettier-ignore
	@Override
	public String toString() {
		return "Ligne{" + "id=" + getId() + ", numero='" + getNumero() + "'" + ", imsi='" + getImsi() + "'"
				+ ", status='" + getStatus() + "'" + "}";
	}
}
