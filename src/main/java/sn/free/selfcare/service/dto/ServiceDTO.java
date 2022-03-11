package sn.free.selfcare.service.dto;

import io.swagger.annotations.ApiModel;
import javax.validation.constraints.*;
import java.io.Serializable;
import sn.free.selfcare.domain.enumeration.ObjectStatus;

/**
 * A DTO for the {@link sn.free.selfcare.domain.Service} entity.
 */
//TODO : MD : Audit : Exploiter Lombok pour dit clean code
@ApiModel(description = "Ligne entity.\n@author Alassane DIALLO")
public class ServiceDTO implements Serializable {
    
    private Long id;

    @NotNull
    private String serviceCode;

    private String serviceName;

    private String description;

    @NotNull
    private ObjectStatus status;

    private Boolean serviceDefault;

    
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getServiceCode() {
        return serviceCode;
    }

    public void setServiceCode(String serviceCode) {
        this.serviceCode = serviceCode;
    }

    public String getServiceName() {
        return serviceName;
    }

    public void setServiceName(String serviceName) {
        this.serviceName = serviceName;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public ObjectStatus getStatus() {
        return status;
    }

    public void setStatus(ObjectStatus status) {
        this.status = status;
    }

    public Boolean isServiceDefault() {
        return serviceDefault;
    }

    public void setServiceDefault(Boolean serviceDefault) {
        this.serviceDefault = serviceDefault;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof ServiceDTO)) {
            return false;
        }

        return id != null && id.equals(((ServiceDTO) o).id);
    }

    @Override
    public int hashCode() {
        return 31;
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "ServiceDTO{" +
            "id=" + getId() +
            ", serviceCode='" + getServiceCode() + "'" +
            ", serviceName='" + getServiceName() + "'" +
            ", description='" + getDescription() + "'" +
            ", status='" + getStatus() + "'" +
            ", serviceDefault='" + isServiceDefault() + "'" +
            "}";
    }
}
