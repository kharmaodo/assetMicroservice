package sn.free.selfcare.web.rest;

import io.github.jhipster.web.util.HeaderUtil;
import io.github.jhipster.web.util.PaginationUtil;
import io.github.jhipster.web.util.ResponseUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import sn.free.selfcare.service.ServiceService;
import sn.free.selfcare.service.dto.ServiceDTO;
import sn.free.selfcare.web.rest.errors.BadRequestAlertException;

import javax.validation.Valid;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Optional;

/**
 * REST controller for managing {@link sn.free.selfcare.domain.Service}.
 */
@RestController
@RequestMapping("/api")
public class ServiceResource {

    private static final String ENTITY_NAME = "assetMicroserviceService";
    private final Logger log = LoggerFactory.getLogger(ServiceResource.class);
    private final ServiceService serviceService;
    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    public ServiceResource(ServiceService serviceService) {
        this.serviceService = serviceService;
    }

    /**
     * {@code POST  /services} : Create a new service.
     *
     * @param serviceDTO the serviceDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new serviceDTO, or with status {@code 400 (Bad Request)} if the service has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/services")
    public ResponseEntity<ServiceDTO> createService(@Valid @RequestBody ServiceDTO serviceDTO) throws URISyntaxException {
        log.debug("REST request to save Service : {}", serviceDTO);
        if (serviceDTO.getId() != null) {
            throw new BadRequestAlertException("A new service cannot already have an ID", ENTITY_NAME, "idexists");
        }
        ServiceDTO result = serviceService.save(serviceDTO);
        return ResponseEntity.created(new URI("/api/services/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    /**
     * {@code PUT  /services} : Updates an existing service.
     *
     * @param serviceDTO the serviceDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated serviceDTO,
     * or with status {@code 400 (Bad Request)} if the serviceDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the serviceDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/services")
    public ResponseEntity<ServiceDTO> updateService(@Valid @RequestBody ServiceDTO serviceDTO) throws URISyntaxException {
        log.debug("REST request to update Service : {}", serviceDTO);
        if (serviceDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        ServiceDTO result = serviceService.save(serviceDTO);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, serviceDTO.getId().toString()))
            .body(result);
    }

    /**
     * {@code GET  /services} : get all the services.
     *
     * @param pageable the pagination information.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of services in body.
     */
    @GetMapping("/services")
    public ResponseEntity<List<ServiceDTO>> getAllServices(Pageable pageable) {
        log.debug("REST request to get a page of Services");
        Page<ServiceDTO> page = serviceService.findAll(pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    /**
     * {@code GET  /services/:id} : get the "id" service.
     *
     * @param id the id of the serviceDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the serviceDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/services/{id}")
    public ResponseEntity<ServiceDTO> getService(@PathVariable Long id) {
        log.debug("REST request to get Service : {}", id);
        Optional<ServiceDTO> serviceDTO = serviceService.findOne(id);
        return ResponseUtil.wrapOrNotFound(serviceDTO);
    }

    /**
     * {@code DELETE  /services/:id} : delete the "id" service.
     *
     * @param id the id of the serviceDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/services/{id}")
    public ResponseEntity<Void> deleteService(@PathVariable Long id) {
        log.debug("REST request to delete Service : {}", id);
        serviceService.delete(id);
        return ResponseEntity.noContent().headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString())).build();
    }

    /**
     * {@code SEARCH  /_search/services?query=:query} : search for the service corresponding
     * to the query.
     *
     * @param query    the query of the service search.
     * @param pageable the pagination information.
     * @return the result of the search.
     */
    @GetMapping("/_search/services")
    public ResponseEntity<List<ServiceDTO>> searchServices(@RequestParam String query, Pageable pageable) {
        log.debug("REST request to search for a page of Services for query {}", query);
        Page<ServiceDTO> page = serviceService.search(query, pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    @GetMapping("/services/all")
    public List<ServiceDTO> getAllServices() {
        log.debug("REST request to get a page of Services");
        List<ServiceDTO> serviceDTOS = serviceService.findAll();
        return serviceDTOS;
    }
}
