package sn.free.selfcare.service.dto;

import io.swagger.annotations.ApiModel;
import javax.validation.constraints.*;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;
import sn.free.selfcare.domain.enumeration.ObjectStatus;

/**
 * A DTO for the {@link sn.free.selfcare.domain.Ligne} entity.
 */
//TODO : MD : Audit : Exploiter Lombok pour dit clean code
@ApiModel(description = "Ligne entity.\n@author Ahmadou Diaw")
public class LigneDTO implements Serializable {

    private Long id;

    @NotBlank
    @Size(min = 9, max = 9)
    @Digits(fraction = 0, integer = 9)
    @Pattern(regexp="^(75|76|77|78|32|70).{7}$")
    private String numero;

    @NotBlank
    @Size(min = 15, max = 15)
    @Digits(fraction = 0, integer = 15)
    @Pattern(regexp="^(60802).{10}$")
    private String imsi;

    @NotNull
    private ObjectStatus status;


    private Long clientId;

    private Long offreId;

    private Set<ServiceDTO> services = new HashSet<>();

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNumero() {
        return numero;
    }

    public void setNumero(String numero) {
        this.numero = numero;
    }

    public String getImsi() {
        return imsi;
    }

    public void setImsi(String imsi) {
        this.imsi = imsi;
    }

    public ObjectStatus getStatus() {
        return status;
    }

    public void setStatus(ObjectStatus status) {
        this.status = status;
    }

    public Long getClientId() {
        return clientId;
    }

    public void setClientId(Long clientId) {
        this.clientId = clientId;
    }

    public Long getOffreId() {
        return offreId;
    }

    public void setOffreId(Long offreId) {
        this.offreId = offreId;
    }

    public Set<ServiceDTO> getServices() {
        return services;
    }

    public void setServices(Set<ServiceDTO> services) {
        this.services = services;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof LigneDTO)) {
            return false;
        }

        LigneDTO lo = (LigneDTO) o;
        return ((id != null && id.equals(lo.id)) || ((numero != null && numero.equals(lo.getNumero()))) || (imsi != null && imsi.equals(lo.getImsi())));
    }

    @Override
    public int hashCode() {
        return 31;
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "LigneDTO{" +
            "id=" + getId() +
            ", numero='" + getNumero() + "'" +
            ", imsi='" + getImsi() + "'" +
            ", status='" + getStatus() + "'" +
            ", clientId=" + getClientId() +
            ", offreId=" + getOffreId() +
            ", services='" + getServices() + "'" +
            "}";
    }
}
