package sn.free.selfcare.service.dto.operation;

import java.io.Serializable;

import javax.validation.constraints.NotNull;
//TODO : MD : Audit : Exploiter Lombok pour dit clean code
public class SimpleAdjustOrderDTO implements Serializable {

	@NotNull
	private String numeroClient;

	@NotNull
	private Double stock;

	public String getNumeroClient() {
		return numeroClient;
	}

	public void setNumeroClient(String numeroClient) {
		this.numeroClient = numeroClient;
	}

	public Double getStock() {
		return stock;
	}

	public void setStock(Double stock) {
		this.stock = stock;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((numeroClient == null) ? 0 : numeroClient.hashCode());
		result = prime * result + ((stock == null) ? 0 : stock.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) return true;
		if (obj == null) return false;
		if (getClass() != obj.getClass()) return false;
		SimpleAdjustOrderDTO other = (SimpleAdjustOrderDTO) obj;
		if (numeroClient == null) {
			if (other.numeroClient != null) return false;
		}
		else if (!numeroClient.equals(other.numeroClient)) return false;
		if (stock == null) {
			if (other.stock != null) return false;
		}
		else if (!stock.equals(other.stock)) return false;
		return true;
	}

	@Override
	public String toString() {
		return "SimpleAdjustOrderDTO [numeroClient=" + numeroClient + ", stock=" + stock + "]";
	}

}
