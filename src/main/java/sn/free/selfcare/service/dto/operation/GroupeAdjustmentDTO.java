package sn.free.selfcare.service.dto.operation;

import java.io.Serializable;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import javax.validation.constraints.NotNull;

import io.swagger.annotations.ApiModel;

/**
 * A DTO for the {@link sn.free.selfcare.domain.Groupe} entity, adapted to be
 * used in automatic resource adjustements.
 */
//TODO : MD : Audit : Exploiter Lombok pour dit clean code
@ApiModel(description = "Groupe entity, adapted to be used in automatic resource adjustements.\n@author Ahmadou Diaw")
public class GroupeAdjustmentDTO implements Serializable {

	private Long id;

	private Long clientId;

	@NotNull
	private String numeroClient;

	@NotNull
	private Double credit;

	@NotNull
	private Double sms;

	@NotNull
	private Double voix;

	@NotNull
	private Double data;

	private Set<EmployeAdjustmentDTO> employes = new HashSet<>();

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Long getClientId() {
		return clientId;
	}

	public void setClientId(Long clientId) {
		this.clientId = clientId;
	}

	public Set<EmployeAdjustmentDTO> getEmployes() {
		return employes;
	}

	public void setEmployes(Set<EmployeAdjustmentDTO> employes) {
		this.employes = employes;
	}

	public String getNumeroClient() {
		return numeroClient;
	}

	public void setNumeroClient(String numeroClient) {
		this.numeroClient = numeroClient;
	}

	public Double getCredit() {
		return credit;
	}

	public void setCredit(Double credit) {
		this.credit = credit;
	}

	public Double getSms() {
		return sms;
	}

	public void setSms(Double sms) {
		this.sms = sms;
	}

	public Double getVoix() {
		return voix;
	}

	public void setVoix(Double voix) {
		this.voix = voix;
	}

	public Double getData() {
		return data;
	}

	public void setData(Double data) {
		this.data = data;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) {
			return true;
		}
		if (!(o instanceof GroupeAdjustmentDTO)) {
			return false;
		}

		return id != null && id.equals(((GroupeAdjustmentDTO) o).id);
	}

	@Override
	public int hashCode() {
		return 31;
	}

	@Override
	public String toString() {
		final int maxLen = 5;
		return "GroupeAdjustmentDTO [id=" + id + ", clientId=" + clientId + ", numeroClient=" + numeroClient
				+ ", credit=" + credit + ", sms=" + sms + ", voix=" + voix + ", data=" + data + ", employes="
				+ employes.size() + "]";
	}

	private String toString(Collection<?> collection, int maxLen) {
		StringBuilder builder = new StringBuilder();
		builder.append("[");
		int i = 0;
		for (Iterator<?> iterator = collection.iterator(); iterator.hasNext() && i < maxLen; i++) {
			if (i > 0) builder.append(", ");
			builder.append(iterator.next());
		}
		builder.append("]");
		return builder.toString();
	}
}
