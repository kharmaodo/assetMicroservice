package sn.free.selfcare.service.dto.operation;

import io.swagger.annotations.ApiModel;
import javax.validation.constraints.*;
import java.io.Serializable;
import sn.free.selfcare.domain.enumeration.ObjectStatus;

/**
 * A DTO for the {@link sn.free.selfcare.domain.PeriodeEnvoi} entity.
 */
//TODO : MD : Audit : Exploiter Lombok pour dit clean code
@ApiModel(description = "PeriodeEnvoi entity.\n@author Ahmadou Diaw")
public class PeriodeEnvoiDTO implements Serializable {
    
    private Long id;

    @NotNull
    private String expression;

    @NotNull
    private Long groupeId;

    @NotNull
    private ObjectStatus status;

    
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getExpression() {
        return expression;
    }

    public void setExpression(String expression) {
        this.expression = expression;
    }

    public Long getGroupeId() {
        return groupeId;
    }

    public void setGroupeId(Long groupeId) {
        this.groupeId = groupeId;
    }

    public ObjectStatus getStatus() {
        return status;
    }

    public void setStatus(ObjectStatus status) {
        this.status = status;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof PeriodeEnvoiDTO)) {
            return false;
        }

        return id != null && id.equals(((PeriodeEnvoiDTO) o).id);
    }

    @Override
    public int hashCode() {
        return 31;
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "PeriodeEnvoiDTO{" +
            "id=" + getId() +
            ", expression='" + getExpression() + "'" +
            ", groupeId=" + getGroupeId() +
            ", status='" + getStatus() + "'" +
            "}";
    }
}
