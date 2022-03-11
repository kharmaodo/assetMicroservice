package sn.free.selfcare.service.dto;

import io.swagger.annotations.ApiModel;

import javax.validation.constraints.NotNull;
import java.io.Serializable;

/**
 * A DTO for the {@link sn.free.selfcare.domain.Service} entity.
 */
//TODO : MD : Audit : Exploiter Lombok pour dit clean code
@ApiModel(description = "Ligne entity.\n@author Alassane DIALLO")
public class ActivateDeactivateServiceDTO implements Serializable {

    @NotNull
    private String msisdn;

    private String imsi;

    @NotNull
    private Integer code;

    private String fnum;

    @NotNull
    private ServiceDTO service;

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

    public String getFnum() { return fnum; }

    public void setFnum(String fnum) { this.fnum = fnum; }

    public ServiceDTO getService() {
        return service;
    }

    public void setService(ServiceDTO service) {
        this.service = service;
    }


    @Override
    public int hashCode() {
        return 31;
    }

    @Override
    public String toString() {
        return "ActivateDeactivateServiceDTO{" +
            "msisdn='" + msisdn + '\'' +
            ", imsi='" + imsi + '\'' +
            ", code=" + code +
            ", fnum='" + fnum + '\'' +
            ", service=" + service +
            '}';
    }
}
