package sn.free.selfcare.service.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.annotations.ApiModel;

import javax.validation.constraints.NotNull;
import java.io.Serializable;

/**
 * A DTO for the {@link sn.free.selfcare.domain.Service} entity.
 */
//TODO : MD : Audit : Exploiter Lombok pour dit clean code
@ApiModel(description = "Ligne entity.\n@author Alassane DIALLO")
public class ActivateDeactivateServiceRequestDTO implements Serializable {

    @NotNull
    private String msisdn;

    private String imsi;

    @NotNull
    private Integer code;

    @NotNull
    private String serviceName;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String fnum;

    public String getMsisdn() {
        return msisdn;
    }

    public void setMsisdn(String msisdn) {
        this.msisdn = msisdn;
    }

    public String getImsi() {
        return imsi;
    }

    public void setImsi(String imsi) {
        this.imsi = imsi;
    }

    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }

    public String getServiceName() {
        return serviceName;
    }

    public void setServiceName(String serviceName) {
        this.serviceName = serviceName;
    }

    public String getFnum() {
        return fnum;
    }

    public void setFnum(String fnum) {
        this.fnum = fnum;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof ActivateDeactivateServiceRequestDTO)) {
            return false;
        }

        return serviceName != null && serviceName.equals(((ActivateDeactivateServiceRequestDTO) o).serviceName);
    }

    @Override
    public int hashCode() {
        return 31;
    }

    @Override
    public String toString() {
        return "ActivateDeactivateServiceRequestDTO{" +
            "msisdn='" + msisdn + '\'' +
            ", imsi='" + imsi + '\'' +
            ", code=" + code +
            ", serviceName='" + serviceName + '\'' +
            ", fnum='" + fnum + '\'' +
            '}';
    }
}
