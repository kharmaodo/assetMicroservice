package sn.free.selfcare.client;

import javax.validation.Valid;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import sn.free.selfcare.service.dto.operation.PeriodeEnvoiDTO;
import sn.free.selfcare.service.dto.operation.SimpleAdjustOrderDTO;

@AuthorizedFeignClient(name = "operationmicroservice")
public interface OperationFeignClient {

	@RequestMapping(method = RequestMethod.POST, value = "/api/periode-envois")
	void createPeriodeEnvoi(PeriodeEnvoiDTO periode);

	@PostMapping("/api/adjust/simple")
	public ResponseEntity<Void> simpleAdjustAccount(@Valid @RequestBody SimpleAdjustOrderDTO adjustOrderDTO);

	@RequestMapping(method = RequestMethod.DELETE, value = "/api/alerts/client/{clientId}")
    void deleteClientAlerts(@PathVariable Long clientId);
}
