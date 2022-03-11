package sn.free.selfcare.domain;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import javax.persistence.*;
import javax.validation.constraints.*;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

/**
 * Country entity.\n@author Ahmadou Diaw
 */
//TODO : MD : Audit : Exploiter Lombok pour dit clean code
@Entity
@Table(name = "country")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@org.springframework.data.elasticsearch.annotations.Document(indexName = "country")
public class Country implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    @Column(name = "nom", nullable = false)
    private String nom;

    @NotNull
    @Column(name = "code", nullable = false)
    private String code;

    @NotNull
    @Column(name = "indicatif", nullable = false)
    private String indicatif;

    @NotNull
    @Column(name = "devise", nullable = false)
    private String devise;

    @OneToMany(mappedBy = "country")
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
    private Set<Client> clients = new HashSet<>();

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

    public Country nom(String nom) {
        this.nom = nom;
        return this;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    public String getCode() {
        return code;
    }

    public Country code(String code) {
        this.code = code;
        return this;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getIndicatif() {
        return indicatif;
    }

    public Country indicatif(String indicatif) {
        this.indicatif = indicatif;
        return this;
    }

    public void setIndicatif(String indicatif) {
        this.indicatif = indicatif;
    }

    public String getDevise() {
        return devise;
    }

    public Country devise(String devise) {
        this.devise = devise;
        return this;
    }

    public void setDevise(String devise) {
        this.devise = devise;
    }

    public Set<Client> getClients() {
        return clients;
    }

    public Country clients(Set<Client> clients) {
        this.clients = clients;
        return this;
    }

    public Country addClient(Client client) {
        this.clients.add(client);
        client.setCountry(this);
        return this;
    }

    public Country removeClient(Client client) {
        this.clients.remove(client);
        client.setCountry(null);
        return this;
    }

    public void setClients(Set<Client> clients) {
        this.clients = clients;
    }
    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Country)) {
            return false;
        }
        return id != null && id.equals(((Country) o).id);
    }

    @Override
    public int hashCode() {
        return 31;
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "Country{" +
            "id=" + getId() +
            ", nom='" + getNom() + "'" +
            ", code='" + getCode() + "'" +
            ", indicatif='" + getIndicatif() + "'" +
            ", devise='" + getDevise() + "'" +
            "}";
    }
}
