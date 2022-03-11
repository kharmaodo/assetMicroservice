package sn.free.selfcare.service.dto.operation;

import java.io.Serializable;

import io.swagger.annotations.ApiModel;

/**
 * A DTO for the {@link sn.free.selfcare.domain.Employe} entity, adapted to be
 * used in automatic resource adjustements.
 */
//TODO : MD : Audit : Exploiter Lombok pour dit clean code
@ApiModel(description = "Employe entity, adapted to be used in automatic resource adjustements.\n@author Ahmadou Diaw")
public class EmployeAdjustmentDTO implements Serializable {

	private Long id;

	private String numero;

	private Double tarifCredit;

	private Double tarifSms;

	private Double tarifVoix;

	private Double tarifData;

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

	public Double getTarifCredit() {
		return tarifCredit;
	}

	public void setTarifCredit(Double tarifCredit) {
		this.tarifCredit = tarifCredit;
	}

	public Double getTarifSms() {
		return tarifSms;
	}

	public void setTarifSms(Double tarifSms) {
		this.tarifSms = tarifSms;
	}

	public Double getTarifVoix() {
		return tarifVoix;
	}

	public void setTarifVoix(Double tarifVoix) {
		this.tarifVoix = tarifVoix;
	}

	public Double getTarifData() {
		return tarifData;
	}

	public void setTarifData(Double tarifData) {
		this.tarifData = tarifData;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) {
			return true;
		}
		if (!(o instanceof EmployeAdjustmentDTO)) {
			return false;
		}

		return id != null && id.equals(((EmployeAdjustmentDTO) o).id);
	}

	@Override
	public int hashCode() {
		return 31;
	}

	@Override
	public String toString() {
		return "EmployeAdjustmentDTO [id=" + id + ", numero=" + numero + ", tarifCredit=" + tarifCredit + ", tarifSms="
				+ tarifSms + ", tarifVoix=" + tarifVoix + ", tarifData=" + tarifData + "]";
	}
}
