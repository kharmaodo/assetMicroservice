package sn.free.selfcare.web.rest.kpi;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import sn.free.selfcare.service.ClientService;
import sn.free.selfcare.service.ProduitService;
import sn.free.selfcare.service.dto.NumberClientActiveKpiDTO;
import sn.free.selfcare.service.dto.NumberClientKpiDTO;
import sn.free.selfcare.service.dto.NumberProductKpiDTO;

import java.util.List;

@RestController
@RequestMapping("/kpi")
public class AssetKPIController {

    @Autowired
    ClientService clientService;

    @Autowired
    ProduitService produitService;

    @GetMapping("/numberOfClients")
    public List<NumberClientKpiDTO> getNumberOfClients(@RequestParam(name = "year", required = true) int year) {
        return clientService.getNumberOfClientsPerMonth(year);
    }

    @GetMapping("/numberOfActiveClients")
    public List<NumberClientActiveKpiDTO> getNumberOfActiveClients(@RequestParam(name = "year", required = true) int year) {
        return clientService.getNumberOfActiveClientsPerMonth(year);
    }

    @GetMapping("/numberOfProductsPerMonth")
    public List<NumberProductKpiDTO> getNumberOfProductsPerMonth(@RequestParam(name = "year", required = true) int year) {
        return produitService.getNumberOfProductsPerMonth(year);
    }

    @GetMapping("/activeClientsIdList")
    public List<Long> getActiveClientsIdList() {
        return clientService.getActiveClientsIdList();
    }
}
