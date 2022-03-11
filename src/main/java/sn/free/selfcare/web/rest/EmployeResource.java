package sn.free.selfcare.web.rest;

import io.github.jhipster.web.util.HeaderUtil;
import io.github.jhipster.web.util.PaginationUtil;
import io.github.jhipster.web.util.ResponseUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import sn.free.selfcare.helper.CSVImportFileHelper;
import sn.free.selfcare.service.EmployeService;
import sn.free.selfcare.service.LigneService;
import sn.free.selfcare.service.dto.*;
import sn.free.selfcare.service.mapper.ActivateDeactivateServiceDTOMapper;
import sn.free.selfcare.web.rest.errors.BadRequestAlertException;

import javax.annotation.PostConstruct;
import javax.validation.Valid;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.*;
import java.util.stream.Collectors;

/**
 * REST controller for managing {@link sn.free.selfcare.domain.Employe}.
 */
@RestController
@RequestMapping("/api")
public class EmployeResource {

    private static final String ENTITY_NAME = "assetMicroserviceEmploye";
    private final Logger log = LoggerFactory.getLogger(EmployeResource.class);
    private final EmployeService employeService;
    private final RestTemplate restTemplate;

    @Autowired
    LigneService ligneService;

    @Autowired
    ActivateDeactivateServiceDTOMapper activateDeactivateRequestMapper;

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    @Value("${services.activateDeactivate.url}")
    private String activateDeactivateServiceUrl;

    public EmployeResource(EmployeService employeService, @Qualifier("restTemplate") RestTemplate restTemplate) {
        this.employeService = employeService;
        this.restTemplate = restTemplate;
    }

    @PostConstruct
    public void init() {
        log.info("Init -> Start");

        log.info("activateDeactivateServiceUrl = {}", activateDeactivateServiceUrl);

        log.info("Init -> End");
    }

    /**
     * {@code POST  /employes} : Create a new employe.
     *
     * @param employeDTO the employeDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new employeDTO, or with status {@code 400 (Bad Request)} if the employe has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/employes")
    public ResponseEntity<EmployeDTO> createEmploye(@Valid @RequestBody EmployeDTO employeDTO) throws URISyntaxException {
        log.debug("REST request to save Employe : {}", employeDTO);
        if (employeDTO.getId() != null) {
            throw new BadRequestAlertException("A new employe cannot already have an ID", ENTITY_NAME, "idexists");
        }

        checkFields(employeDTO);

        if (employeDTO.getLigne() != null) {
            Optional<LigneDTO> optionalLigneDTO = ligneService.findOne(employeDTO.getLigneId());

            if (optionalLigneDTO.isPresent()) {
                LigneDTO ligneDTO = optionalLigneDTO.get();

                Set<ServiceDTO> finalActivatedServices = new HashSet<>();
                Set<ServiceDTO> finalDeactivatedServices = new HashSet<>();
                activateDesactivateServices(employeDTO.getAdServicesRequest(), finalActivatedServices, finalDeactivatedServices);

                if (finalActivatedServices.size() > 0) {
                    ligneDTO.setServices(finalActivatedServices);
                    ligneService.save(ligneDTO);
                }/* else {
                    if (finalDeactivatedServices.size() == employeDTO.getAdServicesRequest().size()) {
                        ligneDTO.setServices(finalActivatedServices);
                        ligneService.save(ligneDTO);
                    }
                }*/
            }
        }

        EmployeDTO result = employeService.save(employeDTO);

        return ResponseEntity.created(new URI("/api/employes/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

     //TODO : MD : Audit : Plus de 40 lignes de codes 
    /**
     * {@code PUT  /employes} : Updates an existing employe.
     *
     * @param employeDTO the employeDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated employeDTO,
     * or with status {@code 400 (Bad Request)} if the employeDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the employeDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/employes")
    public ResponseEntity<EmployeDTO> updateEmploye(@Valid @RequestBody EmployeDTO employeDTO) throws URISyntaxException {
        log.debug("REST request to update Employe : {}", employeDTO);
        if (employeDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }

        checkFields(employeDTO);

        if (employeDTO.getLigne() != null) {
            Optional<LigneDTO> optionalLigneDTO = ligneService.findOne(employeDTO.getLigneId());

            if (optionalLigneDTO.isPresent()) {
                LigneDTO ligneDTO = optionalLigneDTO.get();

                Set<ServiceDTO> finalActivatedServices = new HashSet<>();
                Set<ServiceDTO> finalDeactivatedServices = new HashSet<>();
                activateDesactivateServices(employeDTO.getAdServicesRequest(), finalActivatedServices, finalDeactivatedServices);

                Set<ServiceDTO> servicesToActivate = new HashSet<>();
                if(ligneDTO.getServices() != null) {
                    servicesToActivate.addAll(ligneDTO.getServices());
                }

                if (finalActivatedServices.size() > 0) {
                    servicesToActivate.addAll(finalActivatedServices);
                }

                if (finalDeactivatedServices.size() > 0) {
                    finalDeactivatedServices.stream().forEach(s -> {
                        servicesToActivate.remove(s);
                    });
                }

                ligneDTO.setServices(servicesToActivate);

                ligneService.save(ligneDTO);
            }
        }

        EmployeDTO result = employeService.save(employeDTO);

        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, employeDTO.getId().toString()))
            .body(result);
    }

    /**
     * {@code GET  /employes} : get all the employes.
     *
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of employes in body.
     */
    @GetMapping("/employes")
    public List<EmployeDTO> getAllEmployes() {
        log.debug("REST request to get all Employes");
        return employeService.findAll();
    }

    /**
     * {@code GET  /employes/:id} : get the "id" employe.
     *
     * @param id the id of the employeDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the employeDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/employes/{id}")
    public ResponseEntity<EmployeDTO> getEmploye(@PathVariable Long id) {
        log.debug("REST request to get Employe : {}", id);
        Optional<EmployeDTO> employeDTO = employeService.findOne(id);
        return ResponseUtil.wrapOrNotFound(employeDTO);
    }

    /**
     * {@code DELETE  /employes/:id} : delete the "id" employe.
     *
     * @param id the id of the employeDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/employes/{id}")
    public ResponseEntity<Void> deleteEmploye(@PathVariable Long id) {
        log.debug("REST request to delete Employe : {}", id);
        employeService.delete(id);
        return ResponseEntity.noContent().headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString())).build();
    }

    /**
     * Search by filtering active employees by client
     *
     * @param clientId clientId of employe to be searched
     * @param query    the query of the employe search
     * @param pageable
     * @return the result of the search.
     */
    @GetMapping("/client/{clientId}/employes")
    public ResponseEntity<List<EmployeDTO>> searchEmployes(@PathVariable Long clientId, @RequestParam(required = false) String query, Pageable pageable) {
        log.debug("REST request to findAllByClient Employes for clientId {} and query {}", clientId.toString(), query);
        Page<EmployeDTO> page = employeService.searchEmployes(clientId, query, pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    @GetMapping("/client/{clientId}/all/employes")
    public ResponseEntity<List<EmployeDTO>> searchEmployes(@PathVariable Long clientId) {
        log.debug("REST request to findAllByClient Employes for clientId {} and query {}", clientId.toString());
        List<EmployeDTO> employes = employeService.findAllByClientId(clientId);
        return ResponseEntity.ok().body(employes);
    }

    @GetMapping("/employes/groupe/{groupeId}")
    public List<EmployeDTO> findEmployesWithOffreByGroupe(@PathVariable Long groupeId) {
        log.debug("REST request to find Employes with Offre by groupe: {}", groupeId);
        return employeService.findEmployesWithOffreByGroupe(groupeId);
    }

    @PostMapping("/{clientId}/employes/import")
    public ResponseEntity<Void> importEmployes(@PathVariable("clientId") Long clientId, @RequestParam("file") MultipartFile file) {
        log.debug("REST request to import Employes from CSV file : {} with clientId = {}", file.getOriginalFilename(), clientId);
        if (CSVImportFileHelper.hasCSVFormat(file)) {
            try {
                employeService.saveEmployesFromCSV(clientId, file);
                return ResponseEntity.noContent()
                    .headers(HeaderUtil.createAlert(applicationName, applicationName + "." + ENTITY_NAME + ".fileImported", file.getOriginalFilename()))
                    .build();
            } catch (IOException e) {
                throw new BadRequestAlertException("Fail to parse CSV file: " + e.getMessage(), "fileImport", "fileImport.failure.parse");
            }
        } else {
            throw new BadRequestAlertException("Please upload a csv file: " + file.getOriginalFilename() + "!", "fileImport", "fileImport.failure.badFormat");
        }
    }

    private void checkFields(EmployeDTO employeDTO) {
        if (employeDTO.getLigneId() == null) {
            throw new BadRequestAlertException("Employee's line is required", ENTITY_NAME, ENTITY_NAME + ".lignenull");
        }
        if (employeDTO.getGroupeId() == null) {
            throw new BadRequestAlertException("Employee's group is required", ENTITY_NAME, ENTITY_NAME + ".groupnull");
        }
    }

    private void activateDesactivateServices(List<ActivateDeactivateServiceDTO> services, Set<ServiceDTO> finalActivatedServices, Set<ServiceDTO> finalDeactivatedServices) {
        log.debug("Calling activate/deactivate services -> Start");
        log.debug("Number of services to process: {}", services.size());
        services.parallelStream().forEach(service -> {
            ActivateDeactivateServiceRequestDTO request = activateDeactivateRequestMapper.toRequestDTO(service);
            log.debug("{} Request: {}", request.getServiceName(), request);
            HttpEntity<ActivateDeactivateServiceRequestDTO> requestEntity = new HttpEntity<ActivateDeactivateServiceRequestDTO>(request, new HttpHeaders());
            try {
                Map body = restTemplate.postForEntity(activateDeactivateServiceUrl, requestEntity, Map.class).getBody();
                log.debug("{} Response: {}", request.getServiceName(), body);
                if (body != null) {
                    Map activateDesactivateService = (Map) body.get("ActivateDesactivateService");
                    if (activateDesactivateService != null) {
                        ActivateDeactivateServiceResponseDTO response = new ActivateDeactivateServiceResponseDTO();
                        response.setRespCode((Integer) activateDesactivateService.get("succesCode"));
                        response.setRespDesc((String) activateDesactivateService.get("description"));
                        if (request.getCode() == 1 && response.getRespCode() != null && response.getRespCode() == 200) {
                            finalActivatedServices.add(service.getService());
                        }
                        if (request.getCode() == 0 && (response.getRespCode() == null || response.getRespCode() != 200)) {
                            finalActivatedServices.add(service.getService());
                        }
                        if (request.getCode() == 0 && response.getRespCode() != null && response.getRespCode() == 200) {
                            finalDeactivatedServices.add(service.getService());
                        }
                    }
                }
            } catch (Exception e) {
                log.debug("An Execption occured for the request: {}", e.getMessage());
              //TODO : MD : Audit : Mettre des loggueurs et non des printStackTrace
                e.printStackTrace();
            }
        });
        log.debug("Calling activate/deactivate services -> End");
    }
}
