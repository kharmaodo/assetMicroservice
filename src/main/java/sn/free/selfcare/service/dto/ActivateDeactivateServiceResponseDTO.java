package sn.free.selfcare.service.dto;

import io.swagger.annotations.ApiModel;

import javax.validation.constraints.NotNull;
import java.io.Serializable;

/**
 * A DTO for the {@link sn.free.selfcare.domain.Service} entity.
 */
//TODO : MD : Audit : Exploiter Lombok pour dit clean code
@ApiModel(description = "Ligne entity.\n@author Alassane DIALLO")
public class ActivateDeactivateServiceResponseDTO implements Serializable {

    @NotNull
    private String serviceCode;

    @NotNull
    private Integer respCode;

    private String respDesc;

    public String getServiceCode() {
        return serviceCode;
    }

    public void setServiceCode(String serviceCode) {
        this.serviceCode = serviceCode;
    }

    public Integer getRespCode() {
        return respCode;
    }

    public void setRespCode(Integer respCode) {
        this.respCode = respCode;
    }

    public String getRespDesc() {
        return respDesc;
    }

    public void setRespDesc(String respDesc) {
        this.respDesc = respDesc;
    }

    @Override
    public int hashCode() {
        return 31;
    }

    @Override
    public String toString() {
        return "ActivateDeactivateServiceResponseDTO{" +
            "serviceCode='" + serviceCode + '\'' +
            ", respCode=" + respCode +
            ", respDesc='" + respDesc + '\'' +
            '}';
    }
}
